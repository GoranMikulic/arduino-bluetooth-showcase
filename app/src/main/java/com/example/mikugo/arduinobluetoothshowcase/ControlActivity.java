package com.example.mikugo.arduinobluetoothshowcase;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.mikugo.arduinobluetoothshowcase.bluetooth.BluetoothHelper;

public class ControlActivity extends AppCompatActivity {

    private static final String LED_STATE_ON = "255";
    private static final String LED_STATE_OFF = "0";

    private String mDeviceAddress;

    private BluetoothHelper btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice mDevice;

    private Button mButtonOn;
    private Button mButtonOff;
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        
        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);
        mDevice = btAdapter.getRemoteDevice(mDeviceAddress);
        btManager = new BluetoothHelper(this);
        btManager.connect(mDevice);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setEnabled(false);
        mSeekBar.setProgress(255);
        mButtonOn = (Button) findViewById(R.id.button_on);
        mButtonOff = (Button) findViewById(R.id.button_off);
        mButtonOff.setEnabled(false);

        mButtonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btManager.sendData(LED_STATE_ON);
                mButtonOff.setEnabled(true);
                mButtonOn.setEnabled(false);
                mSeekBar.setEnabled(true);
            }
        });

        mButtonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btManager.sendData(LED_STATE_OFF);
                mButtonOn.setEnabled(true);
                mButtonOff.setEnabled(false);
                mSeekBar.setEnabled(false);
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println(progress);
                btManager.sendData("100");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        
        //btManager.sendData("1");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btManager.sendData("0");
        btManager.disconnect();
    }

}
