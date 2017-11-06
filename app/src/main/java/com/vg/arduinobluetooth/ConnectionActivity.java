package com.vg.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ConnectionActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private ListView mDevicesListView;
    private Button mPairedButton;

    private static String TAG = "ConnectionActivity";
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mDevicesListView = (ListView) findViewById(R.id.devices_list_view);
        mPairedButton = (Button) findViewById(R.id.paired_button);
        mPairedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               pairedDevicesList(); //
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            if (!mBluetoothAdapter.isEnabled()) {
                //Ask to the user turn the bluetooth on
                Intent turnBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBluetoothOn,1);
            }
        }
    }

    private void pairedDevicesList()
    {
        mPairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if (mPairedDevices.size() > 0)
        {
            for(BluetoothDevice bt : mPairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        mDevicesListView.setAdapter(adapter);
        mDevicesListView.setOnItemClickListener(devicesListClickListener); //Method called when the device from the list is clicked
    }

    private AdapterView.OnItemClickListener devicesListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent i = new Intent(ConnectionActivity.this, ControlActivity.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); // this will be received at ControlActivity
            startActivity(i);
        }
    };
}