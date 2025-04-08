import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.mob_dev_portfolio.R

class GameAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val gameTitle = intent.getStringExtra("gameTitle") ?: "Game Alert"
        val gameTime = intent.getLongExtra("gameTime", System.currentTimeMillis())

        // Create and display the notification when the alarm goes off
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = gameTime.toInt()

        val notification = NotificationCompat.Builder(context, "game_notifications")
            .setContentTitle(gameTitle)
            .setContentText("The game starts now!")
            .setSmallIcon(R.drawable.football) // Use a proper icon
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
