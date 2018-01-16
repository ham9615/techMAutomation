package com.example.adi.homesystem;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.UUID;

public class listActivity extends AppCompatActivity {

    ToggleButton button1,button2,button3,button4;
    ImageButton imgButton1,imgButton2,imgButton3,imgButton4;
    TextView textButton1,textButton2,textButton3,textButton4;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
   // static final UUID myUUID = UUID.fromString("150D8C8B-3BF8-E411-A26A-480FCFDE1EE1");

    Boolean isBTConnected = false;
    BluetoothSocket bluetoothSocket = null;
    private ProgressDialog loadingBar;
    BluetoothAdapter bluetoothAdapter;

    String address;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        button1 = (ToggleButton)findViewById(R.id.button1);
        button2 = (ToggleButton)findViewById(R.id.button2);
        button3 = (ToggleButton)findViewById(R.id.button3);
        button4 = (ToggleButton)findViewById(R.id.button4);

        imgButton1 =(ImageButton)findViewById(R.id.greenButton);
        imgButton2 =(ImageButton)findViewById(R.id.yellowButton);
        imgButton3 =(ImageButton)findViewById(R.id.magentaButton);
        imgButton4 =(ImageButton)findViewById(R.id.blueButton);

        textButton1 = (TextView)findViewById(R.id.button1name);
        textButton2 = (TextView)findViewById(R.id.button2name);
        textButton3 = (TextView)findViewById(R.id.button3name);
        textButton4 = (TextView)findViewById(R.id.button4name);
        Intent in = getIntent();
        address = in.getStringExtra(LaunchActivity.EXTRA_ADDRESS);
        System.out.println(address);
        new ConnectBT().execute();


}

    class ConnectBT extends AsyncTask<Void,Void,Void>{
        private Boolean isConnected = true;
        @Override
        protected Void doInBackground(Void... params) {

            try{

                if (bluetoothSocket==null||!isBTConnected){

                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bDevice = bluetoothAdapter.getRemoteDevice(address);
                    bluetoothSocket =bDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                }

            }catch (IOException e){

                isConnected = false;

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingBar = ProgressDialog.show(listActivity.this,"Please Wait!","Updating datasets ,wait!");
        }

        @Override
        protected void onPostExecute(Void Void) {
            super.onPostExecute(Void);
            if(!isConnected){

                Toast.makeText(getApplicationContext(),"Connection Failed. Is it a SPP Bluetooth? Try again.",Toast.LENGTH_SHORT).show();
                finish();

            }
            else
            {
                Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                isBTConnected = true;
            }

            loadingBar.dismiss();
        }


    }
}
