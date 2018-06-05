package com.biosense.adminappv1.biosense;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    //General
    private String readingDate;
    private String recallDate;

    //BioData
    private String ID;
    private int age;
    private double weight;
    private int height;
    private boolean gender;

    //Measurements
    private double r1;
    private double r2;
    private double r3;
    private double r4;
    private double r5;

    private double rArm;
    private double rLeg;
    private double rTrunk;

    //Calculations
    private double TBW;
    private double FFM;
    private double FAT;
    private double SMM;
    private double percBF;
    private double BMR;
    private double BMI;
    private double FM;
    private double BM;

    //Views
    public TextView resultsTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getSupportActionBar().hide();

        resultsTitle = findViewById(R.id.results_title);

        Typeface brandonG = Typeface.createFromAsset(getAssets(), "fonts/Brandon.otf");
        Typeface openS = Typeface.createFromAsset(getAssets(), "fonts/OSRegular.ttf");

        resultsTitle.setTypeface(brandonG);

        dataFromPrevIntent();
        getFromBioData();

        if (recallDate.length() == 0){

            Log.i("OnCreateFunctions", "New Values");
            getResistances();
            performCalculations();
            setValuesInView();


        } else{

            Log.i("OnCreateFunctions", "History" + recallDate);

            readingDate = recallDate;

            getFromVitalSigns();
            setValuesInView();

            Button saveButton = findViewById(R.id.save_button);
            Button pdfButton = findViewById(R.id.pdf_button);

            saveButton.setVisibility(View.GONE);

        }








        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);



    }

    //Setting values
    public void setValuesInView(){

        TextView bmiValue = findViewById(R.id.bmi_value);
        TextView bmrValue = findViewById(R.id.bmr_value);
        TextView tbwValue = findViewById(R.id.tbw_value);
        TextView ffmValue = findViewById(R.id.ffm_value);
        TextView fmValue = findViewById(R.id.fm_value);
        TextView smmValue = findViewById(R.id.smm_value);
        TextView percBFValue = findViewById(R.id.percbf_value);
        TextView bmValue = findViewById(R.id.bm_value);

        bmiValue.setText(String.format("%.1f", BMI));
        bmrValue.setText(String.format("%.1f", BMR));
        tbwValue.setText(String.format("%.1f", TBW));
        ffmValue.setText(String.format("%.1f", FFM));
        fmValue.setText(String.format("%.1f", FM));
        smmValue.setText(String.format("%.1f", SMM));
        percBFValue.setText(String.format("%.1f", percBF));
        bmValue.setText(String.format("%.1f", BM));

        TextView idView = findViewById(R.id.id_view);
        TextView ageView = findViewById(R.id.age_view);
        TextView weightView = findViewById(R.id.weight_view);
        TextView heightView = findViewById(R.id.height_view);
        TextView genderView = findViewById(R.id.gender_view);

        idView.setText("0" + ID);
        ageView.setText(String.valueOf(age) + " yrs");
        weightView.setText(String.valueOf(weight) + " kg");
        heightView.setText(String.valueOf(height) + " cm");

        if(gender) {
            genderView.setText("M");
        } else{
            genderView.setText("F");
        }

        setRanges();

    }

    //Perform Calculations
    public void performCalculations() {

        //Constants
        double c1 = 0.07038448251;
        double c2 = 0.02143662415;
        double c3 = -0.009244479;
        double c4 = 1;
        double c5 = 1;
        double c6 = 1;

        double heightSquared = (double) height * height;
        double heightInMetres = height / 100.0;
        double heightInMetresSquared = heightInMetres * heightInMetres;

        double sex;
        if (gender) {

            sex = 1;
            BMR = 66 + ((double) 13.75 * weight) + ((double) 5 * height) - ((double) 6.8 * age);

        } else {

            sex = 0;
            BMR = 655 + ((double) 9.6 * weight) + ((double) 1.8 * height) - ((double) 4.7 * age);
        }

        TBW = (c1*((heightSquared/r1) + (heightSquared/r2))) + (c2*(heightSquared/r5)) + (c3*((heightSquared/r3) + (heightSquared/r4)));
        FFM = (TBW / 0.73);
        FAT = weight - FFM;
        SMM = 0.74374582 * TBW;
        percBF = ((weight - FFM) * 100) / weight;
        BMI = (double) weight / heightInMetresSquared;
        BM = 0.31 * TBW;
        FM = weight - FFM;


    }

    //Gets data from prevIntent
    public void getResistances() {

        Intent prevIntent = getIntent();
        String rawVoltages = prevIntent.getStringExtra("VOLTAGE_VALUES");

        double voltageValues[] = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

        for (int i = 0; i < 10; i++) {

            voltageValues[i] = Double.parseDouble(rawVoltages.substring(i * 4, (i * 4) + 4));

        }

        for (int i = 0; i < 10; i++) {

            voltageValues[i] = (voltageValues[i] / 1024) * 4.096;

        }

        double resistanceValues[] = {0.0, 0.0, 0.0, 0.0, 0.0};

        for (int i = 0; i < 5; i++) {

            resistanceValues[i] = (voltageValues[i] + voltageValues[i + 5]) / 2;
            resistanceValues[i] = resistanceValues[i] / 0.0008;
        }

        r1 = resistanceValues[0];
        r2 = resistanceValues[1];
        r3 = resistanceValues[2];
        r4 = resistanceValues[3];
        r5 = resistanceValues[4];

        rArm = (r1 * r2) / (r1 + r2);
        rLeg = (r3 * r4) / (r3 + r4);
        rTrunk = r5;

        TextView rAndV = findViewById(R.id.randv);
        rAndV.setText("V1: " + String.format("%.5f", voltageValues[0]) + " " +
                "V2: " + String.format("%.5f", voltageValues[1]) + " " +
                "V3: " + String.format("%.5f", voltageValues[2]) + " " +
                "V4: " + String.format("%.5f", voltageValues[3]) + " " +
                "V5: " + String.format("%.5f", voltageValues[4]) + " " +
                "V1: " + String.format("%.5f", voltageValues[5]) + " " +
                "V2: " + String.format("%.5f", voltageValues[6]) + " " +
                "V3: " + String.format("%.5f", voltageValues[7]) + " " +
                "V4: " + String.format("%.5f", voltageValues[8]) + " " +
                "V5: " + String.format("%.5f", voltageValues[9]) +
                "R1: " + String.format("%.1f", r1) + " " +
                "R2: " + String.format("%.1f", r2) + " " +
                "R3: " + String.format("%.1f", r3) + " " +
                "R4: " + String.format("%.1f", r4) + " " +
                "R5: " + String.format("%.1f", r5) );


    }
    public void dataFromPrevIntent(){

        Intent prevIntent = getIntent();
        ID = prevIntent.getStringExtra("PERSON_ID");
        recallDate = prevIntent.getStringExtra("RECALL_DATE");


        Date date = new Date(Long.parseLong(prevIntent.getStringExtra("TIME_STAMP")));
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        readingDate = format.format(date);

    }


    //Gets Data Database
    public void getFromBioData() {


        BioDataHelper bioDataHelper = new BioDataHelper(this);
        Log.i("BioDataDebug", "ID: " + String.valueOf(ID));

        if(bioDataHelper.idExistsInBioData(ID)){

            Log.i("BioDataDebug", "ID Exists");

            weight = Double.parseDouble(bioDataHelper.getDataFromBioData(ID, "Weight"));
            height = Integer.parseInt(bioDataHelper.getDataFromBioData(ID, "Height"));

            switch (bioDataHelper.getDataFromBioData(ID, "Gender")) {
                case "M":
                    gender = true;
                    break;
                case "F":
                    gender = false;
                    break;
            }

            String DOB = bioDataHelper.getDataFromBioData(ID, "DOB");
            String[] separated = DOB.split("/");

            age = getAge(Integer.parseInt(separated[2]), Integer.parseInt(separated[1]), Integer.parseInt(separated[0]));

        } else{

            Log.i("BioDataDebug", "ID Does not exist");

        }



    }
    public void getFromVitalSigns(){

        BioDataHelper bioDataHelper = new BioDataHelper(this);

        List<String> TimeStampVS = new ArrayList<>();
        List<String> BMIVS = new ArrayList<>();
        List<String> BMRVS = new ArrayList<>();
        List<String> TBWVS = new ArrayList<>();
        List<String> FFMVS = new ArrayList<>();
        List<String> FMVS = new ArrayList<>();
        List<String> SMMVS = new ArrayList<>();
        List<String> percBFVS = new ArrayList<>();
        List<String> FATVS = new ArrayList<>();
        List<String> BMVS = new ArrayList<>();
        List<String> weightVS = new ArrayList<>();
        List<String> R1VS = new ArrayList<>();
        List<String> R2VS = new ArrayList<>();
        List<String> R3VS = new ArrayList<>();
        List<String> R4VS = new ArrayList<>();
        List<String> R5VS = new ArrayList<>();

        TimeStampVS = bioDataHelper.getDataFromVitalSigns(ID, "TimeStamp");
        BMIVS = bioDataHelper.getDataFromVitalSigns(ID, "BMI");
        BMRVS = bioDataHelper.getDataFromVitalSigns(ID, "BMR");
        TBWVS = bioDataHelper.getDataFromVitalSigns(ID, "TBW");
        FFMVS = bioDataHelper.getDataFromVitalSigns(ID, "FFM");
        FMVS = bioDataHelper.getDataFromVitalSigns(ID, "FM");
        SMMVS = bioDataHelper.getDataFromVitalSigns(ID, "SMM");
        percBFVS = bioDataHelper.getDataFromVitalSigns(ID, "percBF");
        FATVS = bioDataHelper.getDataFromVitalSigns(ID, "FAT");
        BMVS = bioDataHelper.getDataFromVitalSigns(ID, "BM");
        weightVS = bioDataHelper.getDataFromVitalSigns(ID, "Weight");
        R1VS = bioDataHelper.getDataFromVitalSigns(ID, "R1");
        R2VS = bioDataHelper.getDataFromVitalSigns(ID, "R2");
        R3VS = bioDataHelper.getDataFromVitalSigns(ID, "R3");
        R4VS = bioDataHelper.getDataFromVitalSigns(ID, "R4");
        R5VS = bioDataHelper.getDataFromVitalSigns(ID, "R5");

        int count = -1;
        Log.i("getFromVitalSignsDebug", String.valueOf(TimeStampVS.size()));




        for (int i = 0; i<TimeStampVS.size(); i++){

            if (TimeStampVS.get(i).toString().equals(recallDate)){
                count = i;
            }

        }

        if (count == -1) {

            Toast.makeText(this, "Couldn't Fetch Data From Database", Toast.LENGTH_LONG).show();
        }else{

            BMI = Double.parseDouble(BMIVS.get(count).toString());
            BMR = Double.parseDouble(BMRVS.get(count).toString());
            TBW = Double.parseDouble(TBWVS.get(count).toString());
            FFM = Double.parseDouble(FFMVS.get(count).toString());
            FM = Double.parseDouble(FMVS.get(count).toString());
            SMM = Double.parseDouble(SMMVS.get(count).toString());
            percBF = Double.parseDouble(percBFVS.get(count).toString());
            FAT = Double.parseDouble(FATVS.get(count).toString());
            BM = Double.parseDouble(BMIVS.get(count).toString());

            weight = Double.parseDouble(weightVS.get(count).toString());

            r1 = Double.parseDouble(R1VS.get(count).toString());
            r2 = Double.parseDouble(R2VS.get(count).toString());
            r3 = Double.parseDouble(R3VS.get(count).toString());
            r4 = Double.parseDouble(R4VS.get(count).toString());
            r5 = Double.parseDouble(R5VS.get(count).toString());

        }

        Log.i("getFromVitalSignsDebug", String.valueOf(count));








    }

    //Support Functions
    public int getAge(int DOByear, int DOBmonth, int DOBday) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOByear;

        if (DOBmonth > currentMonth) {
            --age;
        } else if (DOBmonth == currentMonth) {
            if (DOBday > todayDay) {
                --age;
            }
        }
        return age;
    }


    //Buttons
    public void save(View view){

        BioDataHelper bioDataHelper = new BioDataHelper(this);
        Log.i("Button", "Save pressed");


        if(bioDataHelper.timestampExistsInVitalSigns(readingDate)){

//            bioDataHelper.updateDataInVitalSigns(ID, readingDate, String.valueOf(BMI), String.valueOf(BMR), String.valueOf(TBW), String.valueOf(FFM), String.valueOf(FM), String.valueOf(SMM), String.valueOf(percBF), String.valueOf(FAT), String.valueOf(BM), String.valueOf(weight), String.valueOf(r1), String.valueOf(r2), String.valueOf(r3), String.valueOf(r4), String.valueOf(r5));
            Toast.makeText(this, "Data Updated", Toast.LENGTH_LONG).show();
            Log.i("Button", "Inside if");

        }else{

            bioDataHelper.addDataToVitalSigns(ID, readingDate, String.valueOf(BMI), String.valueOf(BMR), String.valueOf(TBW), String.valueOf(FFM), String.valueOf(FM), String.valueOf(SMM), String.valueOf(percBF), String.valueOf(FM), String.valueOf(BM), String.valueOf(weight), String.valueOf(r1), String.valueOf(r2), String.valueOf(r3), String.valueOf(r4), String.valueOf(r5));
            Toast.makeText(this, "Data Saved", Toast.LENGTH_LONG).show();
            Log.i("Button", "Inside else");

        }

    }

    public void view_pdf(View view){

        Log.i("Button", "PDF pressed");

        BioDataHelper bioDataHelper = new BioDataHelper(this);

        if(bioDataHelper.timestampExistsInVitalSigns(readingDate)){

//            bioDataHelper.updateDataInVitalSigns(ID, readingDate, String.valueOf(BMI), String.valueOf(BMR), String.valueOf(TBW), String.valueOf(FFM), String.valueOf(FM), String.valueOf(SMM), String.valueOf(percBF), String.valueOf(FAT), String.valueOf(BM), String.valueOf(weight), String.valueOf(r1), String.valueOf(r2), String.valueOf(r3), String.valueOf(r4), String.valueOf(r5));
//            Toast.makeText(this, "Data Updated", Toast.LENGTH_LONG).show();
//            Log.i("Button", "Inside if");

        }else{

            bioDataHelper.addDataToVitalSigns(ID, readingDate, String.valueOf(BMI), String.valueOf(BMR), String.valueOf(TBW), String.valueOf(FFM), String.valueOf(FM), String.valueOf(SMM), String.valueOf(percBF), String.valueOf(FAT), String.valueOf(BM), String.valueOf(weight), String.valueOf(r1), String.valueOf(r2), String.valueOf(r3), String.valueOf(r4), String.valueOf(r5));
            Toast.makeText(this, "Data Saved", Toast.LENGTH_LONG).show();
            Log.i("Button", "Inside else");

        }

        Intent i = new Intent(this, ReportActivity.class);
        i.putExtra("PERSON_ID", ID);
        i.putExtra("REPORT_DATE", readingDate);
        i.putExtra("VIEW_PDF", "true");

        startActivity(i);
    }

    public void email_pdf(View view){

        Log.i("Button", "PDF pressed");

        BioDataHelper bioDataHelper = new BioDataHelper(this);

        if(bioDataHelper.timestampExistsInVitalSigns(readingDate)){

//            bioDataHelper.updateDataInVitalSigns(ID, readingDate, String.valueOf(BMI), String.valueOf(BMR), String.valueOf(TBW), String.valueOf(FFM), String.valueOf(FM), String.valueOf(SMM), String.valueOf(percBF), String.valueOf(FAT), String.valueOf(BM), String.valueOf(weight), String.valueOf(r1), String.valueOf(r2), String.valueOf(r3), String.valueOf(r4), String.valueOf(r5));
//            Toast.makeText(this, "Data Updated", Toast.LENGTH_LONG).show();
//            Log.i("Button", "Inside if");

        }else{

            bioDataHelper.addDataToVitalSigns(ID, readingDate, String.valueOf(BMI), String.valueOf(BMR), String.valueOf(TBW), String.valueOf(FFM), String.valueOf(FM), String.valueOf(SMM), String.valueOf(percBF), String.valueOf(FAT), String.valueOf(BM), String.valueOf(weight), String.valueOf(r1), String.valueOf(r2), String.valueOf(r3), String.valueOf(r4), String.valueOf(r5));
            Toast.makeText(this, "Data Saved", Toast.LENGTH_LONG).show();
            Log.i("Button", "Inside else");

        }

        Intent i = new Intent(this, ReportActivity.class);
        i.putExtra("PERSON_ID", ID);
        i.putExtra("REPORT_DATE", readingDate);
        i.putExtra("VIEW_PDF", "false");

        startActivity(i);

    }


    public void setRanges(){

        BMIRangeSet();
        TBWRangeSet();
        SMMRangeSet();
        FATRangeSet();
        percFATRangeSet();

    }


    public void BMIRangeSet(){

        TextView valueShow = findViewById(R.id.bmi_range);
        valueShow.setText("(" + 18 + " - " + 25 + ")");

    }

    public void TBWRangeSet(){

        double TBWValue = TBW;
        double WeightValue = weight;

        int lowValue;
        int highValue;
        int diff;
        int minValue;
        int maxValue;
        int progress;

        if(gender){

            lowValue = (int) (0.50*WeightValue);
            highValue = (int) (0.65*WeightValue);
            diff = highValue-lowValue;

            minValue = lowValue-diff;
            maxValue = highValue+diff;



        } else {

            lowValue = (int) (0.45*WeightValue);
            highValue = (int) (0.60*WeightValue);
            diff = highValue-lowValue;

            minValue = lowValue-diff;
            maxValue = highValue+diff;

        }


        TextView textView = findViewById(R.id.tbw_range);
        textView.setText("(" + String.valueOf(lowValue) + " - " + String.valueOf(highValue) + ")");



    }

    public void SMMRangeSet(){

        double SMMValue = SMM;
        double WeightValue = weight;
        int AgeValue = age;

        double SMMLow = 0.0;
        double SMMHigh = 0.0;

        int lowValue;
        int highValue;
        int diff;
        int minValue;
        int maxValue;
        int progress;


        TextView textView = findViewById(R.id.smm_range);


        if(gender){

            if (AgeValue >= 6 && AgeValue <= 12){
                SMMLow = 0.39;
                SMMHigh = 0.55;
            }

            if (AgeValue >= 13 && AgeValue <= 20){
                SMMLow = 0.37;
                SMMHigh = 0.55;
            }

            if (AgeValue >= 21 && AgeValue <= 40){
                SMMLow = 0.36;
                SMMHigh = 0.55;
            }

            if (AgeValue >= 41 && AgeValue <= 60){
                SMMLow = 0.35;
                SMMHigh = 0.50;
            }

            if (AgeValue >= 61 && AgeValue <= 80){
                SMMLow = 0.33;
                SMMHigh = 0.50;
            }


        }else{

            if (AgeValue >= 6 && AgeValue <= 12){
                SMMLow = 0.35;
                SMMHigh = 0.50;
            }

            if (AgeValue >= 13 && AgeValue <= 20){
                SMMLow = 0.33;
                SMMHigh = 0.50;
            }

            if (AgeValue >= 21 && AgeValue <= 40){
                SMMLow = 0.32;
                SMMHigh = 0.50;
            }

            if (AgeValue >= 41 && AgeValue <= 60){
                SMMLow = 0.31;
                SMMHigh = 0.50;
            }

            if (AgeValue >= 61 && AgeValue <= 80){
                SMMLow = 0.30;
                SMMHigh = 0.50;
            }

        }

        lowValue = (int) (SMMLow*WeightValue);
        highValue = (int) (SMMHigh*WeightValue);
        diff = highValue-lowValue;

        minValue = lowValue-diff;
        maxValue = highValue+diff;

        textView.setText("(" + String.valueOf(lowValue) + " - " + String.valueOf(highValue) + ")");

    }

    public void FATRangeSet(){

        double FATValue = FAT;
        double WeightValue = weight;
        int AgeValue = age;

        double FATLow = 0.0;
        double FATHigh = 0.0;

        int lowValue;
        int highValue;
        int diff;
        int minValue;
        int maxValue;
        int progress;

        TextView textView = findViewById(R.id.fm_range);

        if(gender){

            if (AgeValue >= 7 && AgeValue <= 18){
                FATLow = 0.1;
                FATHigh = 0.2;
            }

            if (AgeValue >= 19 && AgeValue <= 39){
                FATLow = 0.08;
                FATHigh = 0.2;
            }

            if (AgeValue >= 40 && AgeValue <= 59){
                FATLow = 0.11;
                FATHigh = 0.22;
            }

            if (AgeValue >= 60 && AgeValue <= 79){
                FATLow = 0.13;
                FATHigh = 0.25;
            }



        }else{

            if (AgeValue >= 7 && AgeValue <= 18){
                FATLow = 0.15;
                FATHigh = 0.30;
            }

            if (AgeValue >= 19 && AgeValue <= 39){
                FATLow = 0.22;
                FATHigh = 0.33;
            }

            if (AgeValue >= 40 && AgeValue <= 59){
                FATLow = 0.24;
                FATHigh = 0.34;
            }

            if (AgeValue >= 60 && AgeValue <= 79){
                FATLow = 0.25;
                FATHigh = 0.36;
            }

        }

        lowValue = (int) (FATLow*WeightValue);
        highValue = (int) (FATHigh*WeightValue);
        diff = highValue-lowValue;

        minValue = lowValue-diff;
        maxValue = highValue+diff;

        textView.setText("(" + String.valueOf(lowValue) + " - " + String.valueOf(highValue) + ")");

    }

    public void percFATRangeSet(){

        double percFATValue = percBF;
        int AgeValue = age;

        int lowValue = 33;
        int highValue = 66;
        int diff;
        int minValue;
        int maxValue;
        int progress;


        TextView textView = findViewById(R.id.percbf_range);


        if(gender){

            if (AgeValue >= 7 && AgeValue <= 18){
                lowValue = 10;
                highValue = 20;
            }

            if (AgeValue >= 19 && AgeValue <= 39){
                lowValue = 8;
                highValue = 20;
            }

            if (AgeValue >= 40 && AgeValue <= 59){
                lowValue = 11;
                highValue = 22;
            }

            if (AgeValue >= 60 && AgeValue <= 79){
                lowValue = 13;
                highValue = 25;
            }



        }else{

            if (AgeValue >= 7 && AgeValue <= 18){
                lowValue = 15;
                highValue = 30;
            }

            if (AgeValue >= 19 && AgeValue <= 39){
                lowValue = 22;
                highValue = 33;
            }

            if (AgeValue >= 40 && AgeValue <= 59){
                lowValue = 24;
                highValue = 34;
            }

            if (AgeValue >= 60 && AgeValue <= 79){
                lowValue = 25;
                highValue = 36;
            }

        }


        diff = highValue-lowValue;

        minValue = lowValue-diff;
        maxValue = highValue+diff;

        textView.setText("(" + String.valueOf(lowValue) + " - " + String.valueOf(highValue) + ")");

    }




    //For checking the database

    public void copyAppDbToDownloadFolder() throws IOException {
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "bio_sense_database.db"); // for example "my_data_backup.db"
        File currentDB = getApplicationContext().getDatabasePath("bio_sense_database.db"); //databaseName=your current application database name, for example "my_data.db"
        Log.i("Database", "Function body");
        if (currentDB.exists()) {
            Log.i("Database", "If body");
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        copyAppDbToDownloadFolder();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
