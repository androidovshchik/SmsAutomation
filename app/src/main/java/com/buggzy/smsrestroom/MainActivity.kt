package com.buggzy.smsrestroom

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.buggzy.smsrestroom.extensions.allAppPermissions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_url.*

class MainActivity : AppCompatActivity(), MultiplePermissionsListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
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

                refreshStatusText()
                true
            }
            R.id.action_stop -> {

                refreshStatusText()
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
            input.setText("")
            input.setSelection(input.text.length)
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val url = input.text.toString().trim()
                if (!TextUtils.isEmpty(url)) {
                    refreshStatusText()
                    dialog.cancel()
                }
            }
        }
        dialog.show()
    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {}

    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {}
}
