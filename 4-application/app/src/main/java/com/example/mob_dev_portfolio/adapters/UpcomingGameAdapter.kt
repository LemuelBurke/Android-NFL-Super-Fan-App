package com.example.mob_dev_portfolio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.ItemUpcomingGameBinding
import com.example.mob_dev_portfolio.models.UpcomingGame

class UpcomingGamesAdapter(
    private val games: List<UpcomingGame>,
    private val calendarListener: CalendarEventListener
) : RecyclerView.Adapter<UpcomingGamesAdapter.GameViewHolder>() {

    inner class GameViewHolder(val binding: ItemUpcomingGameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(game: UpcomingGame) {
            val context = binding.root.context

            // Set team names
            binding.awayTeam.text = game.awayTeamName
            binding.homeTeam.text = game.homeTeamName

            // Dynamically load away team logo from local drawables using the short name
            val awayLogoResId = context.resources.getIdentifier(
                "logo_${game.awayTeamShortName.toLowerCase().replace(" ", "")}",
                "drawable",
                context.packageName
            )
            if (awayLogoResId != 0) {
                binding.awayLogo.setImageResource(awayLogoResId)
            } else {
                binding.awayLogo.setImageResource(R.drawable.logo_logo)
            }

            // Dynamically load home team logo from local drawables using the short name
            val homeLogoResId = context.resources.getIdentifier(
                "logo_${game.homeTeamShortName.toLowerCase().replace(" ", "")}",
                "drawable",
                context.packageName
            )
            if (homeLogoResId != 0) {
                binding.homeLogo.setImageResource(homeLogoResId)
            } else {
                binding.homeLogo.setImageResource(R.drawable.logo_logo)
            }

            // Set other game info
            binding.gameId.text = game.gameID
            binding.gameDate.text = game.date + " ${game.time} UTC"
            binding.venue.text = game.location
            binding.gameStartTime.text = "${game.time} UTC"
            binding.gameStatus.text = "@"
            binding.addToCalendarButton.setOnClickListener {
              calendarListener.onCalendarButtonClicked(game)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemUpcomingGameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game)
    }

    override fun getItemCount(): Int = games.size
    interface CalendarEventListener {
        fun onCalendarButtonClicked(game: UpcomingGame)
    }



}
