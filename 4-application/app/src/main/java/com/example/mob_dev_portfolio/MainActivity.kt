package com.example.mob_dev_portfolio

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_portfolio.databinding.ActivityMainBinding
import com.example.mob_dev_portfolio.models.Player
import com.example.mob_dev_portfolio.models.PlayerResponse
import com.example.mob_dev_portfolio.network.RetrofitClient
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var offensiveAdapter: PlayerAdapter
    private lateinit var defensiveAdapter: PlayerAdapter
    private var selectedSeason = "2023" // Default season

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // SharedPreferences to load the user's favorite team
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val favoriteTeam = sharedPreferences.getString("favorite_team", null)

        // Set up RecyclerView
        binding.offensiveRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.defensiveRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Set Home as selected on menu
        binding.bottomNavigationView.selectedItemId = R.id.home
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Already on home screen
                    true
                }

                R.id.news -> {
                    val intent = Intent(this, WebViewActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // If no favorite team is found, go to TeamSelectionActivity
        if (favoriteTeam == null) {
            startActivity(Intent(this, TeamSelectionActivity::class.java))
            finish()
        } else {
            setupSeasonSpinner()
            toMainApp()
        }

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupSeasonSpinner() {
        val seasons = listOf("2021", "2022", "2023")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, seasons)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.seasonSpinner.adapter = adapter

        // Set default selection to 2023
        val defaultIndex = seasons.indexOf("2023")
        binding.seasonSpinner.setSelection(defaultIndex)  // Ensures 2023 is selected on launch

        binding.seasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSeason = seasons[position] // Update selectedSeason
                val apiId = sharedPreferences.getString("favourite_team_apiId", null)?.toIntOrNull()
                if (apiId != null) {
                    fetchPlayers(apiId) // Fetch players with new season
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun fetchPlayers(apiId: Int) {
        val apiKey = "ef4fa7259fecbfc5a063a3f379259983"
        val apiHost = "v1.american-football.api-sports.io"

        RetrofitClient.apiService.getPlayers(apiKey, apiHost, selectedSeason.toInt(), apiId)
            .enqueue(object : Callback<PlayerResponse> {
                override fun onResponse(call: Call<PlayerResponse>, response: Response<PlayerResponse>) {
                    if (response.isSuccessful) {
                        val playerResponse = response.body()
                        if (playerResponse != null && playerResponse.response != null) {
                            val players = playerResponse.response
                            updateRecyclerViews(players)
                        } else {
                            Log.e("API_ERROR", "API response is empty or null")
                        }
                    } else {
                        Log.e("API_ERROR", "Response failed: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PlayerResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to fetch players: ${t.message}")
                }
            })
    }

    private fun updateRecyclerViews(players: List<Player>) {
        val offensivePlayers = players.filter { it.position in listOf("QB", "RB", "WR", "TE", "OL") }
        val defensivePlayers = players.filter { it.position in listOf("CB", "LB", "S", "DL", "DE") }

        offensiveAdapter = PlayerAdapter(offensivePlayers)
        defensiveAdapter = PlayerAdapter(defensivePlayers)

        binding.offensiveRecyclerView.adapter = offensiveAdapter
        binding.defensiveRecyclerView.adapter = defensiveAdapter
    }

    private fun toMainApp() {
        val favoriteTeamName = sharedPreferences.getString("favorite_team", "Unknown Team")
        val favoriteTeamShort = sharedPreferences.getString("favorite_team_short", "unknown")
        val apiId = sharedPreferences.getString("favourite_team_apiId", null)?.toIntOrNull()

        if (apiId != null) {
            fetchPlayers(apiId)
        } else {
            Toast.makeText(this, "No API ID found!", Toast.LENGTH_SHORT).show()
        }

        binding.textView.text = favoriteTeamName?.toUpperCase()

        val logoResId = resources.getIdentifier(
            "logo_${favoriteTeamShort?.toLowerCase()}", "drawable", packageName
        )

        binding.imageView.setImageResource(if (logoResId != 0) logoResId else R.drawable.logo_logo)
    }
}
