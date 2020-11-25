package br.com.murilocorreiab.sleepermanager.dataprovider.roster

import br.com.murilocorreiab.sleepermanager.dataprovider.league.entity.LeagueResponseProducer
import br.com.murilocorreiab.sleepermanager.dataprovider.league.entity.UserResponseProducer
import br.com.murilocorreiab.sleepermanager.dataprovider.league.http.LeagueClient
import br.com.murilocorreiab.sleepermanager.dataprovider.league.http.UserClient
import br.com.murilocorreiab.sleepermanager.dataprovider.player.entity.PlayerResponseProducer
import br.com.murilocorreiab.sleepermanager.dataprovider.player.http.PlayerClient
import br.com.murilocorreiab.sleepermanager.dataprovider.player.http.PlayerResponse
import br.com.murilocorreiab.sleepermanager.dataprovider.roster.entity.RosterResponseProducer
import br.com.murilocorreiab.sleepermanager.dataprovider.roster.http.RosterClient
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.inject.Inject

@MicronautTest
class RosterGatewayHttpClientTest {

    @Inject
    private lateinit var target: RosterGatewayHttpClient

    @get:MockBean(UserClient::class)
    val userClient = mockk<UserClient>()

    @get:MockBean(LeagueClient::class)
    val leagueClient = mockk<LeagueClient>()

    @get:MockBean(RosterClient::class)
    val rosterClient = mockk<RosterClient>()

    @get:MockBean(PlayerClient::class)
    val playerClient = mockk<PlayerClient>()

    private val username = "username"
    private val starterPlayerId = "starterPlayerId"
    private val benchPlayerId = "benchPlayerId"
    private val userResponse = UserResponseProducer().build()
    private val leagueResponse = LeagueResponseProducer().build()
    private val rosterResponse = RosterResponseProducer(
        starters = listOf(starterPlayerId),
        players = listOf(starterPlayerId, benchPlayerId),
        ownerId = userResponse.userId
    ).build()
    private val rosterOfAnotherPlayer = RosterResponseProducer(ownerId = "otherPlayer").build()
    private val starterPlayerResponse =
        PlayerResponseProducer(playerId = starterPlayerId, fullName = starterPlayerId).build()
    private val benchPlayerResponse =
        PlayerResponseProducer(playerId = benchPlayerId, fullName = benchPlayerId).build()
    private val playerOutOfTheRosterResponse = PlayerResponseProducer(playerId = "outOfTheRoster").build()

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Test
    fun `should get rosters for user with success`() = runBlockingTest {
        // When
        val players = listOf(
            starterPlayerResponse,
            benchPlayerResponse,
            playerOutOfTheRosterResponse
        )
        arrangeToDoFullFlow(players)

        val actual = target.findUserRostersInLeagues(username)

        // Then
        assertTrue(actual.toList().isNotEmpty())
        actual.collect {
            assertEquals(rosterResponse.rosterId, it.id)
            assertEquals(leagueResponse.leagueId, it.league.id)
            assertEquals(userResponse.userId, it.ownerId)
            assertTrue(it.players.first { player -> player.id == starterPlayerId }.starter)
            assertFalse(it.players.first { player -> player.id == benchPlayerId }.starter)
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    @Test
    fun `if roster has no official players return empty flow`() = runBlockingTest {
        // When
        arrangeToDoFullFlow(emptyList())

        val actual = target.findUserRostersInLeagues(username)

        // Then
        assertTrue(actual.toList().isEmpty())
    }

    private fun arrangeToDoFullFlow(players: List<PlayerResponse>) {
        every { userClient.getByUsername(username) }.returns(Mono.just(userResponse))
        every { leagueClient.getByUserId(userResponse.userId) }.returns(Flux.just(leagueResponse))
        every { rosterClient.getRostersOfALeague(leagueResponse.leagueId) }.returns(
            Flux.just(
                rosterResponse,
                rosterOfAnotherPlayer
            )
        )
        val playersById = players.associateBy({ it.playerId }, { it })
        every { playerClient.getAllPlayers() }.returns(playersById)
    }
}