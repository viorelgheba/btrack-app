package com.btrack.btrack_app;

import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static android.app.PendingIntent.getBroadcast;
import static com.btrack.btrack_app.R.*;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH = 1;
    private BluetoothAdapter BTAdapter;

    private ArrayList<com.btrack.btrack_app.BluetoothDevice> bDevices = new ArrayList<com.btrack.btrack_app.BluetoothDevice>();
    private ScanFilter mScanFilter;
    private ScanSettings mScanSettings;
    private BluetoothLeScanner mBluetoothLeScanner;

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
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    int txPower = 6;
                    if (device.getName() != null) {
                        registerDevice(device, rssi, txPower);
                    }
                } catch (Exception ex) {
                    String m = ex.getMessage();
                }
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
    
    private void scanBluetooth() {

        BTAdapter.cancelDiscovery();
        TextView tv = (TextView) findViewById(id.foundDevices);
        tv.setText("");

        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return;
        }

        if (!BTAdapter.isEnabled()) {
            BTAdapter.enable();
        }

        BTAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        try {
            registerReceiver(mReceiver, filter);
        } catch (Exception ex) {
            String m = ex.getMessage();
        }
    }
}
