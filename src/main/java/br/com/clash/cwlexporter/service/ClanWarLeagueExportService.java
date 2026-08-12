package br.com.clash.cwlexporter.service;

import br.com.clash.cwlexporter.config.ClashProperties;
import br.com.clash.cwlexporter.model.*;
import br.com.clash.cwlexporter.utils.ExcelGenerator;
import br.com.clash.cwlexporter.utils.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClanWarLeagueExportService {

    private final ClashProperties clashProperties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExcelGenerator excelGenerator;
    private final LeagueHistoryService leagueHistoryService;

    public ExportResult exportLeagueFile() throws IOException, InterruptedException {
        log.info("Iniciando exportação do arquivo da liga de guerras...");

        LocalDate hoje = LocalDate.now();
        String nomeMes = hoje.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        String ano = String.valueOf(hoje.getYear());

        String fileName = String.format("%s_%s%s.xlsx", clashProperties.getOutput().getFilenamePrefix(), ano, nomeMes);

        try (Workbook workbook = new XSSFWorkbook()) {
            List<CompletableFuture<ClanExportData>> futures = clashProperties.getClans().stream()
                    .map(clan -> CompletableFuture.supplyAsync(() -> processClan(clan)))
                    .toList();

            List<ClanExportData> results = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();

            for (ClanExportData data : results) {
                excelGenerator.generatePlayerDataExcel(data.playerData, workbook, data.clanName);
                leagueHistoryService.save(data.clanTag, data.clanName, data.season, null, data.playerData);
            }

            if (workbook.getNumberOfSheets() == 0) {
                var sheet = workbook.createSheet("Sem dados");
                var row = sheet.createRow(0);
                row.createCell(0).setCellValue("Nenhuma informação de liga foi gerada. Verifique os logs da API.");
            }

            try (var out = new ByteArrayOutputStream()) {
                workbook.write(out);
                log.info("Excel mensal gerado com sucesso: {}", fileName);
                return new ExportResult(out.toByteArray(), fileName);
            }
        }
    }

    private ClanExportData processClan(Clan clan) {
        log.info("Processando clã: {} | Tag: {}", clan.getNome(), clan.getTag());

        try {
            List<ClanWarLeagueWarRegistry> registros = fetchWarRegistries(clan.getTag());
            if (registros.isEmpty()) {
                log.warn("CLASH-TOOLS-LOG:::::: NENHUMA GUERRA ENCONTRADA PARA CLÃ: {} | Tag: {}", clan.getNome(), clan.getTag());
                return null;
            }

            List<ClanWarLeagueWarClan> clans = extractClans(clan.getNome(), registros);
            List<List<ClanWarLeagueWarMembers>> membersByDay = extractMembers(clans);
            Set<String> uniqueTags = collectUniqueTags(membersByDay);

            log.info("Total de membros únicos na liga: {} | Tag: {}", uniqueTags.size(), clan.getTag());

            List<PlayerData> playerDataList = buildPlayerData(uniqueTags, membersByDay);
            String season = parseSeason(registros.get(0).endTime());
            String clanName = clan.getNome() != null && !clan.getNome().isBlank() ? clan.getNome() : clan.getTag();
            return new ClanExportData(clan.getTag(), clanName, season, playerDataList);
        } catch (Exception e) {
            log.error("CLASH-TOOLS-LOG:::::: ERRO AO PROCESSAR CLÃ: {} | Tag: {}", clan.getNome(), clan.getTag(), e);
            return null;
        }
    }

    private List<ClanWarLeagueWarRegistry> fetchWarRegistries(String tag) throws IOException, InterruptedException {
        List<ClanWarLeagueWarRegistry> warRegistries = new ArrayList<>();
        for (int day = 1; day <= 7; day++) {
            ClanWarLeagueWarRegistry registry = fetchWarRegistryForDay(tag, day);
            if (registry != null) {
                warRegistries.add(registry);
            }
        }
        if (warRegistries.isEmpty()) {
            log.warn("CLASH-TOOLS-LOG:::::: NENHUM REGISTRO DE GUERRA ENCONTRADO (tag: {})", tag);
        }
        return warRegistries;
    }

    private ClanWarLeagueWarRegistry fetchWarRegistryForDay(String tag, int day) throws IOException, InterruptedException {
        ClanWarLeagueGroup group = fetchLeagueGroup(tag);
        if (group == null || group.rounds() == null || group.rounds().size() < day) {
            return null;
        }

        List<String> warTags = group.rounds().get(day - 1).warTags();
        for (String warTag : warTags) {
            String url = buildUrl(clashProperties.getWarEndpoint(), warTag);
            HttpRequest request = HttpUtil.createRequest(url, clashProperties.getApi().getBearerToken());
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                for (Clan clan : clashProperties.getClans()) {
                    if (body.contains(clan.getNome())) {
                        return objectMapper.readValue(body, ClanWarLeagueWarRegistry.class);
                    }
                }
            } else {
                log.warn("Erro ao buscar guerra {} para {}: {} - {}", day, tag, response.statusCode(), response.body());
            }
        }
        return null;
    }

    private ClanWarLeagueGroup fetchLeagueGroup(String tag) throws IOException, InterruptedException {
        String url = buildUrl(clashProperties.getLeagueGroupEndpoint(), tag);
        HttpRequest request = HttpUtil.createRequest(url, clashProperties.getApi().getBearerToken());
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), ClanWarLeagueGroup.class);
        }
        if (response.statusCode() == 404) {
            log.warn("CLASH-TOOLS-LOG:::::: REGISTROS PARA CLÃ NÃO ENCONTRADO (tag: {})", tag);
            return null;
        }
        log.warn("Erro ao buscar liga para {}: {}", tag, response.statusCode());
        return null;
    }

    private String buildUrl(String template, String tag) {
        return template.replace("{tag}", tag.replace("#", "%23"));
    }

    private List<ClanWarLeagueWarClan> extractClans(String clanName, List<ClanWarLeagueWarRegistry> registries) {
        return registries.stream()
                .map(r -> r.clan().name().equals(clanName) ? r.clan() : r.opponent())
                .filter(Objects::nonNull)
                .toList();
    }

    private List<List<ClanWarLeagueWarMembers>> extractMembers(List<ClanWarLeagueWarClan> clans) {
        return clans.stream()
                .map(ClanWarLeagueWarClan::members)
                .filter(Objects::nonNull)
                .toList();
    }

    private Set<String> collectUniqueTags(List<List<ClanWarLeagueWarMembers>> membersByDay) {
        Set<String> uniqueTags = new HashSet<>();
        for (List<ClanWarLeagueWarMembers> dayMembers : membersByDay) {
            for (ClanWarLeagueWarMembers member : dayMembers) {
                uniqueTags.add(member.tag());
            }
        }
        return uniqueTags;
    }

    List<PlayerData> buildPlayerData(Set<String> uniqueTags, List<List<ClanWarLeagueWarMembers>> membersByDay) {
        List<PlayerData> playerDataList = new ArrayList<>();
        double attackWeight = clashProperties.getWeights().getAttackStars();
        double defenseWeight = clashProperties.getWeights().getDefenseStars();

        for (String tag : uniqueTags) {
            Map<Integer, DayData> warData = new HashMap<>();
            String name = null;

            for (int day = 0; day < membersByDay.size(); day++) {
                for (ClanWarLeagueWarMembers member : membersByDay.get(day)) {
                    if (member.tag().equals(tag)) {
                        name = member.name();
                        int attackStars = member.attacks() != null && !member.attacks().isEmpty()
                                ? (int) (member.attacks().get(0).stars() * attackWeight)
                                : 0;
                        double defenseStars = member.bestOpponentAttack() != null
                                ? ((3 - member.bestOpponentAttack().stars()) * defenseWeight)
                                : defenseWeight;
                        warData.put(day + 1, new DayData(attackStars, defenseStars));
                    }
                }
            }

            if (name != null) {
                playerDataList.add(new PlayerData(tag, name, warData));
            }
        }

        playerDataList.sort(Comparator.comparingDouble(PlayerData::getTotalStars).reversed()
                .thenComparing(Comparator.comparingInt(PlayerData::getTotalAttackStars).reversed())
                .thenComparing(Comparator.comparingDouble(PlayerData::getTotalDefenseStars).reversed()));

        return playerDataList;
    }

    private record ClanExportData(String clanTag, String clanName, String season, List<PlayerData> playerData) {
    }

    public record ExportResult(byte[] content, String fileName) {
    }

    private String parseSeason(String endTime) {
        if (endTime == null || endTime.length() < 8) {
            return null;
        }
        try {
            String datePart = endTime.substring(0, 8);
            LocalDate date = LocalDate.parse(datePart, DateTimeFormatter.BASIC_ISO_DATE);
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            log.warn("Não foi possível parsear season de endTime: {}", endTime);
            return null;
        }
    }
}
