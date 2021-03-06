package com.example.mikugo.arduinobluetoothshowcase;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mikugo.arduinobluetoothshowcase.bluetooth.BluetoothActionListener;
import com.example.mikugo.arduinobluetoothshowcase.bluetooth.BluetoothHelper;

public class ControlActivity extends AppCompatActivity {

    private static final String LED_STATE_OFF = "300";
    private static final int SEEK_BAR_MAX_PROGRESS = 255;

    private String mDeviceAddress;
    private String mDeviceName;

    private BluetoothHelper btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice mDevice;

    private Button mButtonOn;
    private Button mButtonOff;
    private SeekBar mSeekBar;
    private TextView mReceivedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);
        mDeviceName = intent.getStringExtra(MainActivity.EXTRA_DEVICE_NAME);
        setTitle(mDeviceName);

        mDevice = btAdapter.getRemoteDevice(mDeviceAddress);
        btManager = new BluetoothHelper(this, new ControlBluetoothActionListener());
        btManager.connect(mDevice);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setEnabled(false);
        mSeekBar.setProgress(SEEK_BAR_MAX_PROGRESS);
        mButtonOn = (Button) findViewById(R.id.button_on);
        mButtonOn.setEnabled(false);
        mButtonOff = (Button) findViewById(R.id.button_off);
        mButtonOff.setEnabled(false);

        mReceivedMessage = (TextView) findViewById(R.id.data_from_arduino);

        mButtonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btManager.sendData(String.valueOf(mSeekBar.getProgress()));
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
                btManager.sendData(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btManager.sendData(LED_STATE_OFF);
        btManager.disconnect();
    }

    private class ControlBluetoothActionListener implements BluetoothActionListener {
        @Override
        public void connected() {
            mButtonOn.setEnabled(true);
        }

        @Override
        public void messageReceived(String message) {
            mReceivedMessage.setText(message);
        }
    }


}
