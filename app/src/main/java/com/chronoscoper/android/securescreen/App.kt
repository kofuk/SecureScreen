/*
 * Copyright 2019-2025 Koki Fukuda
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

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {
    companion object {
        const val NC_DEFAULT = "default"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(
                    NotificationChannel(
                        NC_DEFAULT,
                        getString(R.string.notification_channel_default),
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                        .apply {
                            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                        }
                )
        }
    }
}
