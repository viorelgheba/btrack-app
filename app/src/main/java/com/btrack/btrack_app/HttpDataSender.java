package com.btrack.btrack_app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class HttpDataSender {
    private ArrayList<BluetoothDevice> deviceList;

    private URL httpUrl;
    private HttpURLConnection httpUrlConnection;

    private String url = "https://salty-beach-38173.herokuapp.com/api/event";

    public HttpDataSender(ArrayList<BluetoothDevice> devices) {
        deviceList = devices;
    }

    private JSONObject createRequestBody() throws JSONException {
        JSONObject body = new JSONObject();
        JSONArray beacons = new JSONArray();

        for (BluetoothDevice device : deviceList) {
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("name", device.getName());
            deviceJson.put("uuid", device.getAddress());
            deviceJson.put("signal_strength", device.getSignalStrength());
            deviceJson.put("distance", device.getDistance());
            deviceJson.put("zone", device.getDistanceZone());
            beacons.put(deviceJson);
        }

        body.put("beacons", beacons);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(tz);

        body.put("timestamp", df.format(new Date()));

        return body;
    }

    private boolean hasDevices() {
        if (deviceList.isEmpty()) {
            return false;
        }

        return true;
    }

    public void send() {
        if (!hasDevices()) {
            return;
        }

        try {
            JSONObject json = createRequestBody();
            try {
                httpUrl = new URL(url);
                httpUrlConnection = (HttpURLConnection) httpUrl.openConnection();
                httpUrlConnection.setRequestProperty("Content-Type", "application/json");
                httpUrlConnection.setRequestProperty("Accept", "application/json");
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setDoInput(true);
                httpUrlConnection.setReadTimeout(10000);
                httpUrlConnection.setConnectTimeout(10000);
                httpUrlConnection.setChunkedStreamingMode(0);

            } catch (MalformedURLException error) {
                error.printStackTrace();
                return;
            } catch (SocketTimeoutException error) {
                error.printStackTrace();
                return;
            } catch (IOException error) {
                error.printStackTrace();
                return;
            }

            OutputStream outputStream = httpUrlConnection.getOutputStream();
            BufferedWriter oWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            oWriter.write(json.toString());
            oWriter.close();
            outputStream.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
            deviceList.clear();
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
