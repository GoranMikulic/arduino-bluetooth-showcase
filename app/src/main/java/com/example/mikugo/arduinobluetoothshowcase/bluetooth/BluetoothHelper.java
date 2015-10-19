package com.example.mikugo.arduinobluetoothshowcase.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikugo on 07/10/15.
 */
public class BluetoothHelper {

    public static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private Activity mActivity;
    private ConnectThread mConnectionThread;
    private ConnectedThread mConnectedThread;
    private BluetoothActionListener mdeviceConnectedListener;

    private BluetoothSocket mSocket;

    public BluetoothHelper(Activity activity, BluetoothActionListener bluetoothActionListener) {
        mActivity = activity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mdeviceConnectedListener = bluetoothActionListener;
    }

    public BluetoothHelper(Activity activity) {
        this(activity, null);
    }

    /**
     * Initializes bluetooth adapter and checks if bluetooth is enabled
     */
    public void initBluetooth() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(mActivity, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                 // Gets whether you should show UI with rationale for requesting a permission.
                 // You should do this only if you do not have the permission and the context in
                 //which the permission is requested does not clearly communicate to the user
                 // what would be the benefit from granting this permission.
            } else {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
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

    public void stopDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }

    /**
     * Establishing connection to a BluetoothDevice, running asynchronously in a thread.
     *
     * @param btDevice - BluetoothDevice to connect
     */
    public void connect(BluetoothDevice btDevice) {

        final Handler connectedHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {

                    if (mdeviceConnectedListener != null) {
                        mdeviceConnectedListener.messageReceived((String) msg.obj);
                    } else {
                        Toast.makeText(mActivity, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        Handler connectHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ConnectThread.OBTAIN_SOCKET) {
                    mSocket = (BluetoothSocket) msg.obj;

                    mConnectedThread = new ConnectedThread(mSocket, connectedHandler);
                    mConnectedThread.start();

                    if (mdeviceConnectedListener != null) {
                        mdeviceConnectedListener.connected();
                    }
                }
            }
        };

        mConnectionThread = new ConnectThread(btDevice, connectHandler);
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
