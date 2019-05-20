package com.buggzy.smsrestroom;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 0;
    private static String RESTUrl = "";
    String rest_url_key = "rest_url";
    private boolean isRunning = false;
    private SmsBroadcastReceiver smsBroadcastReceiver;

    public static String getRESTUrl() {
        return RESTUrl;
    }

    public void setRESTUrl(String url) {
        RESTUrl = url;
        saveSettings();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadSettings();

        refreshStatusText();
    }

    private void loadSettings() {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        RESTUrl = sharedPref.getString(rest_url_key, "");
    }

    private void saveSettings() {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(rest_url_key, RESTUrl);
        editor.apply();
    }

    private void startListener() {
        if (!isRunning) {
            smsBroadcastReceiver = new SmsBroadcastReceiver();
            registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        }
        isRunning = true;
    }

    private void stopListener() {
        if (isRunning) {
            unregisterReceiver(smsBroadcastReceiver);
        }
        isRunning = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        refreshStatusText();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void refreshStatusText() {
        String statusText = "";

        statusText = statusText + "Status: " +
            (isRunning ? "Running" : "Stopped") + "\n";

        statusText = statusText + "SMS permissions: " +
            (hasReadSmsPermission() ? "OK" : "failed") + "\n";

        statusText = statusText + "REST URL: " +
            (getRESTUrl().equals("") ? "<undefined>" : getRESTUrl()) + "\n";

        TextView stControl = findViewById(R.id.statusText);
        stControl.setText(statusText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            promptForURL();
//            new RestNotifier().execute("123", "123");
            return true;
        }

        if (id == R.id.action_permissions) {
            requestSmsPermissions();
            return true;
        }

        if (id == R.id.action_start) {
            startListener();
            refreshStatusText();
            return true;
        }

        if (id == R.id.action_stop) {
            stopListener();
            refreshStatusText();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this,
            Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.INTERNET},
            SMS_PERMISSION_CODE);
    }

    private void promptForURL() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("REST URL");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(getRESTUrl());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setRESTUrl(input.getText().toString());
                refreshStatusText();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
