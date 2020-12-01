package br.com.murilocorreiab.sleepermanager.entrypoint

import br.com.murilocorreiab.sleepermanager.domain.player.usecase.GetPlayersInWaiver
import br.com.murilocorreiab.sleepermanager.entrypoint.model.PlayersWaiverResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

@Controller("/players")
class PlayerEntrypoint(private val getPlayersInWaiver: GetPlayersInWaiver) {

    @Get("/user/{username}/waiver{?players}")
    fun getPlayersInWaiverByLeague(
        @PathVariable username: String,
        @QueryValue players: String?
    ): HttpResponse<Flow<PlayersWaiverResponse>> = runBlocking {
        players?.let {
            val namesToSearch = getNamesToSearch(it)
            if (namesToSearch.isNotEmpty()) {
                getPlayersInWaiver.get(username, namesToSearch).map { (player, leagues) ->
                    PlayersWaiverResponse(player, leagues.toList())
                }.let { response -> HttpResponse.ok(response) }
            } else {
                HttpResponse.notFound()
            }
        } ?: HttpResponse.notFound()
    }

    private fun getNamesToSearch(playersQuery: String): List<String> =
        playersQuery.split(",").filter { it.isNotBlank() }
}