package com.example.mikugo.arduinobluetoothshowcase.bluetooth;

/**
 * Created by mikugo on 10/10/15.
 */
public interface BluetoothActionListener {

    void connected();

    void messageReceived(String message);

}
