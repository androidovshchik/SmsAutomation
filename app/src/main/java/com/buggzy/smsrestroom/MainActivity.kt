package com.buggzy.smsrestroom

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.buggzy.smsrestroom.base.BaseActivity
import com.buggzy.smsrestroom.extensions.*
import com.buggzy.smsrestroom.receivers.ServiceReceiver
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_url.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.stopService

class MainActivity : BaseActivity(), MultiplePermissionsListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (Preferences.isRunning) {
            if (!activityManager.isServiceRunning<MainService>()) {
                startForegroundService<MainService>()
            }
        } else {
            if (activityManager.isServiceRunning<MainService>()) {
                stopService<MainService>()
            }
        }
        refreshStatusText()
        Dexter.withActivity(this)
            .withPermissions(*allAppPermissions)
            .withListener(this)
            .check()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                promptForURL()
                true
            }
            R.id.action_start -> {
                if (!Preferences.isRunning) {
                    Preferences.isRunning = true
                    refreshStatusText()
                    restartForegroundService<MainService>()
                }
                true
            }
            R.id.action_stop -> {
                if (Preferences.isRunning) {
                    Preferences.isRunning = false
                    refreshStatusText()
                    cancelAlarm<ServiceReceiver>()
                    if (activityManager.isServiceRunning<MainService>()) {
                        stopService<MainService>()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun promptForURL() {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle("Base URL")
            setView(View.inflate(applicationContext, R.layout.dialog_url, null))
            setNegativeButton(getString(android.R.string.cancel), null)
            setPositiveButton(getString(android.R.string.ok), null)
        }.create()
        dialog.setOnShowListener {
            val input = dialog.input_url
            input.setText(Preferences.baseUrl)
            input.setSelection(input.text.length)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                Preferences.baseUrl = input.text.toString().trim()
                refreshStatusText()
                dialog.cancel()
            }
        }
        dialog.show()
    }

    private fun refreshStatusText() {
        statusText.text = StringBuilder().apply {
            append("Android ID: $androidId\n")
            append("Status: ${if (Preferences.isRunning) "running" else "stopped"}\n")
            append("Base URL: ${Preferences.baseUrl}\n")
        }.toString()
    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {}

    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {}
}
