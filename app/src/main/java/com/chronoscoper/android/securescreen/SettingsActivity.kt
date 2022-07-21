/*
 * Copyright 2017-2022 Koki Fukuda
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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.preference.PreferenceManager
import com.chronoscoper.android.securescreen.databinding.ActivitySettingsBinding
import com.chronoscoper.library.licenseviewer.LicenseViewer

class SettingsActivity : AppCompatActivity() {
    companion object {
        private const val DEVELOPER_WEB_ADDRESS = "https://www.chronoscoper.com/"
        private const val GITHUB_REPO_URL = "https://github.com/kofuk/SecureScreen"
    }

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // trick
        prevActive = !SecureScreenNotification.isActive

        setupStartOnBootSwitch()
        setupFinishOnBackPressedSwitch()
        setupFinishButtonSwitch()
        setupAppInfo()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) initStartButton()
    }

    private var prevActive = false

    private fun initStartButton() {
        if (prevActive == SecureScreenNotification.isActive) return
        if (SecureScreenNotification.isActive) {
            binding.start.setOnClickListener {
                SecureScreenNotification.delete(this)
                initStartButton()
            }
        } else {
            binding.start.setOnClickListener {
                SecureScreenNotification.showNotification(this)
                initStartButton()
            }
        }
        val label = binding.start.findViewById<TextView>(R.id.start_label)
        label.text = getString(
            if (SecureScreenNotification.isActive)
                R.string.hide_notification
            else
                R.string.show_notification
        )
        Handler(Looper.getMainLooper()).postDelayed({
            binding.start.foreground = AppCompatResources.getDrawable(
                this,
                if (SecureScreenNotification.isActive)
                    R.drawable.card_button_deactivate
                else
                    R.drawable.card_button
            )
        }, 400)

        prevActive = SecureScreenNotification.isActive
    }

    private fun setupStartOnBootSwitch() =
        binding.startOnBoot.apply {
            isChecked = preferences.getBoolean("start_on_boot", false)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit().putBoolean("start_on_boot", isChecked).apply()
            }
        }

    private fun setupFinishOnBackPressedSwitch() =
        binding.finishOnBackPressed.apply {
            isChecked =
                preferences.getBoolean("finish_on_back_pressed", false)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit().putBoolean("finish_on_back_pressed", isChecked).apply()
            }
        }

    private fun setupFinishButtonSwitch() =
        binding.finishButton.apply {
            isChecked = preferences.getBoolean("finish_button", true)
            setOnCheckedChangeListener { _, isChecked ->
                preferences.edit().putBoolean("finish_button", isChecked).apply()
            }
        }

    private fun setupAppInfo() {
        binding.joinDevelop.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(GITHUB_REPO_URL)
                    )
                )
            } catch (ignore: ActivityNotFoundException) {
            }
        }
        binding.version.text = BuildConfig.VERSION_NAME
        binding.developer.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(DEVELOPER_WEB_ADDRESS)
                    )
                )
            } catch (ignore: ActivityNotFoundException) {
            }
        }
        binding.ossLicense.setOnClickListener {
            LicenseViewer.open(this, getString(R.string.oss_license), true)
        }
    }
}
