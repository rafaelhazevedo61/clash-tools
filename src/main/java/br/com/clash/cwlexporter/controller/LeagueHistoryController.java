package br.com.clash.cwlexporter.controller;

import br.com.clash.cwlexporter.entity.LeagueHistoryEntity;
import br.com.clash.cwlexporter.entity.PlayerHistoryEntity;
import br.com.clash.cwlexporter.service.LeagueHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/league/history")
@RequiredArgsConstructor
public class LeagueHistoryController {

    private final LeagueHistoryService leagueHistoryService;

    @GetMapping
    public List<LeagueHistoryEntity> listAll() {
        return leagueHistoryService.findAll();
    }

    @GetMapping("/clan")
    public List<LeagueHistoryEntity> listByClan(@RequestParam String tag, @RequestParam(required = false) String season) {
        return leagueHistoryService.findByClan(tag, season);
    }

    @GetMapping("/{id}/players")
    public List<PlayerHistoryEntity> listPlayers(@PathVariable Long id) {
        return leagueHistoryService.findPlayersByLeague(id);
    }
}
