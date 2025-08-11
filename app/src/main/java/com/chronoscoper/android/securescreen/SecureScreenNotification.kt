/*
 * Copyright 2017-2025 Koki Fukuda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chronoscoper.android.securescreen

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class SecureScreenNotification {
    companion object {
        private var id = 1

        var isActive = false

        fun showNotification(context: Context) {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }
            val contentIntent = PendingIntent.getActivity(
                context, 1,
                Intent(context, SecureActivity::class.java), flags
            )
            val deleteIntent = PendingIntent.getBroadcast(
                context, 1,
                Intent(context, DeleteReceiver::class.java), flags
            )

            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .notify(
                    ++id,
                    NotificationCompat.Builder(context, App.NC_DEFAULT)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setContentText(
                            context.getString(
                                R.string.notification_message
                            )
                        )
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setColor(
                            ContextCompat.getColor(
                                context, R.color.colorPrimary
                            )
                        )
                        .setSilent(true)
                        .setContentIntent(contentIntent)
                        .setDeleteIntent(deleteIntent)
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
            isActive = false
        }

    }
}
