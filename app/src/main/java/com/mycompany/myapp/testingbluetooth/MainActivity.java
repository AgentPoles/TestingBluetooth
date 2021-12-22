package com.mycompany.myapp.testingbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter bluetooth;
    ListView listor;
    ArrayList<String> bluetoothName = new ArrayList<>();
    ArrayAdapter<String> listo;
    ArrayAdapter<BluetoothDevice> connectedAdapter;
    private ArrayList<BluetoothDevice> connected = new ArrayList<BluetoothDevice>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listor = (ListView)findViewById(R.id.listor);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        listo = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,bluetoothName);
        connectedAdapter =new ArrayAdapter<BluetoothDevice>(this,android.R.layout.simple_list_item_1,connected);
        listor.setAdapter(listo);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

     if(!bluetooth.isEnabled()){
         Intent  i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(i,1);
     }
        else{
         initBluetooth(bluetooth);
     }
        BroadcastReceiver bluettoothstate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String prevStatee = BluetoothAdapter.EXTRA_PREVIOUS_STATE;
                String presentStatee = BluetoothAdapter.EXTRA_STATE;
                int state = intent.getIntExtra(presentStatee,-1);
                int prevstate = intent.getIntExtra(prevStatee,-1);
                String tt = "";
                switch (state){
                    case(BluetoothAdapter.STATE_TURNING_ON):{
                            tt = "bluetooth turning on";
                        break;
                    }
                    case(BluetoothAdapter.STATE_TURNING_OFF):{
                        tt = "bluetooth turning off";
                        break;
                    }
                    case(BluetoothAdapter.STATE_ON):{
                        tt = "bluetooth now on";
                        initBluetooth(bluetooth);
                        break;
                    }
                    case(BluetoothAdapter.STATE_OFF):{
                        tt= "bluetooth now off";
                        break;
                    }
                    default: break;

                }
                displayo(tt);
            }
        };
        String ActionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
                registerReceiver(bluettoothstate,new IntentFilter(ActionStateChanged));
    }

    private void startDiscovery(){

        if(bluetooth.isDiscovering()){
            displayo("discovering");
            registerReceiver(discoveredreceiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
        if(bluetooth.isEnabled()&&!bluetooth.isDiscovering()){
            displayo("not discovering");
            connected.clear();
            bluetoothName.clear();
            bluetooth.startDiscovery();
            startDiscovery();
        }
    }
     BroadcastReceiver discoveredreceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
            BluetoothDevice devico = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
             String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
             displayo(name);
          connected.add(devico);
             bluetoothName.add(name);
             listo.notifyDataSetChanged();
         }
     };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                initBluetooth(bluetooth);
            }
        }
        if(requestCode==2){
        }
    }
    BroadcastReceiver discoveryMonitor = new BroadcastReceiver() {
        String dfinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
        String dstarted = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
        @Override

        public void onReceive(Context context, Intent intent) {
            if(dfinished.equals(intent.getAction())){
                displayo("finished");
                listo.notifyDataSetChanged();
            }
            if(dfinished.equals((intent.getAction()))){
                displayo("just started");
            }
        }
    };
 BroadcastReceiver bluetoothknower = new BroadcastReceiver() {
     @Override
     public void onReceive(Context context, Intent intent) {
         String thisscanmode = BluetoothAdapter.EXTRA_SCAN_MODE;
         int coverable = intent.getIntExtra(thisscanmode,-1);
         String prevScan = BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE;
         switch(coverable){
             case (BluetoothAdapter.SCAN_MODE_CONNECTABLE):{
                     break;
             }
             case(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE):{
                 break;
             }
             case(BluetoothAdapter.SCAN_MODE_NONE):{
                 Intent y = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                 y.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,5000);
                 startActivityForResult(y,2);
                 break;
             }

         }
     }
 };
    public void initBluetooth(BluetoothAdapter bluetooth){
        String address = bluetooth.getAddress();
        String name = bluetooth.getName();
        bluetooth.setName("MrPoles");
        bluetooth.startDiscovery();
        startDiscovery();
        displayo(address);
        displayo(name);
        registerReceiver(discoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(discoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
    }

    public void displayo(String m){
        Toast.makeText(this,m,Toast.LENGTH_LONG).show();
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
        }

        return super.onOptionsItemSelected(item);
    }
}
