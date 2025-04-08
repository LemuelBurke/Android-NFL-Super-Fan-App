package com.example.mob_dev_portfolio

import GameAlarmReceiver
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_portfolio.adapters.NFLFactsAdapter
import com.example.mob_dev_portfolio.adapters.UpcomingGamesAdapter
import com.example.mob_dev_portfolio.databinding.ActivityStatisticsBinding
import com.example.mob_dev_portfolio.models.FunFact
import com.example.mob_dev_portfolio.models.NFLTeam
import com.example.mob_dev_portfolio.models.UpcomingGame
import com.example.mob_dev_portfolio.network.NetworkUtils
import org.json.JSONArray
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class StatisticsActivity : AppCompatActivity(), UpcomingGamesAdapter.CalendarEventListener {

    private val ALARM_PERMISSION_REQUEST_CODE = 101
    private val EXACT_ALARM_SETTINGS_REQUEST_CODE = 102
    private val NOTIFICATION_PERMISSION_CODE = 103
    private var gamePendingAlarm: UpcomingGame? = null
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Recycler view setup for upcoming games
        binding.upcomingGamesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val games = loadUpcomingGames()
        val adapter = UpcomingGamesAdapter(games, this)
        binding.upcomingGamesRecyclerView.adapter = adapter

        // NFL Facts RecyclerView setup
        val factsRecyclerView = binding.nflFactsRecyclerView
        factsRecyclerView.layoutManager = LinearLayoutManager(this)

        val facts = loadNFLFacts()
        val factsAdapter = NFLFactsAdapter(this, facts)
        factsRecyclerView.adapter = factsAdapter

        binding.bottomNavigationView.selectedItemId = R.id.statistics
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.news -> {
                    val intent = Intent(this, WebViewActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.statistics -> {
                    // Already on this screen
                    true
                }

                else -> false
            }
        }

        binding.gameFilterButton.setOnClickListener {
            filterContent("game")
        }
        binding.playerFilterButton.setOnClickListener {
            filterContent("player")
        }
        binding.teamFilterButton.setOnClickListener {
            filterContent("team")
        }
        binding.draftFilterButton.setOnClickListener {
            filterContent("draft")
        }

        binding.allFilterButton.setOnClickListener{
            filterContent("all")
        }

        checkInternetConnection()
    }


    private fun checkInternetConnection() {
        if (!NetworkUtils.isInternetConnected(this)) {
            showNoInternetDialog()
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

    private fun filterContent(filterType: String) {
        try {
            // Load all facts from the method
            val allFacts = loadNFLFacts()

            // Get the current language code (Welsh or English)
            val languageCode = sharedPreferences.getString("app_language", "en") ?: "en"

            // Modify the filterType based on the language (Welsh or English)
            val translatedFilterType = when (languageCode) {
                "cy" -> { // If the language is Welsh, use the Welsh terms
                    when (filterType.lowercase()) {
                        "game" -> "Gêm"
                        "player" -> "Chwaraewr"
                        "team" -> "Tîm"
                        "draft" -> "Drafft"
                        else -> filterType // Default fallback
                    }
                }
                else -> filterType // Default to English filter types
            }

            // Filter the facts based on the translated filter type
            val filteredFacts = if (translatedFilterType == "all") {
                allFacts
            } else {
                allFacts.filter { it.typeOfFact.equals(translatedFilterType, ignoreCase = true) }
            }

            // Access the adapter and update it with the filtered facts
            val adapter = (findViewById<RecyclerView>(R.id.nflFactsRecyclerView).adapter as NFLFactsAdapter)
            adapter.updateList(filteredFacts)

            // Show a toast indicating the filter applied
            val filterName = translatedFilterType.capitalize(Locale.getDefault())
            Toast.makeText(this, "$filterName facts loaded", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("StatisticsActivity", "Error filtering content: ${e.message}")
            Toast.makeText(this, "Error filtering content", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCalendarButtonClicked(game: UpcomingGame) {
        // Save the game for later use in case we need to request permissions
        gamePendingAlarm = game

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        // First check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
                return
            }
        }

        // Then check for SET_ALARM permission
        val hasAlarmPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.SET_ALARM
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasAlarmPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SET_ALARM),
                ALARM_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Finally check if we can schedule exact alarms (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Need to request permission from settings
                Toast.makeText(
                    this,
                    "Please grant permission to schedule exact alarms",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivityForResult(intent, EXACT_ALARM_SETTINGS_REQUEST_CODE)
                return
            }
        }

        // If we have all necessary permissions, schedule the alarm
        gamePendingAlarm?.let { game ->
            scheduleGameAlarm(game)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EXACT_ALARM_SETTINGS_REQUEST_CODE) {
            // After returning from settings, check all permissions again
            checkAndRequestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Log permissions result for debugging
        Log.d(
            "PermissionResult",
            "RequestCode: $requestCode, Permissions: ${permissions.joinToString()}, GrantResults: ${grantResults.joinToString()}"
        )

        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            val permissionName = when (requestCode) {
                NOTIFICATION_PERMISSION_CODE -> "notification"
                ALARM_PERMISSION_REQUEST_CODE -> "alarm"
                else -> "required"
            }
            Toast.makeText(this, "$permissionName permission denied", Toast.LENGTH_SHORT).show()
            return
        }

        // Handle granted permissions
        when (requestCode) {
            NOTIFICATION_PERMISSION_CODE -> {
                // Notification permission granted, check next permission
                checkAndRequestPermissions()
            }

            ALARM_PERMISSION_REQUEST_CODE -> {
                // Alarm permission granted, check next permission
                checkAndRequestPermissions()
            }
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
                homeTeamShortName = homeTeam.shortName,
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

    private fun scheduleGameAlarm(game: UpcomingGame) {
        try {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val gameStartDate = inputFormat.parse("${game.date} ${game.time}") ?: Date()
            val gameStartMillis = gameStartDate.time

            val intent = Intent(this, GameAlarmReceiver::class.java).apply {
                putExtra("gameTitle", "NFL: ${game.awayTeamName} @ ${game.homeTeamName}")
                putExtra("gameTime", gameStartMillis)
            }

            // Generate a unique request code using the game ID's hash
            val requestCode = game.gameID.hashCode()

            // Check if an alarm already exists for this specific game
            val existingPendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (existingPendingIntent != null) {
                Toast.makeText(this, "Alarm already exists for this game", Toast.LENGTH_SHORT).show()
                return
            }

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, gameStartMillis, pendingIntent)

            Toast.makeText(this, "Game alarm set!", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Cannot set alarm: Missing permission to schedule exact alarms", Toast.LENGTH_LONG).show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivityForResult(settingsIntent, EXACT_ALARM_SETTINGS_REQUEST_CODE)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error setting alarm: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNFLFacts(): List<FunFact> {
        val languageCode = sharedPreferences.getString("app_language", "en") ?: "en"
        val fileName = if (languageCode == "cy") "cy_fun_facts.json" else "fun_facts.json"

        return try {
            assets.open(fileName).use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                JSONArray(jsonString).let { jsonArray ->
                    List(jsonArray.length()) { i ->
                        val factJson = jsonArray.getJSONObject(i)
                        FunFact(
                            id = factJson.getInt("id"),
                            title = factJson.getString("title"),
                            fact = factJson.getString("fact"),
                            typeOfFact = factJson.getString("typeOfFact")
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}