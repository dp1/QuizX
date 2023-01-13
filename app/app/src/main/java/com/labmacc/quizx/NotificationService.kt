package com.labmacc.quizx

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import com.labmacc.quizx.data.ApiDataSource
import com.labmacc.quizx.data.FirebaseAuthDataSource
import kotlinx.coroutines.runBlocking

class NotificationService : JobIntentService() {
    companion object {
        @JvmStatic
        fun pollNotifications(context: Context) {
            val intent = Intent(context, NotificationService::class.java).apply {
                action = ACTION_POLL
            }
            enqueueWork(context, NotificationService::class.java, 1, intent)
        }
        const val ACTION_POLL = "com.labmacc.quizx.action.POLL_NOTIFICATIONS"
        const val TAG = "NotificationService"
    }

    private val auth = FirebaseAuthDataSource()
    private val api = ApiDataSource()

    override fun onHandleWork(intent: Intent) {
        Log.i(TAG, "Handling intent with action ${intent.action}")
        when (intent.action) {
            ACTION_POLL -> {
                doPoll()
            }
        }
    }

    private fun doPoll() {
        auth.restoreLogin()?.let { user ->
            Log.i(TAG, "Restored login for user ${user.uuid}")
            if (runBlocking { api.hasNewQuizzes(user.uuid) }) {
                sendNotification()
            }
        }
    }

    private fun sendNotification() {
        val channelId = "Notifications"
        val channelName = "com.labmacc.quizx"

        val target = Intent(this, RankingActivity::class.java)
        target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, target, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setContentTitle("New quiz")
            .setContentText("You just received a new quiz to solve!")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, builder.build())
        Log.i(TAG, "Notification sent!")
    }
}