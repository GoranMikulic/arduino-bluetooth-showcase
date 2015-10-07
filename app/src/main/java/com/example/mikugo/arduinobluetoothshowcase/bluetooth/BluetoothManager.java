package com.example.mikugo.arduinobluetoothshowcase.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mikugo on 07/10/15.
 */
public class BluetoothManager {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String DEFAULT_SERIAL_UUID = "00001101-0000-1000-8000-00805f9b34fb";


    private BluetoothAdapter mBluetoothAdapter;
    private Activity activity;
    private ConnectThread mConnectionThread;
    private BluetoothSocket mSocket;
    private OutputStream mOutStream;

    public BluetoothManager(Activity activity){
        this.activity = activity;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Initializes bluetooth adapter and checks if bluetooth is enabled
     */
    public void initBluetooth() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(activity, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }



    public List<BluetoothDevice> getPairedDevices() {
        List pairedDevices = new ArrayList();
        pairedDevices.addAll(mBluetoothAdapter.getBondedDevices());

        return pairedDevices;
    }

    public void startDiscovery() {
        mBluetoothAdapter.startDiscovery();
    }

    public void connect(BluetoothDevice btDevice) {
        //mConnectionThread = new ConnectThread(btDevice, mBluetoothAdapter);
        //mConnectionThread.run();
        try {
            mSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(DEFAULT_SERIAL_UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        mBluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        try {
            mSocket.connect();
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException e2) {
                //errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        try {
            mOutStream = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            mOutStream.write(msgBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }
}
