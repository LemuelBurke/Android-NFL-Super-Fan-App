package com.example.mob_dev_portfolio.models
data class PlayerResponse(
    val response: List<Player>
)

data class Player(
    val id: Int,
    val name: String,
    val position: String,
    val number: Int?,
    val image: String
)

