package com.example.mob_dev_portfolio.network

import com.example.mob_dev_portfolio.models.PlayerResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("players")
    fun getPlayers(
        @Header("x-rapidapi-key") apiKey: String, // 🔑 Add headers dynamically
        @Header("x-rapidapi-host") host: String,
        @Query("season") season: Int,
        @Query("team") teamId: Int
    ): Call<PlayerResponse>
}
