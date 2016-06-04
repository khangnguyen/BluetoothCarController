package com.example.khang.bluetoothcarcontroller;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private OutputStream outputStream;
    private InputStream inStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            this.init();
        } catch (Exception e) {
            this.showAlert(e.getMessage());
        }
    }

    private void showAlert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.show();
    }
    private void init() throws IOException {
        Log.i("info", "init is run");
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.e("error", "Get adapter fine");
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    Object[] devices = (Object []) bondedDevices.toArray();
                    for (Iterator<BluetoothDevice> i = bondedDevices.iterator();
                         i.hasNext();) {
                        BluetoothDevice device = i.next();
                        String dName = device.getName();
                        if (device.getName() == "our_device_name") {
                            ParcelUuid[] uuids = device.getUuids();
                            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                            socket.connect();
                            this.outputStream = socket.getOutputStream();
                            this.inStream = socket.getInputStream();
                            return;
                        }
                    }
                }
                throw new RuntimeException("Can't find bluetooth device");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        } else {
            Log.e("error", "Bluetooth is not available.");
        }
    }

    public void sendMsg(View view) {
        try {
            String direction = view.getTag().toString();
            this.outputStream.write(direction.getBytes());
        } catch (Exception e) {
            this.showAlert(e.getMessage());
        }
    }
}
