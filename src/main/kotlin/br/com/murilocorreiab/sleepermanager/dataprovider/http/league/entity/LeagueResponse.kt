package br.com.murilocorreiab.sleepermanager.dataprovider.http.league.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class LeagueResponse(
    val name: String,
    @JsonProperty("league_id") val leagueId: Long,
    @JsonProperty("total_rosters") val totalRosters: Int
)