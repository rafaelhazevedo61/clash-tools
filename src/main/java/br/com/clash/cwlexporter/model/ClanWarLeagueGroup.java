package br.com.clash.cwlexporter.model;

import java.util.List;

public record ClanWarLeagueGroup(String state, String season, List<ClanWarLeagueGroupClan> clans, List<ClanWarLeagueRound> rounds) {
}
