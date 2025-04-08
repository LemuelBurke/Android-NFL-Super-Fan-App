package com.example.mob_dev_portfolio

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.adapters.TeamAdapter
import com.example.mob_dev_portfolio.databinding.ActivityTeamSelectionBinding
import com.example.mob_dev_portfolio.models.NFLTeam
import org.json.JSONArray
import java.io.InputStream

class TeamSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamSelectionBinding
    private lateinit var adapter: TeamAdapter
    private var allTeams: List<NFLTeam> = emptyList()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        // Load NFL teams from JSON
        allTeams = loadNFLTeams()

        // Set up RecyclerView
        binding.teamsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TeamAdapter(allTeams) { selectedTeam -> saveTeamAndNavigateToMain(selectedTeam)} // Start with all teams
        binding.teamsRecyclerView.adapter = adapter

        // Set up button click listeners
        binding.nfcButton.setOnClickListener {
            filterTeamsByConference("NFC")
        }

        binding.afcButton.setOnClickListener {
            filterTeamsByConference("AFC")
        }
    }

    private fun filterTeamsByConference(conference: String) {
        val filteredTeams = allTeams.filter { it.conference == conference }
        adapter.updateTeams(filteredTeams)
    }

    private fun saveTeamAndNavigateToMain(team: NFLTeam) {
        sharedPreferences.edit().apply {
            putString("favorite_team", team.name)
            putString("favorite_team_short", team.shortName)
            putString("favorite_team_color1", team.colour1)
            putString("favorite_team_color2", team.colour2)
            putInt("favourite_team_apiId", team.apiId)
            apply()
        }

        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Load teams from the JSON file in assets
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