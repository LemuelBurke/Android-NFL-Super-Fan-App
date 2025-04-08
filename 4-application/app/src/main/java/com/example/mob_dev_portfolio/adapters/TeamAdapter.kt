package com.example.mob_dev_portfolio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_portfolio.databinding.ItemTeamBinding
import com.example.mob_dev_portfolio.models.NFLTeam

class TeamAdapter(private var teams: List<NFLTeam>,
    private val onTeamSelected: (NFLTeam) -> Unit) :
    RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(val binding: ItemTeamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TeamViewHolder(binding)
    }

    // create team items
    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teams[position]
        val binding = holder.binding

        // Set background color
        binding.teamLayout.setCardBackgroundColor(Color.parseColor(team.colour1))

        // Set team logo
        val context = holder.itemView.context
        val logoResId = context.resources.getIdentifier(
            "logo_${team.shortName}", "drawable", context.packageName
        )
        binding.teamLogo.setImageResource(logoResId)

        // Set team name
//        binding.teamName.text = team.name

        //Click listneer
        holder.itemView.setOnClickListener{
            onTeamSelected(team)
        }
    }

    // update recycle view
    fun updateTeams(newTeams: List<NFLTeam>) {
        teams = newTeams
        notifyDataSetChanged()
    }

    override fun getItemCount() = teams.size
}