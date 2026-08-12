package br.com.clash.cwlexporter.service;

import br.com.clash.cwlexporter.config.ClashProperties;
import br.com.clash.cwlexporter.model.*;
import br.com.clash.cwlexporter.utils.ExcelGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClanWarLeagueExportServiceTest {

    @Mock
    HttpClient httpClient;
    @Mock
    ObjectMapper objectMapper;

    @Test
    void buildPlayerData_calculatesStarsCorrectly() {
        ClashProperties properties = new ClashProperties();
        properties.setWeights(new ClashProperties.Weights());
        properties.getWeights().setAttackStars(1.0);
        properties.getWeights().setDefenseStars(0.5);
        properties.setOutput(new ClashProperties.Output());

        ClanWarLeagueExportService service = new ClanWarLeagueExportService(properties, httpClient, objectMapper, new ExcelGenerator());

        List<ClanWarLeagueWarMembers> day1 = List.of(
                new ClanWarLeagueWarMembers("#tag1", "Player1", 12, 1,
                        List.of(new ClanWarLeagueWarAttacks("#tag1", "#opp", 2, 80, 1, 100)),
                        0,
                        new ClanWarLeagueWarBestOpponentAttack("#opp", "#tag1", 1, 60, 1, 90))
        );

        Set<String> uniqueTags = Set.of("#tag1");
        List<PlayerData> result = service.buildPlayerData(uniqueTags, List.of(day1));

        assertEquals(1, result.size());
        assertEquals("Player1", result.get(0).getName());
        assertEquals(2, result.get(0).getTotalAttackStars());
        assertEquals(1.0, result.get(0).getTotalDefenseStars());
        assertEquals(3.0, result.get(0).getTotalStars());
    }
}
