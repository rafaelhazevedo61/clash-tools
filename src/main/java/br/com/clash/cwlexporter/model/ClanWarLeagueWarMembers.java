package br.com.clash.cwlexporter.model;

import java.util.List;

public record ClanWarLeagueWarMembers(String tag, String name, int townhallLevel, int mapPosition, List<ClanWarLeagueWarAttacks> attacks, int opponentAttacks, ClanWarLeagueWarBestOpponentAttack bestOpponentAttack) {
}
