/*
 * Copyright 2017-2024 Koki Fukuda
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

import android.util.Log

class DebugLogger private constructor() {
    companion object {
        val logger =
                if (BuildConfig.DEBUG) {
                    DebugLogger()
                } else {
                    null
                }
    }

    fun print(tag: String, message: String) {
        Log.d(tag, message)
    }
}
