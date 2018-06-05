package com.biosense.adminappv1.biosense;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class BioDataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bio_sense_database.db";
    private static final int DATABASE_VERSION = 1;



    public BioDataHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE BioData(ID VARCHAR, Weight VARCHAR, Height VARCHAR, DOB VARCHAR, Gender VARCHAR);");
        sqLiteDatabase.execSQL("CREATE TABLE VitalSigns(ID VARCHAR, TimeStamp VARCHAR, BMI VARCHAR, BMR VARCHAR, TBW VARCHAR, FFM VARCHAR, FM VARCHAR, SMM VARCHAR, percBF VARCHAR, FAT VARCHAR, BM VARCHAR, Weight VARCHAR, R1 VARCHAR, R2 VARCHAR, R3 VARCHAR, R4 VARCHAR, R5 VARCHAR);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //VitalSigns Methods
    public boolean idExistsInVitalSigns(String ID){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM VitalSigns WHERE ID ==" + ID, null);


        if (cursor.getCount() == 0){
            db.close();
            return false;
        } else{
            db.close();
            return true;
        }

    }
    public boolean timestampExistsInVitalSigns(String timestamp){

        SQLiteDatabase db = this.getReadableDatabase();
        String TIME_STAMP = timestamp;
        Cursor cursor= db.query("VitalSigns", new String[]{"TimeStamp"}, "TimeStamp" +" = ?", new String[]{timestamp},  null, null, null);


        if (cursor.getCount() == 0){
            db.close();
            return false;
        } else{
            db.close();
            return true;
        }

    }
    public void addDataToVitalSigns(String ID, String TimeStamp, String BMI, String BMR, String TBW, String FFM, String FM, String SMM, String percBF, String FAT, String BM, String Weight, String R1, String R2, String R3, String R4, String R5){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ID", ID);
        values.put("Weight", Weight);
        values.put("TimeStamp", TimeStamp);

        values.put("BMI", BMI);
        values.put("BMR", BMR);
        values.put("TBW", TBW);
        values.put("FFM", FFM);
        values.put("FM", FM);
        values.put("SMM", SMM);
        values.put("percBF", percBF);
        values.put("FAT", FAT);
        values.put("BM", BM);
        values.put("R1", R1);
        values.put("R2", R2);
        values.put("R3", R3);
        values.put("R4", R4);
        values.put("R5", R5);

        db.insert("VitalSigns", null, values);
        db.close();
    }
    public List getDataFromVitalSigns(String ID, String Column){

        List<String> temp = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM VitalSigns WHERE ID ==" + ID, null);

        int max = cursor.getCount();

        Log.i("VSDebug", String.valueOf(max));
        try {
            if (cursor.moveToFirst()) {
                do {
                    temp.add(cursor.getString(cursor.getColumnIndex(Column)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
        }

        db.close();
        return temp;
    }
    public void updateDataInVitalSigns(String ID, String TimeStamp, String BMI, String BMR, String TBW, String FFM, String FM, String SMM, String percBF, String FAT, String BM, String Weight, String R1, String R2, String R3, String R4, String R5){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ID", ID);
        values.put("Weight", Weight);
        values.put("TimeStamp", TimeStamp);

        values.put("BMI", BMI);
        values.put("BMR", BMR);
        values.put("TBW", TBW);
        values.put("FFM", FFM);
        values.put("FM", FM);
        values.put("SMM", SMM);
        values.put("percBF", percBF);
        values.put("FAT", FAT);
        values.put("BM", BM);
        values.put("R1", R1);
        values.put("R2", R2);
        values.put("R3", R3);
        values.put("R4", R4);
        values.put("R5", R5);

        db.update("VitalSigns", values, "ID="+ID, null);
        db.close();
    }
    public List getAllDataFromVitalSigns(String Column){

        List<String> temp = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM VitalSigns", null);

        int max = cursor.getCount();

        Log.i("VSDebug", String.valueOf(max));
        try {
            if (cursor.moveToFirst()) {
                do {
                    temp.add(cursor.getString(cursor.getColumnIndex(Column)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
        }

        db.close();
        return temp;

    }
    public String getIDFromTimeStamp(String timeStamp){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.query("VitalSigns", new String[]{"TimeStamp"}, "TimeStamp" +" = ?", new String[]{timeStamp},  null, null, null);

        String temp = "";

        Log.i("IDFromTimeStamp", String.valueOf(cursor.getCount()));

        try {
            if (cursor.moveToFirst()) {
                do {
                    temp = cursor.getString(cursor.getColumnIndex("ID"));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
        }

        Log.i("IDFromTimeStamp", temp);

        db.close();
        return temp;

    }

    //BioData Methods
    public boolean idExistsInBioData(String ID){


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM BioData WHERE ID ==" + ID, null);


        if (cursor.getCount() == 0){
            db.close();
            return false;
        } else{
            db.close();
            return true;
        }
    }
    public void addDataToBioData(String ID, String Weight, String Height, String DOB, String Gender){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ID", ID);
        values.put("Weight", Weight);
        values.put("Height", Height);
        values.put("DOB", DOB);
        values.put("Gender", Gender);

        db.insert("BioData", null, values);
        db.close();
    }
    public String getDataFromBioData(String ID, String Column){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM BioData WHERE ID ==" + ID, null);
        cursor.moveToFirst();
        String temp = cursor.getString(cursor.getColumnIndex(Column));

        db.close();
        return temp;
    }
    public void updateDataInBioData(String ID, String Weight, String Height, String DOB, String Gender){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ID", ID);
        values.put("Weight", Weight);
        values.put("Height", Height);
        values.put("DOB", DOB);
        values.put("Gender", Gender);

        db.update("BioData", values, "ID=" + ID, null);
        db.close();

    }


}
