import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mob_dev_portfolio.MainActivity
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.models.FunFact
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class FunFactWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val facts = loadFacts(applicationContext)
        val randomFact = facts.random()

        showNotification(randomFact)
        return Result.success()
    }

    private fun loadFacts(context: Context): List<FunFact> {
        val appLanguage = LocaleHelper.getPersistedLanguage(context)

        // Choose the correct JSON file based on language
        val jsonFileName = if (appLanguage == "cy") {
            "cy_fun_facts.json"
        } else {
            "fun_facts.json" // Default to English
        }

        val json = context.assets.open(jsonFileName).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<FunFact>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun showNotification(fact: FunFact) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "fun_fact_channel"

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Fun NFL Facts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val largeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.logo_logo)
        val smallIcon = R.drawable.football
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("🏈 ${fact.title}")
            .setContentText("Fact #${fact.id}" + fact.fact)
            .setLargeIcon(largeIcon)
            .setSmallIcon(smallIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fact.fact))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(fact.id, notification)
    }
}
