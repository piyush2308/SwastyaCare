package com.nitkkr.swastyacare.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.nitkkr.swastyacare.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val medicine = intent.getStringExtra("medicine") ?: "Medicine"

        val manager  = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (required API 26+)
        val channel  = NotificationChannel(
            "reminder_channel",
            "Medicine Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for medicine reminders"
        }
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setContentTitle("💊 Medicine Reminder")
            .setContentText("Time to take $medicine")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}