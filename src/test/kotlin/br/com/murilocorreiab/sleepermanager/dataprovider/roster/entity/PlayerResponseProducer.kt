package br.com.murilocorreiab.sleepermanager.dataprovider.roster.entity

import br.com.murilocorreiab.sleepermanager.dataprovider.roster.http.entity.PlayerResponse
import br.com.murilocorreiab.sleepermanager.domain.roster.entity.PlayerStatus

data class PlayerResponseProducer(
    val playerId: String = "playerId",
    val firstName: String = "firstName",
    val lastName: String = "lastName",
    val injuryStatus: String = PlayerStatus.IR.name
) {
    fun build() =
        PlayerResponse(playerId = playerId, firstName = firstName, lastName = lastName, injuryStatus = injuryStatus)
}
