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

import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.chronoscoper.android.securescreen.databinding.ActivitySecureBinding

class SecureActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SecureActivity"
    }

    private lateinit var binding: ActivitySecureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecureBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
        setContentView(binding.root)

        if (savedInstanceState == null) {
            startLockTask()
            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("finish_button", true)
            )
                findViewById<View>(R.id.finish)
                    .setOnClickListener {
                        stopLockTask()
                        finishAndRemoveTask()
                    }
            else
                binding.finish.visibility = View.GONE
            //TODO: Detect if screen pinning is approved
            val container = findViewById<View>(android.R.id.content)
                .apply {
                    isClickable = true
                    isFocusable = true
                    isFocusableInTouchMode = true
                }
            container.setOnClickListener { v ->
                v.setOnClickListener(null)
                Handler(Looper.getMainLooper()).let {
                    it.post(object : Runnable {
                        override fun run() {
                            if (!isInLockTask()) {
                                finishAndRemoveTask()
                                return
                            }
                            it.postDelayed(this, 500)
                        }
                    })
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!PreferenceManager.getDefaultSharedPreferences(this@SecureActivity)
                        .getBoolean("finish_on_back_pressed", false)
                ) {
                    return
                }
                if (isInLockTask()) {
                    if (BuildConfig.DEBUG) {
                        DebugLogger.logger?.print(
                            TAG,
                            "Activity is in lock task mode. unlocking..."
                        )
                    }
                    stopLockTask()
                }
                finishAndRemoveTask()
            }
        })
    }

    private fun isInLockTask(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
    }

    override fun onPause() {
        super.onPause()
        finishAndRemoveTask()
        DebugLogger.logger?.print(
            TAG,
            "Finishing Secure Screen since activity is no longer displayed"
        )
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
