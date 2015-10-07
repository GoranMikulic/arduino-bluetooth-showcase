package com.example.mikugo.arduinobluetoothshowcase;

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

import com.example.mikugo.arduinobluetoothshowcase.adapters.BluetoothDeviceListAdapapter;
import com.example.mikugo.arduinobluetoothshowcase.bluetooth.BluetoothManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothManager mBtManager;

    private ListView mPairedDevices;
    private BluetoothDeviceListAdapapter mPairedDevicesAdapter;

    private ListView mAvailableDevices;
    private BluetoothDeviceListAdapapter mAvailableDevicesAdapter;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtManager = new BluetoothManager(this);
        mBtManager.initBluetooth();

        mPairedDevices = (ListView) findViewById(R.id.list_paired_devices);
        mPairedDevicesAdapter = new BluetoothDeviceListAdapapter(this, R.layout.bluetooth_device_list_item, mBtManager.getPairedDevices());
        mPairedDevices.setAdapter(mPairedDevicesAdapter);
        mPairedDevices.setOnItemClickListener(new OnItemClickListener(this, mPairedDevices));

        mAvailableDevices = (ListView) findViewById(R.id.list_available_devices);
        mAvailableDevicesAdapter = new BluetoothDeviceListAdapapter(this, R.layout.bluetooth_device_list_item);
        mAvailableDevices.setAdapter(mAvailableDevicesAdapter);
        mAvailableDevices.setOnItemClickListener(new OnItemClickListener(this, mAvailableDevices));


        mBtManager.startDiscovery();

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
            mBtManager.cancelDiscovery();

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

    private void initPairedDevicesList() {
        List<BluetoothDevice> devices = mBtManager.getPairedDevices();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mBtManager.disconnect();
    }
}
