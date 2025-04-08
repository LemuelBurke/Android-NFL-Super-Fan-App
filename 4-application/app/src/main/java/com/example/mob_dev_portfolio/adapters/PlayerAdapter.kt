import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.ItemPlayerBinding
import com.example.mob_dev_portfolio.models.Player

class PlayerAdapter(private val players: List<Player>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_PLAYER = 1
    }

    inner class PlayerViewHolder(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root)
    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return if (players.isEmpty()) TYPE_EMPTY else TYPE_PLAYER
    }

    // create viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_EMPTY) {
            EmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_no_data, parent, false)
            )
        } else {
            PlayerViewHolder(
                ItemPlayerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    // create player items which fill recycler
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlayerViewHolder -> {
                val player = players[position]
                with(holder.binding) {
                    playerName.text = player.name
                    playerNumber.text = player.number?.toString() ?: "N/A"
                    playerPosition.text = player.position

                    Glide.with(playerImage.context)
                        .load(player.image)
                        .placeholder(R.drawable.logo_logo)
                        .error(R.drawable.question_mark)
                        .into(playerImage)
                }
            }
        }
    }

    //count items
    override fun getItemCount(): Int {
        return if (players.isEmpty()) 1 else players.size
    }
}