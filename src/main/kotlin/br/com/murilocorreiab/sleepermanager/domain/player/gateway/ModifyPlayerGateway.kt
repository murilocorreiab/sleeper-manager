package br.com.murilocorreiab.sleepermanager.domain.player.gateway

import br.com.murilocorreiab.sleepermanager.domain.player.entity.Player

interface ModifyPlayerGateway {
    suspend fun updatePlayers(players: List<Player>): List<Player>
}
