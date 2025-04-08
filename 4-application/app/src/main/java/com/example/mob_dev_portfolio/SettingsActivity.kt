package com.example.mob_dev_portfolio

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.mob_dev_portfolio.databinding.ActivitySettingsBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        applySavedTheme()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }
        binding.btnWelsh.setOnClickListener {
            setLanguageAndRestart("cy")
        }
        binding.btnEnglish.setOnClickListener {
            setLanguageAndRestart("en")
        }

        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        val favoriteTeamShort = sharedPreferences.getString("favorite_team_short", "unknown")
        val logoResId = resources.getIdentifier(
            "logo_${favoriteTeamShort?.lowercase(Locale.getDefault())}", "drawable", packageName
        )
        binding.imageFavTeam.setImageResource(if (logoResId != 0) logoResId else R.drawable.logo_logo)

        binding.buttonChangeFavTeam.setOnClickListener(){
            val intent = Intent(this, TeamSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun applySavedTheme() {
        val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setLanguageAndRestart(languageCode: String) {
        LocaleHelper.setAppLocale(this, languageCode)
        lifecycleScope.launch {
            delay(300) // Small delay for visual feedback
            startActivity(Intent(this@SettingsActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }
}