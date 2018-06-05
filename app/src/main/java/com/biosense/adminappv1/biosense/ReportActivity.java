package com.biosense.adminappv1.biosense;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.PdfPTable;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private String ID;
    private String Age;
    private String Height;
    private String Weight;
    private String Gender;
    private String ReportDate;

    private double r1;
    private double r2;
    private double r3;
    private double r4;
    private double r5;

    //Calculations
    private String TBW;
    private String FFM;
    private String FAT;
    private String SMM;
    private String percBF;
    private String BMR;
    private String BMI;
    private String FM;
    private String BM;

    //Image
    private Bitmap weightBitmap;
    private Bitmap smmBitmap;
    private Bitmap fatBitmap;
    private Bitmap percBFBitmap;
    private Bitmap bmiBitmap;

    private Bitmap rightHandSideValues;
    private Bitmap calorieExpenditureTable;

    private String viewPDF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getSupportActionBar().hide();

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        getFromPreviousIntent();
        getFromBioData();
        getFromVitalSigns();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                insideGenerateReport();
                Toast.makeText(ReportActivity.this, "Report Saved", Toast.LENGTH_LONG).show();

                if (viewPDF.equalsIgnoreCase("true"))
                    viewPDFFile();
                else
                    emailPDFFile();


            }
        }, 1000);



        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }


    //Buttons

    public void bitmapsave(View view){

        Typeface arialR = Typeface.createFromAsset(getAssets(), "fonts/ArialR.ttf");
        Typeface arialB = Typeface.createFromAsset(getAssets(), "fonts/ArialB.ttf");

        RelativeLayout relativeLayout1 = findViewById(R.id.idForSaving);



        BMISet();
        //File file = saveBitmap(ReportActivity.this, relativeLayout, "BMI");
        bmiBitmap = getBitmapFromView(relativeLayout1);


        //TBWSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "TBW");

        SMMSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "SMM");
        smmBitmap = getBitmapFromView(relativeLayout1);

        FATSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "FAT");
        fatBitmap = getBitmapFromView(relativeLayout1);

        percFATSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "percFAT");
        percBFBitmap = getBitmapFromView(relativeLayout1);

        WeightSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "Weight");
        weightBitmap = getBitmapFromView(relativeLayout1);


        RelativeLayout relativeLayout2 = findViewById(R.id.idForSaving2);


        TextView pdfBMRTitle = findViewById(R.id.pdf_bmr_title);
        TextView pdfBMRValue = findViewById(R.id.pdf_bmr_value);
        TextView pdfCalorieTitle = findViewById(R.id.pdf_calorie_title);
        TextView pdfCalorieValue = findViewById(R.id.pdf_calorie_value);
        TextView calorieExpTitle = findViewById(R.id.calorieexp_title);

        pdfBMRTitle.setTypeface(arialB);
        pdfBMRValue.setTypeface(arialR);
        pdfCalorieTitle.setTypeface(arialB);
        pdfCalorieValue.setTypeface(arialR);
        calorieExpTitle.setTypeface(arialB);


        pdfBMRValue.setText("    " + String.format("%.1f", Double.parseDouble(BMR)));

        Double dailyCalorieIntake = 0.0;

        if (Gender.equalsIgnoreCase("F")){
            dailyCalorieIntake = (10*Double.parseDouble(Weight)) + (6.25*Double.parseDouble(Height)) - (5*Double.parseDouble(Age)) - (161*1.3);
        }else{
            dailyCalorieIntake = (10*Double.parseDouble(Weight)) + (6.25*Double.parseDouble(Height)) - (5*Double.parseDouble(Age)) + (5*1.3);
        }

        pdfCalorieValue.setText(String.format("%.1f", dailyCalorieIntake));
        rightHandSideValues = getBitmapFromView(relativeLayout2);

        //File file = saveBitmap(ReportActivity.this, relativeLayout2, "Calorie");

        TextView bicyclingValue = findViewById(R.id.bicycling_value);
        TextView aerobicsValue = findViewById(R.id.aerobics_value);
        TextView runningValue = findViewById(R.id.running_value);
        TextView skiingValue = findViewById(R.id.skiing_value);
        TextView swimmingValue = findViewById(R.id.swimming_value);
        TextView walkingValue = findViewById(R.id.walking_value);
        TextView joggingValue = findViewById(R.id.jogging_value);

        bicyclingValue.setText(String.format("%.0f", 0.0175*6.0*Double.parseDouble(Weight)*30));
        aerobicsValue.setText(String.format("%.0f", 0.0175*6.0*Double.parseDouble(Weight)*30));
        runningValue.setText(String.format("%.0f", 0.0175*8.0*Double.parseDouble(Weight)*30));
        skiingValue.setText(String.format("%.0f", 0.0175*7.0*Double.parseDouble(Weight)*30));
        swimmingValue.setText(String.format("%.0f", 0.0175*6.0*Double.parseDouble(Weight)*30));
        walkingValue.setText(String.format("%.0f", 0.0175*2.5*Double.parseDouble(Weight)*30));
        joggingValue.setText(String.format("%.0f", 0.0175*6.5*Double.parseDouble(Weight)*30));

        RelativeLayout relativeLayout3 = findViewById(R.id.idForSaving3);
        calorieExpenditureTable = getBitmapFromView(relativeLayout3);
        File file = saveBitmap(ReportActivity.this, relativeLayout3, "CalorieExp");


        //if(file != null){
        //    Log.i("TAG", "Drawing Saved");
        //}else{
        //    Log.i("TAG", "Error occured");
        //}

        try {
            createPDF();
        } catch (IOException e){

            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the document");

        }catch (DocumentException e){

            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the document");

        }
    }




    //Set Value in View
    public void BMISet(){

        double BMIValue = Double.parseDouble(BMI);

        TextView oneValue = findViewById(R.id.onevalue);
        TextView twoValue = findViewById(R.id.twovalue);
        TextView valueShow = findViewById(R.id.pdfvalueshow);

        oneValue.setText("18");
        twoValue.setText("25");
        valueShow.setText(String.format("%.1f", BMIValue));

        SeekBar seekBar = findViewById(R.id.seekbar);
        int progress;
        if (BMIValue < 11) {

            progress = 0;

        } else if (BMIValue > 32){

            progress = 100;

        }else {

            progress = (int) ((BMIValue-11)*4.71);

        }


        seekBar.setProgress(progress);
    }

    public void TBWSet(){

        double TBWValue = Double.parseDouble(TBW);
        double WeightValue = Double.parseDouble(Weight);

        TextView oneValue = findViewById(R.id.onevalue);
        TextView twoValue = findViewById(R.id.twovalue);
        TextView valueShow = findViewById(R.id.pdfvalueshow);
        SeekBar seekBar = findViewById(R.id.seekbar);

        int lowValue;
        int highValue;
        int diff;
        int minValue;
        int maxValue;
        int progress;

        if(Gender.equalsIgnoreCase("M")){

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

        if (TBWValue < minValue){
            progress = 0;
        } else if(TBWValue > maxValue){
            progress = 100;
        }else{
            double subtraction = TBWValue-minValue;
            double factor = (double) 33/diff;
            double progressPut = subtraction*factor;

            progress = (int) progressPut;
        }

        oneValue.setText(String.valueOf(lowValue));
        twoValue.setText(String.valueOf(highValue));
        valueShow.setText(String.format("%.1f", TBWValue));
        seekBar.setProgress(progress);



    }

    public void SMMSet(){

        double SMMValue = Double.parseDouble(SMM);
        double WeightValue = Double.parseDouble(Weight);
        int AgeValue = Integer.parseInt(Age);

        double SMMLow = 0.0;
        double SMMHigh = 0.0;

        int lowValue;
        int highValue;
        int diff;
        int minValue;
        int maxValue;
        int progress;

        TextView oneValue = findViewById(R.id.onevalue);
        TextView twoValue = findViewById(R.id.twovalue);
        TextView valueShow = findViewById(R.id.pdfvalueshow);
        SeekBar seekBar = findViewById(R.id.seekbar);

        if(Gender.equalsIgnoreCase("M")){

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

        if (SMMValue < minValue){
            progress = 0;
        } else if(SMMValue > maxValue){
            progress = 100;
        }else{
            double subtraction = SMMValue-minValue;
            double factor = (double) 33/diff;
            double progressPut = subtraction*factor;

            progress = (int) progressPut;
        }

        oneValue.setText(String.valueOf(lowValue));
        twoValue.setText(String.valueOf(highValue));
        valueShow.setText(String.format("%.1f", SMMValue));
        seekBar.setProgress(progress);

    }

    public void FATSet(){

        double FATValue = Double.parseDouble(FAT);
        double WeightValue = Double.parseDouble(Weight);
        int AgeValue = Integer.parseInt(Age);

        double FATLow = 0.0;
        double FATHigh = 0.0;

        int lowValue;
        int highValue;
        int diff;
        int minValue;
        int maxValue;
        int progress;

        TextView oneValue = findViewById(R.id.onevalue);
        TextView twoValue = findViewById(R.id.twovalue);
        TextView valueShow = findViewById(R.id.pdfvalueshow);
        SeekBar seekBar = findViewById(R.id.seekbar);

        if(Gender.equalsIgnoreCase("M")){

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

        if (FATValue < minValue){
            progress = 0;
        } else if(FATValue > maxValue){
            progress = 100;
        }else{

            double subtraction = FATValue-minValue;
            double factor = (double) 33/diff;
            double progressPut = subtraction*factor;

            progress = (int) progressPut;


        }

        oneValue.setText(String.valueOf(lowValue));
        twoValue.setText(String.valueOf(highValue));
        valueShow.setText(String.format("%.1f", FATValue));
        seekBar.setProgress(progress);




    }

    public void percFATSet(){

        double percFATValue = Double.parseDouble(percBF);
        int AgeValue = Integer.parseInt(Age);

        int lowValue = 33;
        int highValue = 66;
        int diff;
        int minValue;
        int maxValue;
        int progress;

        TextView oneValue = findViewById(R.id.onevalue);
        TextView twoValue = findViewById(R.id.twovalue);
        TextView valueShow = findViewById(R.id.pdfvalueshow);
        SeekBar seekBar = findViewById(R.id.seekbar);

        if(Gender.equalsIgnoreCase("M")){

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

        if (percFATValue < minValue){
            progress = 0;
        } else if(percFATValue > maxValue){
            progress = 100;
        }else{
            double subtraction = percFATValue-minValue;
            double factor = (double) 33/diff;
            double progressPut = subtraction*factor;

            progress = (int) progressPut;
        }

        oneValue.setText(String.valueOf(lowValue));
        twoValue.setText(String.valueOf(highValue));
        valueShow.setText(String.format("%.1f", percFATValue));
        seekBar.setProgress(progress);

    }

    public void WeightSet(){

        double WeightValue = Double.parseDouble(Weight);
        double HeightValue = Integer.parseInt(Height);

        double lowValue = 33;
        double highValue = 66;
        int diff;
        int minValue;
        int maxValue;
        int progress;

        TextView oneValue = findViewById(R.id.onevalue);
        TextView twoValue = findViewById(R.id.twovalue);
        TextView valueShow = findViewById(R.id.pdfvalueshow);
        SeekBar seekBar = findViewById(R.id.seekbar);

        if(Gender.equalsIgnoreCase("M")){

            if (HeightValue>137.16){
                lowValue = 28.5;
                highValue = 34.9;
            }

            if (HeightValue>139.7){
                lowValue = 30.8;
                highValue = 38.1;
            }

            if (HeightValue>142.24){
                lowValue = 33.5;
                highValue = 40.8;
            }
            if (HeightValue>144.78){
                lowValue = 35.8;
                highValue = 43.9;
            }
            if (HeightValue>147.32){
                lowValue = 38.5;
                highValue = 46.72;
            }
            if (HeightValue>149.86){
                lowValue = 40.8;
                highValue = 49.9;
            }
            if (HeightValue>152.4){
                lowValue = 43;
                highValue = 53;
            }
            if (HeightValue>154.94){
                lowValue = 45.8;
                highValue = 55.8;
            }
            if (HeightValue>157.48){
                lowValue = 48;
                highValue = 58.9;
            }
            if (HeightValue>160.02){
                lowValue = 50.8;
                highValue = 61.6;
            }
            if (HeightValue>162.56){
                lowValue = 53;
                highValue = 64.8;
            }
            if (HeightValue>165.1){
                lowValue = 55.3;
                highValue = 68;
            }
            if (HeightValue>167.64){
                lowValue = 58;
                highValue = 70.7;
            }
            if (HeightValue>170.18){
                lowValue = 60.3;
                highValue = 73.9;
            }
            if (HeightValue>172.72){
                lowValue = 63;
                highValue = 76.6;
            }
            if (HeightValue>175.26){
                lowValue = 65.3;
                highValue = 79.8;
            }
            if (HeightValue>177.8){
                lowValue = 67.5;
                highValue = 83;
            }
            if (HeightValue>180.34){
                lowValue = 70.3;
                highValue = 85.7;
            }
            if (HeightValue>182.88){
                lowValue = 72.5;
                highValue = 88.9;
            }
            if (HeightValue>185.42){
                lowValue = 75.2;
                highValue = 91.6;
            }
            if (HeightValue>187.96){
                lowValue = 77.5;
                highValue = 94.8;
            }
            if (HeightValue>190.5){
                lowValue = 79.8;
                highValue = 97.9;
            }
            if (HeightValue>193.04){
                lowValue = 82.5;
                highValue = 100.6;
            }


        }else{

            if (HeightValue>137.16){
                lowValue = 28.5;
                highValue = 34.9;
            }

            if (HeightValue>139.7){
                lowValue = 30.8;
                highValue = 37.6;
            }

            if (HeightValue>142.24){
                lowValue = 32.6;
                highValue = 39.9;
            }
            if (HeightValue>144.78){
                lowValue = 34.9;
                highValue = 42.6;
            }
            if (HeightValue>147.32){
                lowValue = 36.7;
                highValue = 44.9;
            }
            if (HeightValue>149.86){
                lowValue = 39;
                highValue = 47.6;
            }
            if (HeightValue>152.4){
                lowValue = 40.8;
                highValue = 49.8;
            }
            if (HeightValue>154.94){
                lowValue = 43;
                highValue = 52.6;
            }
            if (HeightValue>157.48){
                lowValue = 44.9;
                highValue = 54.8;
            }
            if (HeightValue>160.02){
                lowValue = 47.1;
                highValue = 57.6;
            }
            if (HeightValue>162.56){
                lowValue = 48.9;
                highValue = 59.8;
            }
            if (HeightValue>165.1){
                lowValue = 51.2;
                highValue = 60.3;
            }
            if (HeightValue>167.64){
                lowValue = 53;
                highValue = 64.8;
            }
            if (HeightValue>170.18){
                lowValue = 55.3;
                highValue = 67.5;
            }
            if (HeightValue>172.72){
                lowValue = 57.15;
                highValue = 69.8;
            }
            if (HeightValue>175.26){
                lowValue = 59.4;
                highValue = 72.5;
            }
            if (HeightValue>177.8){
                lowValue = 61.2;
                highValue = 74.8;
            }
            if (HeightValue>180.34){
                lowValue = 63.5;
                highValue = 77.5;
            }
            if (HeightValue>182.88){
                lowValue = 65.3;
                highValue = 79.8;
            }

        }

        diff = (int) (highValue-lowValue);

        minValue = (int)lowValue-diff;
        maxValue = (int)highValue+diff;

        if (WeightValue < minValue){
            progress = 0;
        } else if(WeightValue > maxValue){
            progress = 100;
        }else{
            double subtraction = WeightValue-minValue;
            double factor = (double) 33/diff;
            double progressPut = subtraction*factor;

            progress = (int) progressPut;
        }

        oneValue.setText(String.valueOf((int)lowValue));
        twoValue.setText(String.valueOf((int)highValue));
        valueShow.setText(String.format("%.1f", WeightValue));
        seekBar.setProgress(progress);


    }


    //Functions to get Image of View

    public Bitmap getBitmapFromView(View view){

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);

        Drawable bgDrawable = view.getBackground();

        if(bgDrawable != null){

            bgDrawable.draw(canvas);

        }else{

            canvas.drawColor(Color.WHITE);

        }

        view.draw(canvas);

        return returnedBitmap;
    }
    public File saveBitmap(Context context, View drawView, String typeOfFile){

        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Biosense");
        if(!pictureFileDir.exists()){

            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save image");
            return null;

        }

        String filename = pictureFileDir.getPath() + File.separator + typeOfFile + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);

        try{
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e){

            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image");

        }

        return pictureFile;

    }

    //Creating PDF
    public void createPDF() throws DocumentException, IOException{

        File docsFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Biosense");
        if(!docsFileDir.exists()){

            boolean isDirectoryCreated = docsFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save image");


        }


        File pdfFile = new File(docsFileDir.getAbsolutePath(), "Report.pdf");
        FileOutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);


        Font regular = new Font(Font.FontFamily.HELVETICA, 12);
        Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font title = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font semititle = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font range = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
        Image image;

        document.open();

        /*-----------------TITLE----------------------*/
        document.add(new Paragraph("Body Composition Report", title));

        /*-----------------BioSense Logo----------------------*/

        try {
            document.open();
            Drawable d = getResources().getDrawable(R.drawable.logoreport);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            image = Image.getInstance(stream.toByteArray());
            image.scaleAbsolute(77.5f, 25f);
            image.setAbsolutePosition(480f, 770f);
            document.add(image);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*-----------------BioData----------------------*/

        DottedLineSeparator dottedLine = new DottedLineSeparator();
        dottedLine.setOffset(-50);
        dottedLine.setGap(0f);
        document.add(dottedLine);

        document.add(new Phrase("\n"));


        document.add(new Chunk("ID: ", bold));
        document.add(new Chunk(ID, regular));

        document.add(new Chunk("  Age: ", bold));
        document.add(new Chunk(Age + " yrs", regular));

        document.add(new Chunk("  Height: ", bold));
        document.add(new Chunk(Height + " cm", regular));

        document.add(new Chunk("  Gender: ", bold));
        document.add(new Chunk(Gender, regular));

        document.add(new Chunk("  Date: ", bold));
        document.add(new Chunk(ReportDate, regular));

        dottedLine.setOffset(-52);
        dottedLine.setGap(0f);
        document.add(dottedLine);

        /*-----------------Body Composition Analysis Table----------------------*/

        BaseColor color1 = new BaseColor(255, 240, 170);
        BaseColor color2 = new BaseColor(176, 229, 124);
        BaseColor color3 = new BaseColor(180, 216, 231);

        document.add(new Paragraph("\nBody Composition Analysis", semititle));

        PdfPTable table = new PdfPTable(6);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setSpacingBefore(50f);
        table.setWidthPercentage(100);

        PdfPCell cell1 = new PdfPCell(new Paragraph(""));
        cell1.setColspan(2);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setBorderColor(BaseColor.WHITE);

        PdfPCell cell2 = new PdfPCell(new Paragraph("Values\n", bold));
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorderColor(BaseColor.WHITE);

        PdfPCell cell3 = new PdfPCell(new Paragraph("Total Body Water\n", bold));
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setBorderColor(BaseColor.WHITE);

        PdfPCell cell4 = new PdfPCell(new Paragraph("Lean Body Mass\n", bold));
        cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell4.setBorderColor(BaseColor.WHITE);

        PdfPCell cell5 = new PdfPCell(new Paragraph("Weight\n", bold));
        cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell5.setBorderColor(BaseColor.WHITE);

        PdfPCell cell6 = new PdfPCell (new Paragraph("Total Body Water  ", bold));
        cell6.setColspan(2);
        cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell6.setBorderColor(BaseColor.WHITE);

        PdfPCell cell7 = new PdfPCell(new Paragraph(String.format("%.1f", Double.parseDouble(TBW)) + "\n" + TBWRange(), range));

        cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell7.setHorizontalAlignment(Element.ALIGN_CENTER);


        PdfPCell cell8 = new PdfPCell(new Paragraph(String.format("%.1f", Double.parseDouble(TBW))));
        cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell8.setBackgroundColor(color1);

        PdfPCell cell9 = new PdfPCell(new Paragraph(String.format("%.1f", Double.parseDouble(Weight) - Double.parseDouble(FAT))));
        cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell9.setBackgroundColor(color2);

        PdfPCell cell10 = new PdfPCell(new Paragraph(String.format("%.1f", Double.parseDouble(Weight))));
        cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell10.setBackgroundColor(color3);

        PdfPCell cell11 = new PdfPCell (new Paragraph("Dry Lean Mass  ", bold));
        cell11.setColspan(2);
        cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell11.setBorderColor(BaseColor.WHITE);


        PdfPCell cell12 = new PdfPCell(new Paragraph(String.format("%.1f", Double.parseDouble(Weight) - (Double.parseDouble(TBW) + Double.parseDouble(FAT))), range));
        cell12.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell12.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell13 = new PdfPCell(new Paragraph(""));
        cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell13.setBackgroundColor(color2);

        PdfPCell cell14 = new PdfPCell(new Paragraph(""));
        cell14.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell14.setBackgroundColor(color2);

        PdfPCell cell15 = new PdfPCell(new Paragraph(""));
        cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell15.setBackgroundColor(color3);


        PdfPCell cell16 = new PdfPCell (new Paragraph("Body Fat Mass  ", bold));
        cell16.setColspan(2);
        cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell16.setBorderColor(BaseColor.WHITE);

        PdfPCell cell17 = new PdfPCell(new Paragraph(String.format("%.1f", Double.parseDouble(FAT)) + "\n" + FATRange(), range));
        cell17.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell17.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell18 = new PdfPCell(new Paragraph(""));
        cell18.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell18.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell18.setBackgroundColor(color3);

        PdfPCell cell19 = new PdfPCell(new Paragraph(""));
        cell19.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell19.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell19.setBackgroundColor(color3);

        PdfPCell cell20 = new PdfPCell(new Paragraph(""));
        cell20.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell20.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell20.setBackgroundColor(color3);

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        table.addCell(cell6);
        table.addCell(cell7);
        table.addCell(cell8);
        table.addCell(cell9);
        table.addCell(cell10);
        table.addCell(cell11);
        table.addCell(cell12);
        table.addCell(cell13);
        table.addCell(cell14);
        table.addCell(cell15);
        table.addCell(cell16);
        table.addCell(cell17);
        table.addCell(cell18);
        table.addCell(cell19);
        table.addCell(cell20);

        document.add(table);

        /*-----------------Muscle Fat Analysis----------------------*/

        document.add(new Paragraph("Muscle Fat Analysis", semititle));
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Weight: ", regular));
        document.add(Chunk.NEWLINE);
        document.add(new Chunk("\n"));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        weightBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image weightImage = Image.getInstance(stream.toByteArray());
        weightImage.scaleAbsolute(320f, 27.11f);

        document.add(weightImage);

        document.add(new Paragraph("Skeletal Muscle Mass: ", regular));
        document.add(Chunk.NEWLINE);
        document.add(new Chunk("\n"));

        stream = new ByteArrayOutputStream();
        smmBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image smmImage = Image.getInstance(stream.toByteArray());
        smmImage.scaleAbsolute(320f, 27.11f);

        document.add(smmImage);

        document.add(new Paragraph("Body Fat: ", regular));
        document.add(Chunk.NEWLINE);
        document.add(new Chunk("\n"));

        stream = new ByteArrayOutputStream();
        fatBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image fatImage = Image.getInstance(stream.toByteArray());
        fatImage.scaleAbsolute(320f, 27.11f);

        document.add(fatImage);

        /*-----------------Obesity Analysis----------------------*/

        document.add(new Paragraph("Obesity Analysis", semititle));
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Body Mass Index: ", regular));
        document.add(Chunk.NEWLINE);
        document.add(new Chunk("\n"));

        stream = new ByteArrayOutputStream();
        bmiBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image BMIImage = Image.getInstance(stream.toByteArray());
        BMIImage.scaleAbsolute(320f, 27.11f);

        document.add(BMIImage);

        document.add(new Paragraph("Percentage Body Fat: ", regular));
        document.add(Chunk.NEWLINE);
        document.add(new Chunk("\n"));

        stream = new ByteArrayOutputStream();
        percBFBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image percBFImage = Image.getInstance(stream.toByteArray());
        percBFImage.scaleAbsolute(320f, 27.11f);

        document.add(percBFImage);

        /*-----------------Things At Right Side----------------------*/

        stream = new ByteArrayOutputStream();
        rightHandSideValues.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image rightHandSideValues = Image.getInstance(stream.toByteArray());
        rightHandSideValues.scaleAbsolute(175f, 62.94f);
        rightHandSideValues.setAbsolutePosition(375f, 500f);

        document.add(rightHandSideValues);

        stream = new ByteArrayOutputStream();
        calorieExpenditureTable.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image calorieExpenditureTable = Image.getInstance(stream.toByteArray());
        calorieExpenditureTable.scaleAbsolute(200f, 103.62f);
        calorieExpenditureTable.setAbsolutePosition(375f, 380f);

        document.add(calorieExpenditureTable);

        try {
            Drawable d1 = getResources().getDrawable(R.drawable.reportinfo);
            BitmapDrawable bitDw1 = ((BitmapDrawable) d1);
            Bitmap bmp1 = bitDw1.getBitmap();
            stream = new ByteArrayOutputStream();
            bmp1.compress(Bitmap.CompressFormat.PNG, 100, stream);
            image = Image.getInstance(stream.toByteArray());

            image.setAbsolutePosition(368f, 90f);
            image.scaleAbsolute(200f, 274f);
            document.add(image);

        } catch (Exception e) {
            e.printStackTrace();
        }

        document.close();

    }


    //Supporting Functions

    public void getFromBioData() {


        BioDataHelper bioDataHelper = new BioDataHelper(this);
        Log.i("BioDataDebug", "ID: " + String.valueOf(ID));

        if(bioDataHelper.idExistsInBioData(ID)){

            Log.i("BioDataDebug", "ID Exists");

            Height = bioDataHelper.getDataFromBioData(ID, "Height");

            Gender = bioDataHelper.getDataFromBioData(ID, "Gender");

            String DOB = bioDataHelper.getDataFromBioData(ID, "DOB");
            String[] separated = DOB.split("/");

            Age = String.valueOf(getAge(Integer.parseInt(separated[2]), Integer.parseInt(separated[1]), Integer.parseInt(separated[0])));

        } else{

            Log.i("BioDataDebug", "ID Does not exist");

        }



    }

    public void getFromVitalSigns(){

        BioDataHelper bioDataHelper = new BioDataHelper(this);

        List<String> TimeStampVS;
        List<String> BMIVS;
        List<String> BMRVS;
        List<String> TBWVS;
        List<String> FFMVS;
        List<String> FMVS;
        List<String> SMMVS;
        List<String> percBFVS;
        List<String> FATVS;
        List<String> BMVS;
        List<String> weightVS;
        List<String> R1VS;
        List<String> R2VS;
        List<String> R3VS;
        List<String> R4VS;
        List<String> R5VS;

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

            if (TimeStampVS.get(i).toString().equals(ReportDate)){
                count = i;
            }

        }

        if (count == -1) {

            Toast.makeText(this, "Couldn't Fetch Data From Database", Toast.LENGTH_LONG).show();
        }else{

            BMI = BMIVS.get(count).toString();
            BMR = BMRVS.get(count).toString();
            TBW = TBWVS.get(count).toString();
            FFM = FFMVS.get(count).toString();
            FM = FMVS.get(count).toString();
            SMM = SMMVS.get(count).toString();
            percBF = percBFVS.get(count).toString();
            FAT = FATVS.get(count).toString();
            BM = BMIVS.get(count).toString();

            Weight = weightVS.get(count).toString();

            r1 = Double.parseDouble(R1VS.get(count).toString());
            r2 = Double.parseDouble(R2VS.get(count).toString());
            r3 = Double.parseDouble(R3VS.get(count).toString());
            r4 = Double.parseDouble(R4VS.get(count).toString());
            r5 = Double.parseDouble(R5VS.get(count).toString());

        }

        Log.i("getFromVitalSignsDebug", String.valueOf(count));








    }

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

    public void getFromPreviousIntent(){

        Intent prevIntent = getIntent();
        ID = prevIntent.getStringExtra("PERSON_ID");
        ReportDate = prevIntent.getStringExtra("REPORT_DATE");
        viewPDF = prevIntent.getStringExtra("VIEW_PDF");

    }

    public String TBWRange(){

        double TBWValue = Double.parseDouble(TBW);
        double WeightValue = Double.parseDouble(Weight);

        Double lowValue;
        Double highValue;

        if(Gender.equalsIgnoreCase("M")){

            lowValue = (0.50*WeightValue);
            highValue = (0.65*WeightValue);


        } else {

            lowValue = (0.45*WeightValue);
            highValue = (0.60*WeightValue);

        }


        return "(" + String.format("%.1f", lowValue) + " - " + String.format("%.1f", highValue) + ")";

    }

    public String FATRange(){

        double FATValue = Double.parseDouble(FAT);
        double WeightValue = Double.parseDouble(Weight);
        double AgeValue = Double.parseDouble(Age);

        double FATLow = 0.0;
        double FATHigh = 0.0;

        double lowValue;
        double highValue;


        if(Gender.equalsIgnoreCase("M")){

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

        lowValue = (FATLow*WeightValue);
        highValue = (FATHigh*WeightValue);


        return "(" + String.format("%.1f", lowValue) + " - " + String.format("%.1f", highValue) + ")";


    }

    public void insideGenerateReport(){

        Typeface arialR = Typeface.createFromAsset(getAssets(), "fonts/ArialR.ttf");
        Typeface arialB = Typeface.createFromAsset(getAssets(), "fonts/ArialB.ttf");

        RelativeLayout relativeLayout1 = findViewById(R.id.idForSaving);



        BMISet();
        //File file = saveBitmap(ReportActivity.this, relativeLayout, "BMI");
        bmiBitmap = getBitmapFromView(relativeLayout1);


        //TBWSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "TBW");

        SMMSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "SMM");
        smmBitmap = getBitmapFromView(relativeLayout1);

        FATSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "FAT");
        fatBitmap = getBitmapFromView(relativeLayout1);

        percFATSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "percFAT");
        percBFBitmap = getBitmapFromView(relativeLayout1);

        WeightSet();
        //file = saveBitmap(ReportActivity.this, relativeLayout, "Weight");
        weightBitmap = getBitmapFromView(relativeLayout1);


        RelativeLayout relativeLayout2 = findViewById(R.id.idForSaving2);


        TextView pdfBMRTitle = findViewById(R.id.pdf_bmr_title);
        TextView pdfBMRValue = findViewById(R.id.pdf_bmr_value);
        TextView pdfCalorieTitle = findViewById(R.id.pdf_calorie_title);
        TextView pdfCalorieValue = findViewById(R.id.pdf_calorie_value);
        TextView calorieExpTitle = findViewById(R.id.calorieexp_title);

        pdfBMRTitle.setTypeface(arialB);
        pdfBMRValue.setTypeface(arialR);
        pdfCalorieTitle.setTypeface(arialB);
        pdfCalorieValue.setTypeface(arialR);
        calorieExpTitle.setTypeface(arialB);


        pdfBMRValue.setText(String.format("%.1f", Double.parseDouble(BMR)) + "cal" + "cal");

        Double dailyCalorieIntake = 0.0;

        if (Gender.equalsIgnoreCase("F")){
            dailyCalorieIntake = (10*Double.parseDouble(Weight)) + (6.25*Double.parseDouble(Height)) - (5*Double.parseDouble(Age)) - (161*1.3);
        }else{
            dailyCalorieIntake = (10*Double.parseDouble(Weight)) + (6.25*Double.parseDouble(Height)) - (5*Double.parseDouble(Age)) + (5*1.3);
        }

        pdfCalorieValue.setText(String.format("%.1f", dailyCalorieIntake));
        rightHandSideValues = getBitmapFromView(relativeLayout2);

        //File file = saveBitmap(ReportActivity.this, relativeLayout2, "Calorie");

        TextView bicyclingValue = findViewById(R.id.bicycling_value);
        TextView aerobicsValue = findViewById(R.id.aerobics_value);
        TextView runningValue = findViewById(R.id.running_value);
        TextView skiingValue = findViewById(R.id.skiing_value);
        TextView swimmingValue = findViewById(R.id.swimming_value);
        TextView walkingValue = findViewById(R.id.walking_value);
        TextView joggingValue = findViewById(R.id.jogging_value);

        bicyclingValue.setText(String.format("%.0f", 0.0175*6.0*Double.parseDouble(Weight)*30));
        aerobicsValue.setText(String.format("%.0f", 0.0175*6.0*Double.parseDouble(Weight)*30));
        runningValue.setText(String.format("%.0f", 0.0175*8.0*Double.parseDouble(Weight)*30));
        skiingValue.setText(String.format("%.0f", 0.0175*7.0*Double.parseDouble(Weight)*30));
        swimmingValue.setText(String.format("%.0f", 0.0175*6.0*Double.parseDouble(Weight)*30));
        walkingValue.setText(String.format("%.0f", 0.0175*2.5*Double.parseDouble(Weight)*30));
        joggingValue.setText(String.format("%.0f", 0.0175*6.5*Double.parseDouble(Weight)*30));

        RelativeLayout relativeLayout3 = findViewById(R.id.idForSaving3);
        calorieExpenditureTable = getBitmapFromView(relativeLayout3);
        File file = saveBitmap(ReportActivity.this, relativeLayout3, "CalorieExp");



        try {
            createPDF();
        } catch (IOException e){

            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the document");

        }catch (DocumentException e){

            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the document");

        }

    }

    public void viewPDFFile(){

        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Biosense/Report.pdf");

        if (pdfFile.exists()) //Checking for the file is exist or not
        {
            Uri path = Uri.fromFile(pdfFile);
            Intent objIntent = new Intent(Intent.ACTION_VIEW);
            objIntent.setDataAndType(path, "application/pdf");
            objIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
            objIntent.setFlags(Intent. FLAG_ACTIVITY_NO_HISTORY);
            objIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(objIntent);//Staring the pdf viewer
            this.finish();
        } else {

            Toast.makeText(getBaseContext(), "PDF File Doesn't Exist", Toast.LENGTH_SHORT).show();

        }

    }

    public void emailPDFFile(){

        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Biosense/Report.pdf");

        if (pdfFile.exists()) //Checking for the file is exist or not
        {
            Uri path = Uri.fromFile(pdfFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Biosense Test Report");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "BioSense Test Report");
            shareIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(shareIntent);
            this.finish();

        } else {

            Toast.makeText(getBaseContext(), "PDF File Doesn't Exist", Toast.LENGTH_SHORT).show();

        }

    }


}
