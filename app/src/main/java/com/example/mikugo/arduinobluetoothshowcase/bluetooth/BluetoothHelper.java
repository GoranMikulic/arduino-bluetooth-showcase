package com.example.mikugo.arduinobluetoothshowcase.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikugo on 07/10/15.
 */
public class BluetoothHelper {

    public static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private Activity activity;
    private ConnectThread mConnectionThread;
    private ConnectedThread mConnectedThread;

    private BluetoothSocket mSocket;
    private OutputStream mOutStream;

    public BluetoothHelper(Activity activity) {
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

        final Handler connectedHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {

                    Toast.makeText(activity, "Data received ", Toast.LENGTH_SHORT).show();
                }
            }
        };

        Handler connectHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ConnectThread.OBTAIN_SOCKET) {
                    mSocket = (BluetoothSocket) msg.obj;

                    try {
                        mOutStream = mSocket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mConnectedThread = new ConnectedThread(mSocket, connectedHandler);
                    mConnectedThread.start();
                }
            }
        };

        mConnectionThread = new ConnectThread(btDevice, mBluetoothAdapter, connectHandler);
        mConnectionThread.start();


    }

    public void sendData(String message) {
        mConnectedThread.write(message);
    }

    public void disconnect() {
        mConnectionThread.disconnect();
    }

    public void cancelDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }
}
