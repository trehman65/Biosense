package com.biosense.adminappv1.biosense;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class splashScreen extends AppCompatActivity {

    public static final int MULTIPLE_PERMISSIONS = 10;

    String[] permissions= new String[]{

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        if (checkPermissions()){
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                    Intent i = new Intent(getBaseContext(), WelcomeActivity.class);

                    startActivity(i);
                }
            }, 2000);
        }

    }

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {

                            Intent i = new Intent(getBaseContext(), WelcomeActivity.class);

                            startActivity(i);
                        }
                    }, 2000);
                } else {
                    Toast.makeText(this, "Permission Not Granted. Cant Run App.", Toast.LENGTH_LONG).show();
                    finishAndRemoveTask();
                }
                return;
            }
        }
    }

}
