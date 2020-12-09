package br.com.murilocorreiab.sleepermanager.entrypoint

import br.com.murilocorreiab.sleepermanager.dataprovider.player.db.PlayerRepository
import br.com.murilocorreiab.sleepermanager.entrypoint.client.UpdatePlayerClient
import br.com.murilocorreiab.sleepermanager.util.Wiremock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import io.micronaut.http.HttpStatus.OK
import io.micronaut.http.MediaType
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest()
class UpdatePlayerEntrypointTest {

    @Inject
    private lateinit var updatePlayerClient: UpdatePlayerClient

    @Inject
    private lateinit var playerRepository: PlayerRepository

    @Inject
    private lateinit var wireMock: Wiremock

    @BeforeEach
    fun cleanDatabase() {
        playerRepository.deleteAll()
    }

    @Test
    fun `should update with success`() = runBlocking {
        // Given
        val playersId = listOf("96", "2133", "4866", "4199")

        // When
        stubFor(
            get(urlEqualTo("/players/nfl"))
                .willReturn(
                    aResponse().withHeader(
                        "content-type",
                        MediaType.APPLICATION_JSON
                    ).withBodyFile("players_response.json")
                )
        )

        val response = updatePlayerClient.update()

        // Then
        val playersInDb = playerRepository.findAll().map { it.id }
        assertEquals(OK, response.status)
        assertEquals(playersId, playersInDb)
    }
}
