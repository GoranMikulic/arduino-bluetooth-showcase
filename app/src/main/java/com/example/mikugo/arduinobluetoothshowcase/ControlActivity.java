package com.example.mikugo.arduinobluetoothshowcase;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mikugo.arduinobluetoothshowcase.bluetooth.BluetoothManager;

public class ControlActivity extends AppCompatActivity {

    private static final String DEFAULT_SERIAL_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    private String mDeviceAddress;

    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

        mDevice = btAdapter.getRemoteDevice(mDeviceAddress);

        btManager = new BluetoothManager(this);
        btManager.connect(mDevice);


        btManager.sendData("1");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btManager.sendData("0");
        btManager.disconnect();
    }
}
