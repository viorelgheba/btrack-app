package com.btrack.btrack_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static com.btrack.btrack_app.R.*;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter BTAdapter;

    private ArrayList<com.btrack.btrack_app.BluetoothDevice> bDevices = new ArrayList<com.btrack.btrack_app.BluetoothDevice>();

    private long lastTimeDataWasSend = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        final Button btnScan;
        final Button exitButton;

        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        btnScan = (Button) findViewById(id.btn_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBluetooth();
            }
        });

        exitButton = (Button) findViewById(id.ext_Button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.exit(0);
            }
        });

        scanBluetooth();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                try {
                    /*do not register devices without name*/
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getName() != null) {
                        int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                        int txPower = 6;
                        registerDevice(device, rssi, txPower);
                    }
                } catch (Exception ex) {
                    Dialog dialog = new Dialog(ex.getMessage());
                    dialog.popup(context, true);
                }

                if (shouldResetSearch()) {
                    sendCollectedData();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                sendCollectedData();
            }
        }
    };

    private void registerDevice(BluetoothDevice device, int rssi, int power) {
        TextView list = (TextView) findViewById(id.foundDevices);
        list.append(device.getName());
        list.append("\n");
        com.btrack.btrack_app.BluetoothDevice bluetoothDevice = new com.btrack.btrack_app.BluetoothDevice();
        bluetoothDevice.setName(device.getName());
        bluetoothDevice.setAddress(device.getAddress());
        bluetoothDevice.setState(device.getBondState());
        bluetoothDevice.setSignalStrength(rssi);
        bluetoothDevice.setSignalPower(power);
        bDevices.add(bluetoothDevice);
    }


    private void sendCollectedData() {
        // send data
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpDataSender dataSender = new HttpDataSender(bDevices);
                    dataSender.send();
                } catch (Exception e) {
                    Dialog dialog = new Dialog(e.getMessage());
                    dialog.popup(getBaseContext(), true);
                }
            }
        });
        thread.start();

        resetSearch();
    }

    private boolean shouldResetSearch() {
        long msTime = System.currentTimeMillis();
        long dif = (msTime - lastTimeDataWasSend) / 1000;
        int scanDelay = 3;
        if (Math.abs(dif) > scanDelay) {
            lastTimeDataWasSend = msTime;

            return true;
        }

        return false;
    }

    private void resetSearch() {
        TextView fDevices = (TextView) findViewById(id.foundDevices);
        fDevices.setText("");
        BTAdapter.startDiscovery();
    }

    private void scanBluetooth() {
        if (BTAdapter == null) {
            Dialog dialog = new Dialog("Your phone does not support Bluetooth");
            dialog.popup(this, true);

            return;
        }

        if (!BTAdapter.isEnabled()) {
            BTAdapter.enable();
        }

        BTAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);
    }
}
