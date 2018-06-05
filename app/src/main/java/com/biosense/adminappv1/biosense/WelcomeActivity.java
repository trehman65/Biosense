package com.biosense.adminappv1.biosense;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportActionBar().hide();

        Typeface openS = Typeface.createFromAsset(getAssets(), "fonts/OSLight.ttf");

        TextView welcomeDetail = findViewById(R.id.welcome_detail);
        EditText enteredID = findViewById(R.id.enter_id);

        welcomeDetail.setTypeface(openS);
        enteredID.setTypeface(openS);
    }

    public void next(View view){

        EditText ID = findViewById(R.id.enter_id);
        String idEntered = ID.getText().toString();



        if (idEntered.length() == 0){

            Toast.makeText(this, "Please enter patient ID.", Toast.LENGTH_LONG).show();

        } else if (idEntered.length() != 11){
            Toast.makeText(this, "Not a valid mobile phone number.", Toast.LENGTH_LONG).show();
        } else{

            idEntered = idEntered.substring(1, idEntered.length());
            Intent i = new Intent(getBaseContext(), BiodataActivity.class);
            i.putExtra("PERSON_ID", idEntered);
            startActivity(i);

        }


    }

    public void history(View view){

        EditText ID = findViewById(R.id.enter_id);
        String idEntered = ID.getText().toString();



        if (idEntered.length() == 0){

            Toast.makeText(this, "Please enter patient ID.", Toast.LENGTH_LONG).show();

        } else if (idEntered.length() != 11){
            Toast.makeText(this, "Not a valid mobile phone number.", Toast.LENGTH_LONG).show();
        } else{

            idEntered = idEntered.substring(1, idEntered.length());
            Intent i = new Intent(getBaseContext(), HistoryAcitivity.class);
            i.putExtra("PERSON_ID", idEntered);
            i.putExtra("ALL_DATA", "false");
            startActivity(i);

        }

    }

    public void allrecords(View view){



        Intent i = new Intent(getBaseContext(), HistoryAcitivity.class);
        i.putExtra("PERSON_ID", "");
        i.putExtra("ALL_DATA", "true");
        startActivity(i);

    }
}
