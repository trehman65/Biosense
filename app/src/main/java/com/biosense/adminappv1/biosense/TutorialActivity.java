package com.biosense.adminappv1.biosense;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        getSupportActionBar().hide();

    }

    public void next(View view){

        Intent prevIntent = getIntent();


        Intent i = new Intent(this, ReadingsActivity.class);
        i.putExtra("PERSON_ID", prevIntent.getStringExtra("PERSON_ID"));
        startActivity(i);

    }
}
