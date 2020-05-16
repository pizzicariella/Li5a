package de.htw.ai.ema.network.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


//TODO interface erstellen
public class BluetoothService {

    //TODO find out what would be a useful tag
    private static final String TAG = "BluetoothService";
    private Handler handler;

    private interface MessageConstants{
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ...
    }

    //TODO dies ist ggf. nicht unbedingt nötig, da ja nur die streams übergeben werden sollen, aber implementierungen ggf so verwenden
    private class ConnectedThread extends Thread{

        private final BluetoothSocket btSocket;
        private final InputStream btIn;
        private final OutputStream btOut;
        private byte[] buffer;

        public ConnectedThread(BluetoothSocket socket){
            btSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error creating output stream", e);
            }
            btIn = tmpIn;
            btOut = tmpOut;
        }

        public void run(){
            buffer = new byte[1024];
            int numBytes;
            boolean listening = true;

            while (listening){
                try {
                    numBytes = btIn.read(buffer);
                    Message readMsg = handler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1, buffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "InputStream disconnected", e);
                    listening = false;
                }
            }
        }

        public void write(byte[] bytes){
            try{
                btOut.write(bytes);
                Message writtenMsg = handler.obtainMessage(MessageConstants.MESSAGE_WRITE, -1, -1, buffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error sending data", e);
                Message writeErrorMsg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "couldn't send data to other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        public void cancel(){
            try{
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close socket", e);
            }
        }

    }
}
