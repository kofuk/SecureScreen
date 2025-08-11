package com.chronoscoper.android.securescreen

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class PinningTileService : TileService() {
    override fun onClick() {
        super.onClick()

        startActivity(Intent(this, SecureActivity::class.java).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        })
    }
}