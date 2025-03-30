package com.example.mob_dev_portfolio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mob_dev_portfolio.databinding.ItemPlayerBinding
import com.example.mob_dev_portfolio.models.Player

class PlayerAdapter(private val players: List<Player>) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        with(holder.binding) {
            playerName.text = player.name
            playerNumber.text = player.number?.toString() ?: "N/A"
            playerPosition.text = player.position

            Glide.with(playerImage.context)
                .load(player.image)
                .into(playerImage)
        }
    }

    override fun getItemCount() = players.size
}
