package com.example.mob_dev_portfolio.models
// upcoming games data class to pass values from JSON
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

