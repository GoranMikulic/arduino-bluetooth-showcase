package com.example.mikugo.arduinobluetoothshowcase.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikugo on 07/10/15.
 */
public class BluetoothManager {

    private static final int REQUEST_ENABLE_BT = 1;


    private BluetoothAdapter mBluetoothAdapter;
    private Activity activity;
    private ConnectThread mConnectionThread;

    public BluetoothManager(Activity activity){
        this.activity = activity;
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
        mConnectionThread = new ConnectThread(btDevice, mBluetoothAdapter);
        mConnectionThread.run();
    }

    public  void disconnect() {
        mConnectionThread.cancel();
    }
}
