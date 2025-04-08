import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.ItemGameBinding

class GameAdapter(private val games: List<Game>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_GAME = 1
    }

    inner class GameViewHolder(val binding: ItemGameBinding) : RecyclerView.ViewHolder(binding.root)
    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) // Using regular View instead of binding

    override fun getItemViewType(position: Int): Int {
        return if (games.isEmpty()) TYPE_EMPTY else TYPE_GAME
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_EMPTY) {
            // Inflate your item_no_data layout
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_no_data, parent, false)
            EmptyViewHolder(view)
        } else {
            GameViewHolder(
                ItemGameBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    // Set up game item
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GameViewHolder -> {
                val game = games[position]
                with(holder.binding) {
                    gameId.text = "Game ID: #${game.game.id}"
                    gameDate.text = "${game.game.date.date} ${game.game.date.time} UTC"
                    venue.text = game.game.venue.name
                    homeScore.text = game.scores.home.total.toString()
                    awayScore.text = game.scores.away.total.toString()
                    homeTeam.text = game.teams.home.name
                    awayTeam.text = game.teams.away.name

                    Glide.with(homeLogo.context)
                        .load(game.teams.home.logo)
                        .placeholder(R.drawable.logo_logo)
                        .error(R.drawable.question_mark)
                        .into(homeLogo)

                    Glide.with(awayLogo.context)
                        .load(game.teams.away.logo)
                        .placeholder(R.drawable.logo_logo)
                        .error(R.drawable.question_mark)
                        .into(awayLogo)
                }
            }
        }
    }

    // return items
    override fun getItemCount(): Int {
        return if (games.isEmpty()) 1 else games.size
    }
}