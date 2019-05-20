package com.buggzy.smsrestroom;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class RestNotifier extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        int responseCode;
        try {
            Map<String, String> data = new HashMap<>();
            data.put("Sender", params[0]);
            data.put("Body", params[1]);
            URL url = new URL(MainActivity.getRESTUrl());
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
            outputPost.write(new JSONObject(data).toString().getBytes(Charset.forName("UTF-8")));
            outputPost.flush();
            outputPost.close();
            responseCode = client.getResponseCode();
            client.disconnect();
        } catch (Exception e) {
        }
        return null;
    }
}
