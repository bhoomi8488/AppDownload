package com.udacity.Helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.udacity.DetailActivity
import com.udacity.MainActivity
import com.udacity.Model.FileDownload
import com.udacity.R

/**
 * Created by Bhoomi on 4/22/2021.
 */
class NotificationHelper(private val context: Context) {

    private val CHANNEL_ID = "channelId"
    private val NOTIFICATION_ID = 8

    fun sendNotification(downloadedFile: FileDownload) {

        // call cancel notification
        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()

        //create notification channel
        createNotificationChannel()

        //create intent
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // add  action
        val detailIntent = Intent(context, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        detailIntent.putExtra("file_name", downloadedFile.fileName)
        detailIntent.putExtra("status", downloadedFile.status)
        val detailPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            detailIntent,
            0
        )

        // Get an instance of NotificationCompat.Builder
        // Build the notification
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            //  set title, text and icon to builder
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(downloadedFile.fileName)
            //  set content intent
            .setContentIntent(contentPendingIntent)
            // add detail action
            .addAction(
                R.drawable.download,
                context.getString(R.string.notification_button),
                detailPendingIntent
            )
            .setAutoCancel(true)
            // set priority
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        //  call notify
        println("notification send perfact")
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())

    }

    //Create the notification Channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = context.getString(R.string.notification_description)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    /**
     * Cancels all notifications.
     *
     */
    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }

}