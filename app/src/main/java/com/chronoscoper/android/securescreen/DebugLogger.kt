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
