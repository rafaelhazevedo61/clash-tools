package br.com.clash.cwlexporter.controller;

import br.com.clash.cwlexporter.service.ClanWarLeagueExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/league")
@RequiredArgsConstructor
public class LeagueExportController {

    private final ClanWarLeagueExportService exportService;

    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportLeagueFile() {
        try {
            ClanWarLeagueExportService.ExportResult result = exportService.exportLeagueFile();
            ByteArrayResource resource = new ByteArrayResource(result.content());
            log.info("CLASH-TOOLS-LOG:::::: ARQUIVO GERADO COM SUCESSO: {}", result.fileName());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.fileName() + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (IOException | InterruptedException e) {
            log.error("CLASH-TOOLS-LOG:::::: ERRO AO GERAR ARQUIVO", e);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao exportar arquivo da liga: " + e.getMessage(), e);
        }
    }
}
