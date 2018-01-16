package com.example.adi.homesystem;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class LaunchActivity extends AppCompatActivity {

    ImageView logo;
    TextView logoText;
    private BluetoothAdapter bluetoothAdapter;
    Button connectButton;
    private Set<BluetoothDevice> pairList;
    public static String EXTRA_ADDRESS = "device_address";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);

        logo = (ImageView)findViewById(R.id.logo);
        logoText = (TextView)findViewById(R.id.logoText);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectButton =(Button)findViewById(R.id.connect);


        if(bluetoothAdapter==null){
            Toast.makeText(getApplicationContext(),"This device is not Bluetooth Supported",Toast.LENGTH_LONG).show();
        }

        else {
            Intent blueStartIntent =  new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blueStartIntent,1);
            Intent blueDiscoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(blueDiscoverIntent,2);

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pairList = bluetoothAdapter.getBondedDevices();

                    ArrayList<String> list = new ArrayList<String>();

                    if(pairList.size()>0){
                        for(BluetoothDevice bt:pairList){
                            System.out.println(bt.getName());
                            System.out.println(bt.getAddress());
                            System.out.println(bt.getUuids());



                          //  list.add(bt.getName() + " " + bt.getAddress());
                           if(bt.getName().equals("HAMSTER")){
                                    String address = bt.getAddress();
                                Intent nextScreenIntent = new Intent(LaunchActivity.this,listActivity.class);
                                nextScreenIntent.putExtra(EXTRA_ADDRESS,address);
                                startActivity(nextScreenIntent);
                            }

                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No paired Device",Toast.LENGTH_SHORT).show();
                    }



                }
            });
        }




    }
}
