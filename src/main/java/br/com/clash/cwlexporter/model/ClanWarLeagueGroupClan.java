package br.com.clash.cwlexporter.model;

import java.util.List;

public record ClanWarLeagueGroupClan(String tag, String name, int clanLevel, BadgeUrls badgeUrls, List<ClanWarLeagueGroupMember> members) {
}
