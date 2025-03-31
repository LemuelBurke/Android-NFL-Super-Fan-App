package com.example.mob_dev_portfolio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.mob_dev_portfolio.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root) // Use the binding root view

        // Setup WebView
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true

        // Load the NFL news page
        binding.webView.loadUrl("https://www.nfl.com/news/")

        binding.bottomNavigationView.selectedItemId = R.id.news
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    startActivity(intent)
                    true
                }

                R.id.news -> {
                    // Already on this screen
                    true
                }
                else -> false
            }
        }
    }

}
