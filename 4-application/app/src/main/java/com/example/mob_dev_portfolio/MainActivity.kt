package com.example.mob_dev_portfolio

import Game
import GameAdapter
import GameResponse
import PlayerAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.databinding.ActivityMainBinding
import com.example.mob_dev_portfolio.models.Player
import com.example.mob_dev_portfolio.models.PlayerResponse
import com.example.mob_dev_portfolio.network.NetworkUtils
import com.example.mob_dev_portfolio.network.RetrofitClient
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var offensiveAdapter: PlayerAdapter
    private lateinit var defensiveAdapter: PlayerAdapter
    private var selectedSeason = "2023" // Default season
    private lateinit var gameAdapter: GameAdapter
    private var gamesList: MutableList<Game> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applyLanguageSettings()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameAdapter = GameAdapter(emptyList())
        binding.pastGamesRecyclerView.adapter = gameAdapter

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // SharedPreferences to load the favorite team
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val favoriteTeam = sharedPreferences.getString("favorite_team", null)

        // Set up RecyclerView
        binding.offensiveRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.defensiveRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.pastGamesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Set Home as selected on menu
        binding.bottomNavigationView.selectedItemId = R.id.home
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    //  on home screen
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

        // If no favorite team is found go to TeamSelectionActivity
        if (favoriteTeam == null) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        } else {
            setupSeasonSpinner()
            toMainApp()
        }

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        checkInternetConnection()
    }

    // when the activity is resumed select the home btn
    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.selectedItemId = R.id.home
    }

    private fun checkInternetConnection() {
        if (!NetworkUtils.isInternetConnected(this)) {
            showNoInternetDialog()
        } else {
            toMainApp()
        }
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please connect to the internet to use this app.")
            .setPositiveButton("Retry") { _, _ ->
                recreate()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    // setup season spinner
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
                val apiId = sharedPreferences.getInt("favourite_team_apiId", 1)
                if (apiId != null) {
                    fetchPlayers(apiId) // Fetch players with new season
                    fetchGames(apiId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // fetch players from API
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
                            updatePlayerRecyclerViews(players)
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

    private fun fetchGames(apiId: Int) {
        val apiKey = "ef4fa7259fecbfc5a063a3f379259983"
        val apiHost = "v1.american-football.api-sports.io"

        RetrofitClient.apiService.getGames(apiKey, apiHost, selectedSeason.toInt(), apiId)
            .enqueue(object : Callback<GameResponse> {
                override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { gameResponse ->
                            updateGameRecyclerView(gameResponse.response)
                        }
                    } else {
                        Log.e("API_ERROR", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failure: ${t.message}")
                }
            })
    }

    override fun attachBaseContext(newBase: Context) {
        // Apply the saved language before the activity attaches to its context
        val context = LocaleHelper.applyLocale(newBase)
        super.attachBaseContext(context)
    }

    private fun applyLanguageSettings() {
        val languageCode = LocaleHelper.getPersistedLanguage(this)
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageCode)
        )
    }

    private fun updateGameRecyclerView(games: List<Game>) {
        gameAdapter = GameAdapter(games)
        binding.pastGamesRecyclerView.apply {
            adapter = gameAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    // update player recycle views
    private fun updatePlayerRecyclerViews(players: List<Player>) {
        val offensivePlayers = players.filter { it.position in listOf("QB", "RB", "WR", "TE", "OL") }
        val defensivePlayers = players.filter { it.position in listOf("CB", "LB", "S", "DL", "DE") }

        offensiveAdapter = PlayerAdapter(offensivePlayers)
        defensiveAdapter = PlayerAdapter(defensivePlayers)

        binding.offensiveRecyclerView.apply {
            adapter = PlayerAdapter(offensivePlayers)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.defensiveRecyclerView.apply {
            adapter = PlayerAdapter(defensivePlayers)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    // main activity func called after fav team selected
    private fun toMainApp() {
        val favoriteTeamName = sharedPreferences.getString("favorite_team", "Unknown Team")
        val favoriteTeamShort = sharedPreferences.getString("favorite_team_short", "unknown")
        val apiId = sharedPreferences.getInt("favourite_team_apiId", 1)

        if (apiId != null) {
            fetchPlayers(apiId)
            fetchGames(apiId)
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
