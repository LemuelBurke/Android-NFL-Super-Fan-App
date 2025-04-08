package com.example.mob_dev_portfolio.models

data class NFLTeam(
    val id: Int,
    val name: String,
    val shortName: String,
    val colour1: String,
    val colour2: String,
    val conference: String,
    val apiId: Int
)