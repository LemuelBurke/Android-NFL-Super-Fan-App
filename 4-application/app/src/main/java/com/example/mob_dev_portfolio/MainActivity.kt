package com.example.mob_dev_portfolio

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
    private lateinit var binding: ActivityMainBinding // Declare the binding object
    private lateinit var offensiveAdapter: PlayerAdapter
    private lateinit var defensiveAdapter: PlayerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the binding object
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Use the root view from the binding object

        // Handle edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets -> // Change `binding.main` to `binding.root`
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // SharedPreferences to load the user's favorite team
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val favoriteTeam = sharedPreferences.getString("favorite_team", null)

        // Set up RecyclerView with LinearLayoutManager
        binding.offensiveRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.defensiveRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // If no favorite team is found, go to TeamSelectionActivity
        if (favoriteTeam == null) {
            val intent = Intent(this, TeamSelectionActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            toMainApp()
        }

        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchPlayers(apiId: Int) {
        val apiKey = "ef4fa7259fecbfc5a063a3f379259983"
        val apiHost = "v1.american-football.api-sports.io"

        RetrofitClient.apiService.getPlayers(apiKey, apiHost, 2023, apiId)
            .enqueue(object : Callback<PlayerResponse> {
                override fun onResponse(call: Call<PlayerResponse>, response: Response<PlayerResponse>) {
                    if (response.isSuccessful) {
                        val playerResponse = response.body()

                        if (playerResponse != null && playerResponse.response != null) {
                            val players = playerResponse.response

                            // Sort players into offensive and defensive lists
                            val offensivePlayers = mutableListOf<Player>()
                            val defensivePlayers = mutableListOf<Player>()

                            for (player in players) {
                                // Check if position is null before accessing
                                if (player.position != null) {
                                    when (player.position) {
                                        "QB", "RB", "WR", "TE", "OL" -> offensivePlayers.add(player)
                                        "CB", "LB", "S", "DL", "DE" -> defensivePlayers.add(player)
                                        else -> Log.e("API_ERROR", "Unrecognized position: ${player.position}")
                                    }
                                } else {
                                    Log.e("API_ERROR", "Player position is null: ${player.name}")
                                }
                            }

                            // Assign the lists to adapters
                            offensiveAdapter = PlayerAdapter(offensivePlayers)
                            defensiveAdapter = PlayerAdapter(defensivePlayers)

                            binding.offensiveRecyclerView.adapter = offensiveAdapter
                            binding.defensiveRecyclerView.adapter = defensiveAdapter

                            // Disable nested scrolling for smooth scrolling
                            binding.offensiveRecyclerView.isNestedScrollingEnabled = false
                            binding.defensiveRecyclerView.isNestedScrollingEnabled = false
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                recreate()
                true
            }
            R.id.news -> {
                recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toMainApp() {
        // Retrieve the saved team info
        val favoriteTeamName = sharedPreferences.getString("favorite_team", "Unknown Team")
        val favoriteTeamShort = sharedPreferences.getString("favorite_team_short", "unknown")
        val apiId = sharedPreferences.getString("favourite_team_apiId", null)?.toIntOrNull()

        if (apiId != null) {
            fetchPlayers(apiId)
        } else {
            Toast.makeText(this, "No API ID found!", Toast.LENGTH_SHORT).show()
        }

        // Set the team name in the TextView using View Binding
        binding.textView.text = favoriteTeamName?.toUpperCase()

        // Dynamically load the team's logo based on the short name
        val logoResId = resources.getIdentifier(
            "logo_${favoriteTeamShort?.toLowerCase()}", "drawable", packageName
        )

        if (logoResId != 0) {
            // Set the team's logo if the resource ID is found
            binding.imageView.setImageResource(logoResId)
        } else {
            // Fallback logo if no specific logo is found
            binding.imageView.setImageResource(R.drawable.logo_logo) // Use default fallback logo
        }
    }
}
