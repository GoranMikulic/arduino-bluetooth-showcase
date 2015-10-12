package com.example.mikugo.arduinobluetoothshowcase;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mikugo.arduinobluetoothshowcase.adapters.BluetoothDeviceListAdapapter;
import com.example.mikugo.arduinobluetoothshowcase.bluetooth.BluetoothHelper;

import java.util.List;

/**
 * Main Activity - responsible for displaying available and paired devices. Starts discovering bluetooth devices on startup.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothHelper mBtHelper;

    private ListView mPairedDevices;
    private BluetoothDeviceListAdapapter mPairedDevicesAdapter;

    private ListView mAvailableDevices;
    private BluetoothDeviceListAdapapter mAvailableDevicesAdapter;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtHelper = new BluetoothHelper(this);
        mBtHelper.initBluetooth();

        mPairedDevices = (ListView) findViewById(R.id.list_paired_devices);
        mPairedDevicesAdapter = new BluetoothDeviceListAdapapter(this, R.layout.bluetooth_device_list_item, mBtHelper.getPairedDevices());
        mPairedDevices.setAdapter(mPairedDevicesAdapter);
        mPairedDevices.setOnItemClickListener(new OnItemClickListener(this, mPairedDevices));

        mAvailableDevices = (ListView) findViewById(R.id.list_available_devices);
        mAvailableDevicesAdapter = new BluetoothDeviceListAdapapter(this, R.layout.bluetooth_device_list_item);
        mAvailableDevices.setAdapter(mAvailableDevicesAdapter);
        mAvailableDevices.setOnItemClickListener(new OnItemClickListener(this, mAvailableDevices));

        mBtHelper.startDiscovery();

        //receiver handles broadcast if a bluetooth device is found, adds device to the list
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    mAvailableDevicesAdapter.add(device);
                    mAvailableDevicesAdapter.notifyDataSetChanged();
                }
            }
        };

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

    }

    /**
     * Devices doesn't get explored if bluetooth is not active before launch, discovery doesn't get started properly in onCreate() because
     * bluetooth is not activated. Activity receives result with requestCode BluetoothHelper.REQUEST_ENABLE_BT after user accepted bluetooth
     * activation.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (BluetoothHelper.REQUEST_ENABLE_BT == requestCode) {
            mBtHelper.initBluetooth();
            mBtHelper.startDiscovery();
        }
    }

    /**
     * Starting intent for ControlActivity when user selects a device
     */
    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        private Context context;
        private ListView listView;

        public OnItemClickListener(Context c, ListView listView) {
            this.context = c;
            this.listView = listView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice btDevice = (BluetoothDevice) listView.getItemAtPosition(position);
            mBtHelper.cancelDiscovery();

            Intent intent = new Intent(context, ControlActivity.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, btDevice.getAddress());

            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initPairedDevicesList();
    }

    /**
     * Setting data for the list of paired devices
     */
    private void initPairedDevicesList() {
        List<BluetoothDevice> devices = mBtHelper.getPairedDevices();
        mPairedDevicesAdapter.clear();
        mPairedDevicesAdapter.addAll(devices);
        mPairedDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
