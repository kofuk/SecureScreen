package com.chronoscoper.android.securescreen

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class SecureScreenNotification {
    companion object {
        private var id = 1

        var isActive = false

        fun showNotification(context: Context) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .notify(
                            ++id,
                            NotificationCompat.Builder(context, App.NC_DEFAULT)
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setContentTitle(context.getString(R.string.notification_title))
                                    .setContentText(context.getString(
                                            R.string.notification_message))
                                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                    .setColor(ContextCompat.getColor(
                                            context, R.color.colorPrimary))
                                    .apply {
                                        setContentIntent(PendingIntent.getActivity(context, 1,
                                                Intent(context, SecureActivity::class.java), 0))
                                        setDeleteIntent(PendingIntent.getBroadcast(context, 1,
                                                Intent(context, DeleteReceiver::class.java), 0))
                                    }
                                    .build()
                    )
            isActive = true
        }

        fun delete(context: Context) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .cancel(id)
            isActive = false
        }
    }

    class DeleteReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            SecureScreenNotification.isActive = false
        }

    }
}