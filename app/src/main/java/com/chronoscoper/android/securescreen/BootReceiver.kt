/*
 * Copyright 2017-2019 Koki Fukuda
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
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) =
            if (intent.action == Intent.ACTION_BOOT_COMPLETED
                    && PreferenceManager.getDefaultSharedPreferences(context)
                            .getBoolean("start_on_boot", false))
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                        .notify(
                                1,
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
                                        }
                                        .build()
                        )
            else Unit
}
