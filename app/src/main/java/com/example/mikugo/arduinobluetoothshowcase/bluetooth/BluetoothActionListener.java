package com.example.mikugo.arduinobluetoothshowcase.bluetooth;

/**
 * Interface for bluetooth actions
 */
public interface BluetoothActionListener {

    void connected();

    void messageReceived(String message);

}
