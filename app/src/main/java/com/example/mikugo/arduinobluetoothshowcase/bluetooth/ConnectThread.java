package com.example.mikugo.arduinobluetoothshowcase.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * Thread for connecting a bluetooth device.
 */
public class ConnectThread extends Thread {

    private static final String DEFAULT_SERIAL_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    public static final int OBTAIN_SOCKET = 0;

    private final BluetoothSocket mSocket;
    private Handler mConnectedHandler;

    public ConnectThread(BluetoothDevice device, Handler connectedHandler) {
        // Use a temporary object that is later assigned to mSocket,
        // because mSocket is final
        BluetoothSocket tmp = null;
        mConnectedHandler = connectedHandler;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // DEFAULT_SERIAL_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(DEFAULT_SERIAL_UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        //mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mSocket.connect();
            mConnectedHandler.obtainMessage(OBTAIN_SOCKET, mSocket).sendToTarget();

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void disconnect() {
        try {
            mSocket.close();
        } catch (IOException e) {
        }
    }
}
