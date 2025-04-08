// data class for past games
// used to retrofit the api response
data class GameResponse(
    val get: String,
    val parameters: Parameters,
    val errors: Any?=null,
    val results: Int,
    val response: List<Game>
)

data class Parameters(
    val season: String,
    val team: String
)

data class Game(
    val game: GameInfo,
    val league: League,
    val teams: Teams,
    val scores: Scores
)

data class GameInfo(
    val id: Int,
    val stage: String,
    val week: String,
    val date: GameDate,
    val venue: Venue,
    val status: GameStatus
)

data class GameDate(
    val timezone: String,
    val date: String,
    val time: String,
    val timestamp: Long
)

data class Venue(
    val name: String,
    val city: String
)

data class GameStatus(
    val short: String,
    val long: String,
    val timer: Any?
)

data class League(
    val id: Int,
    val name: String,
    val season: String,
    val logo: String,
    val country: Country
)

data class Country(
    val name: String,
    val code: String,
    val flag: String
)

data class Teams(
    val home: Team,
    val away: Team
)

data class Team(
    val id: Int,
    val name: String,
    val logo: String
)

data class Scores(
    val home: Score,
    val away: Score
)

data class Score(
    val quarter_1: Int,
    val quarter_2: Int,
    val quarter_3: Int,
    val quarter_4: Int,
    val overtime: Any?,
    val total: Int
)