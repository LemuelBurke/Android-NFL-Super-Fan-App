package com.example.mob_dev_portfolio

import FunFactWorker
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mob_dev_portfolio.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWelsh.setOnClickListener {
            setLanguageAndRestart("cy")
        }

        binding.btnEnglish.setOnClickListener {
            setLanguageAndRestart("en")
        }

        binding.btnGetStarted.setOnClickListener(){
            val intent = Intent(this, TeamSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }

        updateButtonStates()
    }

    private fun updateButtonStates() {
        val currentLang = LocaleHelper.getPersistedLanguage(this)
        binding.btnWelsh.isSelected = currentLang == "cy"
        binding.btnEnglish.isSelected = currentLang == "en"
    }

    private fun setLanguageAndRestart(languageCode: String) {
        // Save language preference
        LocaleHelper.setAppLocale(this, languageCode)
        lifecycleScope.launch {
            delay(300) // Small delay for visual feedback
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }


}