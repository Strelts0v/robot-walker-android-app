package com.vg.arduinobluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {

    private Button mForwardButton;
    private Button mForwardLeftButton;
    private Button mForwardRightButton;
    private Button mLeftButton;
    private Button mStopButton;
    private Button mRightButton;
    private Button mBackButton;
    private Button mBackLeftButton;
    private Button mBackRightButton;
    private Button mDisableButton;
    private ProgressDialog progress;

    private String address;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private boolean isBluetoothConnected = false;
    //SPP UUID. Look for it
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String BLUETOOTH_COMMAND_FORWARD = "1";
    private static String BLUETOOTH_COMMAND_FORWARD_LEFT = "2";
    private static String BLUETOOTH_COMMAND_FORWARD_RIGHT = "3";
    private static String BLUETOOTH_COMMAND_BACKWARD = "4";
    private static String BLUETOOTH_COMMAND_BACKWARD_LEFT = "5";
    private static String BLUETOOTH_COMMAND_BACKWARD_RIGHT = "6";
    private static String BLUETOOTH_COMMAND_TURN_LEFT = "7";
    private static String BLUETOOTH_COMMAND_TURN_RIGHT = "8";
    private static String BLUETOOTH_COMMAND_STOP = "9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(ConnectionActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device

        mForwardButton = (Button) findViewById(R.id.forward_button);
        mForwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(BLUETOOTH_COMMAND_FORWARD);
            }
        });

        mForwardLeftButton = (Button) findViewById(R.id.forward_left_button);
        mForwardLeftButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(BLUETOOTH_COMMAND_FORWARD_LEFT);
            }
        });

        mForwardRightButton = (Button) findViewById(R.id.forward_right_button);
        mForwardRightButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(BLUETOOTH_COMMAND_FORWARD_RIGHT);
            }
        });

        mLeftButton = (Button) findViewById(R.id.left_button);
        mLeftButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(BLUETOOTH_COMMAND_TURN_LEFT);
            }
        });

        mStopButton = (Button) findViewById(R.id.stop_button);
        mStopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(BLUETOOTH_COMMAND_STOP);
            }
        });

        mRightButton = (Button) findViewById(R.id.right_button);
        mRightButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(BLUETOOTH_COMMAND_TURN_RIGHT);
            }
        });

        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(BLUETOOTH_COMMAND_BACKWARD);
            }
        });

        mBackLeftButton = (Button) findViewById(R.id.back_left_button);
        mBackLeftButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(BLUETOOTH_COMMAND_BACKWARD_LEFT);
            }
        });

        mBackRightButton = (Button) findViewById(R.id.back_right_button);
        mBackRightButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(BLUETOOTH_COMMAND_BACKWARD_RIGHT);
            }
        });

        mDisableButton = (Button) findViewById(R.id.disable_button);
        mDisableButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                disconnect(); //close connection
            }
        });

        // Call the class to connect
        new ConnectBluetooth().execute();
    }

    private void sendData(String data){
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.getOutputStream().write(data.getBytes());
            } catch (IOException e) {
                showMessage("Error");
            }
        }
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void disconnect() {
        if (mBluetoothSocket!=null) {
            try {
                mBluetoothSocket.close(); //close connection
            } catch (IOException e) {
                showMessage("Error");
            }
        }
        finish(); //return to the first layout

    }

    private class ConnectBluetooth extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ControlActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (mBluetoothSocket == null || !isBluetoothConnected)
                {
                    //get the mobile bluetooth device
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    //connects to the device's address and checks if it's available
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    //create a RFCOMM (SPP) connection
                    mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBluetoothSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                showMessage("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else {
                showMessage("Connected.");
                isBluetoothConnected = true;
            }
            progress.dismiss();
        }
    }
}
