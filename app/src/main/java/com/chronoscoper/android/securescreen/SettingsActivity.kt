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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView
import com.chronoscoper.library.licenseviewer.LicenseViewer
import kotterknife.bindView

class SettingsActivity : AppCompatActivity() {
    companion object {
        private const val DEVELOPER_WEB_ADDRESS = "https://www.chronoscoper.com/"
        private const val GIT_HUB_ADDRESS = "https://github.com/kofuk/SecureScreen"
    }

    private val startButton by bindView<FrameLayout>(R.id.start)
    private val versionLabel by bindView<TextView>(R.id.version)
    private val developerLabel by bindView<View>(R.id.developer)
    private val startOnBootSwitch by bindView<Switch>(R.id.start_on_boot)
    private val finishOnBackPressedSwitch by bindView<Switch>(R.id.finish_on_back_pressed)
    private val finishButtonSwitch by bindView<Switch>(R.id.finish_button)
    private val joinDevelopmentButton by bindView<View>(R.id.join_develop)
    private val ossLicenseLabel by bindView<View>(R.id.oss_license)

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
        if (SecureScreenNotification.isActive)
            startButton.setOnClickListener {
                SecureScreenNotification.delete(this)
                initStartButton()
            }
        else
            startButton.setOnClickListener {
                SecureScreenNotification.showNotification(this)
                initStartButton()
            }
        val label = startButton.findViewById<TextView>(R.id.start_label)
        label.text = getString(
                if (SecureScreenNotification.isActive)
                    R.string.hide_notification
                else
                    R.string.show_notification
        )
        Handler().postDelayed({
            startButton.foreground = getDrawable(
                    if (SecureScreenNotification.isActive)
                        R.drawable.card_button_deactivate
                    else
                        R.drawable.card_button
            )
        }, 400)

        prevActive = SecureScreenNotification.isActive
    }

    private fun setupStartOnBootSwitch() =
            startOnBootSwitch.apply {
                isChecked = preferences.getBoolean("start_on_boot", false)
                setOnCheckedChangeListener { _, isChecked ->
                    preferences.edit().putBoolean("start_on_boot", isChecked).apply()
                }
            }

    private fun setupFinishOnBackPressedSwitch() =
            finishOnBackPressedSwitch.apply {
                isChecked =
                        preferences.getBoolean("finish_on_back_pressed", false)
                setOnCheckedChangeListener { _, isChecked ->
                    preferences.edit().putBoolean("finish_on_back_pressed", isChecked).apply()
                }
            }

    private fun setupFinishButtonSwitch() =
            finishButtonSwitch.apply {
                isChecked = preferences.getBoolean("finish_button", true)
                setOnCheckedChangeListener { _, isChecked ->
                    preferences.edit().putBoolean("finish_button", isChecked).apply()
                }
            }

    private fun setupAppInfo() {
        joinDevelopmentButton.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse(GIT_HUB_ADDRESS)))
            } catch (ignore: ActivityNotFoundException) {
            }
        }
        versionLabel.text = BuildConfig.VERSION_NAME
        developerLabel.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse(DEVELOPER_WEB_ADDRESS)))
            } catch (ignore: ActivityNotFoundException) {
            }
        }
        ossLicenseLabel.setOnClickListener {
            LicenseViewer.open(this, getString(R.string.oss_license), true)
        }
    }
}
