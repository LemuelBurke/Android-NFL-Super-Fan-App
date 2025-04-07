package com.example.mob_dev_portfolio

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.adapters.UpcomingGamesAdapter
import com.example.mob_dev_portfolio.models.UpcomingGame
import com.example.mob_dev_portfolio.databinding.ActivityStatisticsBinding
import com.example.mob_dev_portfolio.models.NFLTeam
import com.google.gson.Gson
import org.json.JSONArray
import java.io.InputStream
import java.io.InputStreamReader

class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.upcomingGamesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.upcomingGamesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        val games = loadUpcomingGames()

        val adapter = UpcomingGamesAdapter(games)
        binding.upcomingGamesRecyclerView.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadUpcomingGames(): List<UpcomingGame> {
        val inputStream: InputStream = assets.open("mock_upcoming_games.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        val teams = loadNFLTeams()  // Load the NFL teams
        val games = mutableListOf<UpcomingGame>()
        for (i in 0 until jsonArray.length()) {
            val gameJson = jsonArray.getJSONObject(i)
            val homeId = gameJson.getInt("homeId")
            val awayId = gameJson.getInt("awayId")

            val homeTeam = teams.first { it.id == homeId }
            val awayTeam = teams.first { it.id == awayId }

            val game = UpcomingGame(
                gameID = gameJson.getString("gameID"),
                homeId = homeId,
                awayId = awayId,
                location = gameJson.getString("location"),
                date = gameJson.getString("date"),
                time = gameJson.getString("time"),
                homeTeamName = homeTeam.name,
                awayTeamName = awayTeam.name,
                homeTeamShortName = homeTeam.shortName, // Add these fields
                awayTeamShortName = awayTeam.shortName
            )
            games.add(game)
        }
        return games
    }

    private fun loadNFLTeams(): List<NFLTeam> {
        val inputStream: InputStream = assets.open("nfl_teams.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        val teams = mutableListOf<NFLTeam>()
        for (i in 0 until jsonArray.length()) {
            val teamJson = jsonArray.getJSONObject(i)
            val team = NFLTeam(
                id = teamJson.getInt("id"),
                name = teamJson.getString("name"),
                shortName = teamJson.getString("shortName"),
                colour1 = teamJson.getString("colour1"),
                colour2 = teamJson.getString("colour2"),
                conference = teamJson.getString("conference"),
                apiId = teamJson.getInt("apiId")
            )
            teams.add(team)
        }
        return teams
    }


}
