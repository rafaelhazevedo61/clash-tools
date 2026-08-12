package br.com.clash.cwlexporter.service;

import br.com.clash.cwlexporter.entity.LeagueHistoryEntity;
import br.com.clash.cwlexporter.entity.PlayerDayDataEntity;
import br.com.clash.cwlexporter.entity.PlayerHistoryEntity;
import br.com.clash.cwlexporter.model.DayData;
import br.com.clash.cwlexporter.model.PlayerData;
import br.com.clash.cwlexporter.repository.LeagueHistoryRepository;
import br.com.clash.cwlexporter.repository.PlayerHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeagueHistoryService {

    private final LeagueHistoryRepository leagueHistoryRepository;
    private final PlayerHistoryRepository playerHistoryRepository;

    @Transactional
    public LeagueHistoryEntity save(String clanTag, String clanName, String season, String filePath, List<PlayerData> players) {
        clanTag = normalizeTag(clanTag);
        LeagueHistoryEntity league = new LeagueHistoryEntity();
        league.setClanTag(clanTag);
        league.setClanName(clanName);
        league.setSeason(season);
        league.setFilePath(filePath);

        for (PlayerData playerData : players) {
            PlayerHistoryEntity player = new PlayerHistoryEntity();
            player.setPlayerTag(playerData.getTag());
            player.setPlayerName(playerData.getName());
            player.setTotalAttackStars(playerData.getTotalAttackStars());
            player.setTotalDefenseStars(playerData.getTotalDefenseStars());
            player.setTotalStars(playerData.getTotalStars());
            player.setLeague(league);

            addDay(player, 1, playerData.getWar1());
            addDay(player, 2, playerData.getWar2());
            addDay(player, 3, playerData.getWar3());
            addDay(player, 4, playerData.getWar4());
            addDay(player, 5, playerData.getWar5());
            addDay(player, 6, playerData.getWar6());
            addDay(player, 7, playerData.getWar7());

            league.getPlayers().add(player);
        }

        return leagueHistoryRepository.save(league);
    }

    private void addDay(PlayerHistoryEntity player, int day, DayData dayData) {
        if (dayData == null) {
            return;
        }
        PlayerDayDataEntity entity = new PlayerDayDataEntity();
        entity.setDay(day);
        entity.setAttackStars(dayData.getAttackStars());
        entity.setDefenseStars(dayData.getDefenseStars());
        entity.setPlayer(player);
        player.getDays().add(entity);
    }

    @Transactional(readOnly = true)
    public List<LeagueHistoryEntity> findAll() {
        return leagueHistoryRepository.findAllByOrderByGeneratedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<LeagueHistoryEntity> findById(Long id) {
        return leagueHistoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<LeagueHistoryEntity> findByClan(String clanTag, String season) {
        clanTag = normalizeTag(clanTag);
        if (season != null && !season.isBlank()) {
            return leagueHistoryRepository.findByClanTagAndSeasonOrderByGeneratedAtDesc(clanTag, season);
        }
        return leagueHistoryRepository.findByClanTagOrderByGeneratedAtDesc(clanTag);
    }

    @Transactional(readOnly = true)
    public List<PlayerHistoryEntity> findPlayersByLeague(Long leagueId) {
        return playerHistoryRepository.findByLeagueIdOrderByTotalStarsDesc(leagueId);
    }

    @Transactional
    public long clearAll() {
        long count = leagueHistoryRepository.count();
        leagueHistoryRepository.deleteAllHistories();
        return count;
    }

    private String normalizeTag(String tag) {
        if (tag == null) {
            return null;
        }
        return tag.replace("%23", "#").trim();
    }
}
