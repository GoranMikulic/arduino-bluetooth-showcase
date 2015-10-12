package com.example.mikugo.arduinobluetoothshowcase.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Thread running when connection is established. Provides input and output stream for sending and receiving data.
 */
public class ConnectedThread extends Thread {
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private Handler mBluetoothIn;


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

        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

    public void run() {
        byte delimiter = 10;
        int readBufferPosition = 0;
        byte[] readBuffer = new byte[1024];

        // Keep looping to listen for received messages
        while (true) {
            try {
                int bytesAvailable = mInStream.available();
                if (bytesAvailable > 0) {

                    byte[] packetBytes = new byte[bytesAvailable];

                    mInStream.read(packetBytes);
                    for (int i = 0; i < bytesAvailable; i++) {
                        byte b = packetBytes[i];
                        if (b == delimiter) {
                            byte[] encodedBytes = new byte[readBufferPosition];
                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                            final String data = new String(encodedBytes, "US-ASCII");
                            readBufferPosition = 0;

                            mBluetoothIn.obtainMessage(0, b, -1, data).sendToTarget();

                        } else {
                            readBuffer[readBufferPosition++] = b;
                        }
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    /**
     * Sending message to arduino
     *
     * @param input - Message to send
     */
    public void write(String input) {
        String msg = input + "\n";

        try {
            mOutStream.write(msg.getBytes()); //write bytes over BT connection via outstream
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

