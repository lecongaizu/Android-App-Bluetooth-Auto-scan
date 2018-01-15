package com.example.lcong.androidbluetooth;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Handler;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lcong.androidbluetooth.R;

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private Button onBtn;
    private Button offBtn;

    // private Button listBtn;
    private Button findBtn;
    private TextView statusText;
    private BluetoothAdapter bluetoothAdapter;

    //private Set<BluetoothDevice> pairedDevices;
    private ListView bluetoothDeviceListView;
    private ArrayAdapter<String> bluetoothArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // take an instance of BluetoothAdapter - Bluetooth radio
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);

            // listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            statusText.setText("Status: not supported");

            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            statusText = findViewById(R.id.text);

            /// On Button/////////////////
            onBtn = findViewById(R.id.turnOn);
            onBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    on(v);
                }
            });

            /// Off Button/////////////////
            offBtn = findViewById(R.id.turnOff);
            offBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    off(v);
                }
            });


            findBtn = findViewById(R.id.search);
            findBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    find(v);
                }
            });

            bluetoothDeviceListView = findViewById(R.id.listView1);

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            bluetoothArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            bluetoothDeviceListView.setAdapter(bluetoothArrayAdapter);
        }
    }

    public void on(View view){
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void off(View view){
        bluetoothAdapter.disable();
        statusText.setText("Status of Bluetooth: Disconnected");

        Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(bluetoothAdapter.isEnabled()) {
                statusText.setText("Status of Bluetooth: Enabled");
            } else {
                statusText.setText("Status of Bluetooth: Disabled");
            }
        }
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // add the rssi of bluetooth
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                // add the name and the MAC address of the object to the arrayAdapter
                bluetoothArrayAdapter.add(device.getName() + "\n" + device.getAddress() + "\n" + rssi + " dB");
                bluetoothArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    /// the code using timer for automatically update list device
    public  void find( View view)
    {
        final Timer mTimer = new Timer();
        final TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bluetoothAdapter.isDiscovering()) {
                            bluetoothAdapter.cancelDiscovery();
                        }
                        else {
                            bluetoothArrayAdapter.clear();
                            bluetoothAdapter.startDiscovery();
                            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                        }
                    }
                });

            }
        };
        findBtn.setVisibility(View.GONE);
        mTimer.schedule(mTimerTask,0,2000); // set up the time for update (2000 milisecond = 2s)
    }
////////////////////



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }

}
