package com.example.mob_dev_portfolio.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_portfolio.databinding.ItemFactBinding
import com.example.mob_dev_portfolio.models.FunFact
import com.example.mob_dev_portfolio.models.NFLTeam

class NFLFactsAdapter(private val context: Context, private var factsList: List<FunFact>) :
    RecyclerView.Adapter<NFLFactsAdapter.FactViewHolder>() {

    private val factTypeColors = mapOf(
        "Game" to Color.parseColor("#3F51B5"),
        "Player" to Color.parseColor("#4CAF50"),
        "Team" to Color.parseColor("#FF9800"),
        "Draft" to Color.parseColor("#9C27B0"),
        "Play" to Color.parseColor("#F44336"),
        "Gêm" to Color.parseColor("#3F51B5"),
        "Chwaraewr" to Color.parseColor("#4CAF50"),
        "Tîm" to Color.parseColor("#FF9800"),
        "Chwarae" to Color.parseColor("#F44336"),
        "Drafft" to Color.parseColor("#9C27B0")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val binding = ItemFactBinding.inflate(LayoutInflater.from(context), parent, false)
        return FactViewHolder(binding)
    }

    fun updateList(filteredFacts: List<FunFact>) {
        this.factsList = filteredFacts
        notifyDataSetChanged()  // Notify RecyclerView to update the list
    }

    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        val fact = factsList[position]

        // Bind the views using ViewBinding
        holder.binding.tvFactTitle.text = fact.title
        holder.binding.tvFactContent.text = fact.fact
        holder.binding.tvFactType.text = fact.typeOfFact

        // Set background color based on fact type
        factTypeColors[fact.typeOfFact]?.let { color ->
            holder.binding.tvFactType.background.setTint(color)
        }
    }

    override fun getItemCount(): Int = factsList.size

    class FactViewHolder(val binding: ItemFactBinding) : RecyclerView.ViewHolder(binding.root)
}
