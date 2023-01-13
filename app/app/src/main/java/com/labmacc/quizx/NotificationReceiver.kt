package com.labmacc.quizx

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.labmacc.quizx.data.LoginRepository

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "NotificationRec"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Triggered")
        NotificationService.pollNotifications(context)
    }
}
