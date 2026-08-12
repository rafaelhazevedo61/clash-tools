package br.com.clash.cwlexporter.model;

public record ClanWarLeagueWarRegistry(
        String state,
        int teamSize,
        String preparationStartTime,
        String startTime,
        String endTime,
        String battleModifier,
        Integer attacksPerMember,
        ClanWarLeagueWarClan clan,
        ClanWarLeagueWarClan opponent,
        String warStartTime
) {
}
