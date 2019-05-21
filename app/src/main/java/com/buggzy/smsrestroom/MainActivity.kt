package com.buggzy.smsrestroom

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.buggzy.smsrestroom.base.BaseActivity
import com.buggzy.smsrestroom.extensions.allAppPermissions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_url.*

class MainActivity : BaseActivity(), MultiplePermissionsListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
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
                }
                true
            }
            R.id.action_stop -> {
                if (Preferences.isRunning) {
                    Preferences.isRunning = false
                    refreshStatusText()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun promptForURL() {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle("REST URL")
            setView(View.inflate(applicationContext, R.layout.dialog_url, null))
            setNegativeButton(getString(android.R.string.cancel), null)
            setPositiveButton(getString(android.R.string.ok), null)
        }.create()
        dialog.setOnShowListener {
            val input = dialog.input_url
            input.setText(Preferences.restUrl)
            input.setSelection(input.text.length)
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val url = input.text.toString().trim()
                if (!TextUtils.isEmpty(url)) {
                    Preferences.restUrl = url
                    refreshStatusText()
                    dialog.cancel()
                }
            }
        }
        dialog.show()
    }

    private fun refreshStatusText() {
        statusText.text = StringBuilder().apply {
            append("Status: " + if (Preferences.isRunning) "Running" else "Stopped" + "\n")
            append("REST URL: ${Preferences.restUrl}\n")
        }.toString()
    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {}

    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {}
}
