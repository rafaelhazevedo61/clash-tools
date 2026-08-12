package br.com.clash.cwlexporter.model;

import java.util.List;

public record ClanWarLeagueClan(String tag, String name, BadgeUrls badgeUrls, int clanLevel, int attacks, int stars, float destructionPercentage, List<ClanWarLeagueWarMembers> members) {
}
