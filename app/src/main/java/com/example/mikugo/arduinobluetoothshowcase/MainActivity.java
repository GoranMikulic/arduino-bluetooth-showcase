package com.example.mikugo.arduinobluetoothshowcase;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mikugo.arduinobluetoothshowcase.adapters.BluetoothDeviceListAdapapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;

    private ListView mPairedDevices;
    private BluetoothDeviceListAdapapter mPairedDevicesAdapter;

    private ListView mAvailableDevices;
    private BluetoothDeviceListAdapapter mAvailableDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBluetooth();

        mPairedDevices = (ListView) findViewById(R.id.list_paired_devices);
        mPairedDevicesAdapter = new BluetoothDeviceListAdapapter(this, R.layout.bluetooth_device_list_item, getPairedDevices());
        mPairedDevices.setAdapter(mPairedDevicesAdapter);
        mPairedDevices.setOnItemClickListener(new OnItemClickListener());

        mAvailableDevices = (ListView) findViewById(R.id.list_available_devices);
        mAvailableDevicesAdapter = new BluetoothDeviceListAdapapter(this, R.layout.bluetooth_device_list_item);
        mAvailableDevices.setAdapter(mAvailableDevicesAdapter);
        mAvailableDevices.setOnItemClickListener(new OnItemClickListener());

        discoverDevices();

    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice btDevice = (BluetoothDevice) mPairedDevices.getItemAtPosition(position);
            Toast.makeText(mPairedDevices.getContext(), "you selected" + btDevice.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initializes bluetooth adapter and checks if bluetooth is enabled
     */
    private void initBluetooth() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(MainActivity.this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        initPairedDevicesList();

    }

    private void initPairedDevicesList() {
        List<BluetoothDevice> devices = getPairedDevices();
        mPairedDevicesAdapter.clear();
        mPairedDevicesAdapter.addAll(devices);
        mPairedDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search_devices) {
            //queryPairedDevices();
        }

        return super.onOptionsItemSelected(item);
    }


    private List<BluetoothDevice> getPairedDevices() {
        List pairedDevices = new ArrayList();
        pairedDevices.addAll(mBluetoothAdapter.getBondedDevices());

        return pairedDevices;
    }

    private void discoverDevices() {
        mBluetoothAdapter.startDiscovery();

        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    mAvailableDevicesAdapter.add(device);
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }
}
