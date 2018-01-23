package com.example.adi.homesystem;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.Result;

import static com.example.adi.homesystem.R.mipmap.speak;

public class listActivity extends AppCompatActivity implements AIListener{

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
    private FirebaseAnalytics firebaseAnalytics;
    ImageButton speakButton;
    private AIService aiService;



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
        speakButton = (ImageButton)findViewById(R.id.speak);
        Intent in = getIntent();
        address = in.getStringExtra(LaunchActivity.EXTRA_ADDRESS);
        System.out.println(address);
        SharedPreferences preferences =getSharedPreferences("com.example.adi.homesystem",MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        final FirebaseAnalytics mAnalytics = FirebaseAnalytics.getInstance(this);
        mAnalytics.setMinimumSessionDuration(1000);






        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
      final   DatabaseReference device1db = mFirebaseInstance.getReference("device1");
      final  DatabaseReference device2db = mFirebaseInstance.getReference("device2");
      final  DatabaseReference device3db = mFirebaseInstance.getReference("device3");
      final  DatabaseReference device4db = mFirebaseInstance.getReference("device4");


        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
       final Date date = new Date();


        final AIConfiguration config = new AIConfiguration("c8f537db821a4ca38151e393ef400e44",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);


        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aiService.startListening();

            }
        });

    //code to save state
        Boolean r1 = preferences.getBoolean("r1_on_state",true);
        if(r1){
            button1.setChecked(true);

        }
        else
            button1.setChecked(false);




//Only testing the first relay


        button1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                final Bundle params = new Bundle();
            final String key = device1db.push().getKey();

                if(isChecked){
                    if(bluetoothSocket!=null){

                        try {
                            device1db.child(key).child("off_time").setValue(formatter.format(date).toString());
                            params.putString(FirebaseAnalytics.Param.START_DATE,formatter.format(date));
                            mAnalytics.logEvent(FirebaseAnalytics
                                    .Event.SELECT_CONTENT,params);
                            editor.putBoolean("r1_on_state",true);
                            editor.commit();
                            bluetoothSocket.getOutputStream().write("a0".toString().getBytes());

                        } catch (IOException e) {
                            System.out.print("Error encountered at channel 1 while switcing off");
                            Toast.makeText(getApplicationContext(),"Error encountered at channel 1 while switcing off",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }
                   else{
                    if(bluetoothSocket!=null){
                        try {
                            device1db.child(key).child("on_time").setValue(formatter.format(date).toString());
                            params.putString(FirebaseAnalytics.Param.END_DATE,formatter.format(date));
                            mAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,params);
                            editor.putBoolean("r1_off_state",false);
                            editor.commit();
                            bluetoothSocket.getOutputStream().write("a1".toString().getBytes());

                        }catch (IOException e){
                            System.out.print("Error encountered at channel 1 while switching on");
                            Toast.makeText(getApplicationContext(),"Error encountered at channel 1 while switching on",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }}
                );

        mAnalytics.setAnalyticsCollectionEnabled(true);

        new ConnectBT().execute();



}

    @Override
    public void onResult(ai.api.model.AIResponse response) {
        Result resultApi = response.getResult();

        String responseString = "";

        if(resultApi.getParameters()!=null&&!resultApi.getParameters().isEmpty()){

            for (Map.Entry<String,JsonElement> entry:resultApi.getParameters().entrySet()){

                switch(entry.getKey().toString()){

                    case "dev1on":post("Module 1 switched on");
                        button1.setChecked(true);

                        break;
                    /*case "r2_on":post("Module 2 switched on");
                        l2.setChecked(true);
                        break;
                    case "r3":post("Module 3 switched on");
                        l3.setChecked(true);
                        break;
                    case "r4":post("Module 4 switched on");
                        l4.setChecked(true);
                        break;*/
                    case "r1_off":post("Module 1 switched off");
                        button1.setChecked(false);
                        break;
                    /*case "r3_off":post("Module 3 switched off");
                        l3.setChecked(false);
                        break;
                    case "r4_off":post("Module 4 switched it bs");
                        l4.setChecked(false);
                        break;
                    case "r2_off":post("Module 2 switche off");
                        l2.setChecked(false);
                        break;*/
                    default:
                        post("Failed to interpret data");

                }

            }
        }
    }

    private void post(String s) {

        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError(ai.api.model.AIError error) {
        post(error.toString());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

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
