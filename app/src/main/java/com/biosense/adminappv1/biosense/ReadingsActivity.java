package com.biosense.adminappv1.biosense;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class ReadingsActivity extends AppCompatActivity implements ArduinoListener {

    private Arduino arduino;
    private TextView textView;
    public String receivedValues;
    private ProgressBar progressBar;
    public int i = 0;
    public CountDownTimer mCountDownTimer;
    public Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readings);

        nextButton = findViewById(R.id.reading_next_btn);
        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.textView);
        arduino = new Arduino(this);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Brandon.otf");
        Typeface openS = Typeface.createFromAsset(getAssets(), "fonts/OSLight.ttf");

        getSupportActionBar().hide();
        TextView readingsTitle = (TextView) findViewById(R.id.readings_title);
        readingsTitle.setTypeface(custom_font);
        TextView mainActivityDetail = findViewById(R.id.readings_detail);
        mainActivityDetail.setTypeface(openS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        arduino.setArduinoListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        display("Arduino attached.");
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
        display("Arduino detached.");

    }

    @Override
    public void onArduinoMessage(byte[] bytes) {
        saveData(new String(bytes));
    }

    @Override
    public void onArduinoOpened() {
        String str = "7";
        arduino.send(str.getBytes());
    }

    @Override
    public void onUsbPermissionDenied() {
        display("Permission denied... New attempt in 3 sec");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arduino.reopen();
            }
        }, 3000);
    }

    public void display(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(message);
            }
        });
    }

    public void saveData(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assembleString(message);
            }
        });
    }

    public void get(View view){
        String str = "5";
        arduino.send(str.getBytes());

        receivedValues = "";
        display("Getting readings");
        progressBar.setVisibility(View.VISIBLE);

        i=0;
        setProgressToFifty();
    }

    public void assembleString(String message){
        receivedValues = receivedValues + message;

        if(receivedValues.length() == 20){
            setProgressToHundred();
            progressBar.setProgress(50);
        } else if (receivedValues.length() == 40){

            display("All values received.");
            progressBar.setProgress(100);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    public void reading_next(View view){

        Intent prevIntent = getIntent();

        Intent i = new Intent (this, ResultsActivity.class);
        i.putExtra("PERSON_ID", prevIntent.getStringExtra("PERSON_ID"));
        i.putExtra("VOLTAGE_VALUES", receivedValues);
        i.putExtra("RECALL_DATE", "");
        i.putExtra("TIME_STAMP", String.valueOf(System.currentTimeMillis()));
        startActivity(i);
        this.finish();
    }

    //Timer
    public void setProgressToFifty(){

        mCountDownTimer=new CountDownTimer(4000,100) {

            @Override
            public void onTick(long millisUntilFinished) {

                i++;
                progressBar.setProgress((int)i*50/(4000/100));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                progressBar.setProgress(50);
            }
        };
        mCountDownTimer.start();

    }
    public void setProgressToHundred(){

        mCountDownTimer=new CountDownTimer(4000,100) {

            @Override
            public void onTick(long millisUntilFinished) {

                i++;
                progressBar.setProgress(((int)i*50/(4000/100)));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                progressBar.setProgress(100);
            }
        };
        mCountDownTimer.start();

    }

}
