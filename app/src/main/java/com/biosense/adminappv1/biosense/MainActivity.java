package com.biosense.adminappv1.biosense;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements ArduinoListener {

    public Arduino arduino;

    public String globalVar = "Blank";
    public String globalVar2 = "Blank";

    public int readingNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //Design settings
        getSupportActionBar().hide();
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Brandon.otf");
        Typeface openS = Typeface.createFromAsset(getAssets(), "fonts/OSLight.ttf");


        TextView vmTitle = (TextView) findViewById(R.id.vm_title);
        vmTitle.setTypeface(custom_font);
        TextView mainActivityDetail = findViewById(R.id.main_activity_detail);
        mainActivityDetail.setTypeface(openS);

        arduino = new Arduino(this);
        arduino.setArduinoListener(this);

    }

    @Override
    protected void onStart(){
        super.onStart();
        arduino.setArduinoListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.close();
        arduino.unsetArduinoListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        arduino.close();
        arduino.unsetArduinoListener();
    }


    @Override
    public void onArduinoAttached(UsbDevice device) {
        arduino.open(device);

        TextView status = (TextView) findViewById(R.id.status);
        status.setText("Arduino Attached");

        ImageView statusImg = (ImageView) findViewById(R.id.status_img);
        statusImg.setImageResource(R.drawable.microchip_green);

        Button startButton = (Button) findViewById(R.id.start_btn);
        startButton.setEnabled(true);

    }

    @Override
    public void onArduinoDetached() {
        arduino.close();

        TextView status = (TextView) findViewById(R.id.status);
        status.setText("Arduino Detached");

        ImageView statusImg = (ImageView) findViewById(R.id.status_img);
        statusImg.setImageResource(R.drawable.microchip_black);

        Button startButton = (Button) findViewById(R.id.start_btn);

        startButton.setEnabled(false);
    }

    @Override
    public void onArduinoMessage(byte[] bytes) {

        gatherData(new String(bytes));


    }

    @Override
    public void onArduinoOpened() {

    }

    @Override
    public void onUsbPermissionDenied() {

    }

    public void gatherData(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assembleString(message);
            }
        });
    }

    public void getreadings(View view){

        Toast.makeText(this, "Start button pressed", Toast.LENGTH_SHORT).show();

        globalVar = "";
        globalVar2 = "";

        readingNumber = 1;
        String str = "5";
        arduino.send(str.getBytes());

        TextView textView = findViewById(R.id.getting_readings);
        ProgressBar progressBar = findViewById(R.id.readings_progress);

        textView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

    }



    public void assembleString(String message){

        if(readingNumber == 1){
            globalVar = globalVar + message;
        } else if(readingNumber == 2){
            globalVar2 = globalVar2 + message;
        }


        if (globalVar.length()<20 && readingNumber == 1){


            Button startButton = findViewById(R.id.start_btn);
            startButton.setEnabled(false);

            ProgressBar progressBar = findViewById(R.id.readings_progress);
            progressBar.setIndeterminate(false);
            progressBar.setProgress((Integer)globalVar.length()/20*50);

        } else if(globalVar.length()==20 && readingNumber == 1){

            ProgressBar progressBar = findViewById(R.id.readings_progress);
            progressBar.setProgress(50);

            Toast.makeText(this, globalVar, Toast.LENGTH_SHORT).show();


            readingNumber = 2;



        }

        if (globalVar2.length()<20 && readingNumber == 2){

            ProgressBar progressBar = findViewById(R.id.readings_progress);
            progressBar.setProgress(((Integer)globalVar.length()/20*50) + 50);

        } else if(globalVar2.length()==20 && readingNumber == 2){


            Button startButton = findViewById(R.id.start_btn);
            startButton.setEnabled(true);
            startButton.setText("Restart");

            ProgressBar progressBar = findViewById(R.id.readings_progress);
            progressBar.setProgress(100);

            Toast.makeText(this, globalVar2, Toast.LENGTH_SHORT).show();


        }

    }


}
