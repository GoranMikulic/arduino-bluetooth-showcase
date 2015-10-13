package com.example.mikugo.arduinobluetoothshowcase.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mikugo.arduinobluetoothshowcase.R;

import java.util.List;

/**
 * List adapter representing bluetooth devices in list view
 */
public class BluetoothDeviceListAdapapter extends ArrayAdapter<BluetoothDevice> {

    public BluetoothDeviceListAdapapter(Context context, int resource) {
        super(context, resource);
    }

    public BluetoothDeviceListAdapapter(Context context, int resource, List<BluetoothDevice> devices) {
        super(context, resource, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BluetoothDevice item = getItem(position);
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.bluetooth_device_list_item, null);
        view.setTag(item);

        if (item != null) {
            TextView btDeviceName = (TextView) view.findViewById(R.id.text_device_name);
            btDeviceName.setText(item.getName());
            btDeviceName.setTag(item);

            TextView btDeviceMacAdress = (TextView) view.findViewById(R.id.text_bt_mac_address);
            btDeviceMacAdress.setText(item.getAddress());
            btDeviceMacAdress.setTag(item);
        }

        return view;
    }

}
