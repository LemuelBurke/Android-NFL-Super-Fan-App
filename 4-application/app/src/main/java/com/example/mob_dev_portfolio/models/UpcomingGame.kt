package com.example.mob_dev_portfolio.models

data class UpcomingGame(
    val gameID: String,
    val homeId: Int,
    val awayId: Int,
    val location: String,
    val date: String,
    val time: String,
    var homeTeamName: String,
    var awayTeamName: String,
    var homeTeamShortName: String,
    var awayTeamShortName: String
)

