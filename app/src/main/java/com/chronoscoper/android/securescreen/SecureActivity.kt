/*
 * Copyright 2017 KoFuk
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
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class SecureActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SecureActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        if (savedInstanceState == null) {
            startLockTask()
        }
    }


    private fun isInLockTask(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return activityManager.isInLockTaskMode
        } else {
            return activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
        }
    }

    override fun onBackPressed() {
        if (isInLockTask()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG,"Activity is in lock task mode. unlocking...")
            }
            stopLockTask()
        }
        finishAndRemoveTask()
    }

    override fun onPause() {
        super.onPause()
        finishAndRemoveTask()
        if (BuildConfig.DEBUG){
            Log.d(TAG,"Secure Screen finished since onPause called")
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
