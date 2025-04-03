package com.example.mob_dev_portfolio.network

import GameResponse
import com.example.mob_dev_portfolio.models.PlayerResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("players")
    fun getPlayers(
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") host: String,
        @Query("season") season: Int,
        @Query("team") teamId: Int
    ): Call<PlayerResponse>

    @GET("games")
    fun getGames(
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String,
        @Query("season") season: Int,
        @Query("team") teamId: Int
    ): Call<GameResponse>

}
