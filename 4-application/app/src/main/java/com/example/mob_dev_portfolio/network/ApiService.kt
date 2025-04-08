package com.example.mob_dev_portfolio.network

import GameResponse
import com.example.mob_dev_portfolio.models.PlayerResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
// api service
interface ApiService {
    @GET("players")
    fun getPlayers(
        @Header("x-rapidapi-key") apiKey: String, // dynamically pass api key
        @Header("x-rapidapi-host") host: String, // dynamically pass end-address
        @Query("season") season: Int, // add a query for a season
        @Query("team") teamId: Int // add a query f a teamID
    ): Call<PlayerResponse> //Call the player response retrofitt

    @GET("games")
    fun getGames(
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String,
        @Query("season") season: Int,
        @Query("team") teamId: Int
    ): Call<GameResponse>

}
