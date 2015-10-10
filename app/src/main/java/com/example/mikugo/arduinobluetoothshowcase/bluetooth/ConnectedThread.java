package com.example.mikugo.arduinobluetoothshowcase.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mikugo on 07/10/15.
 */
public class ConnectedThread extends Thread {
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mBluetoothIn;

    //creation of the connect thread
    public ConnectedThread(BluetoothSocket socket, Handler bluetoothIn) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mBluetoothIn = bluetoothIn;

        try {
            //Create I/O streams for connection
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        // Keep looping to listen for received messages
        while (true) {
            try {
                bytes = mmInStream.read(buffer);            //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                // Send the obtained bytes to the UI Activity via handler
                mBluetoothIn.obtainMessage(0, bytes, -1, readMessage).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    //write method
    public void write(String input) {
        byte[] msgBuffer = input.getBytes(); //converts entered String into bytes
        try {
            mmOutStream.write(msgBuffer); //write bytes over BT connection via outstream
        } catch (IOException e) {

        }
    }
}

