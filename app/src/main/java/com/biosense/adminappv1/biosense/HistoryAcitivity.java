package com.biosense.adminappv1.biosense;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HistoryAcitivity extends AppCompatActivity {

    List<HistoryDate> historyDateList;
    RecyclerView recyclerView;

    private String ID = "3042324406";
    private String AllData = "false";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_acitivity);

        getSupportActionBar().hide();

        //Getting Views

        Typeface brandonG = Typeface.createFromAsset(getAssets(), "fonts/Brandon.otf");
        TextView historyTitle = findViewById(R.id.history_title);
        historyTitle.setTypeface(brandonG);

        //Data From Previous Intent

        Intent intent = getIntent();
        ID = intent.getStringExtra("PERSON_ID");
        AllData = intent.getStringExtra("ALL_DATA");

        //Recycler View Settings

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //Array Declarations

        BioDataHelper bioDataHelper = new BioDataHelper(this);
        historyDateList = new ArrayList<>();
        List<String> TimeStampVS = new ArrayList<>();
        List<String> IDVS = new ArrayList<>();

        //Decision Making

        if (AllData.equalsIgnoreCase("true")){

            TimeStampVS = bioDataHelper.getAllDataFromVitalSigns("TimeStamp");
            IDVS = bioDataHelper.getAllDataFromVitalSigns("ID");

        } else{

            TimeStampVS = bioDataHelper.getDataFromVitalSigns(ID, "TimeStamp");
            IDVS = bioDataHelper.getDataFromVitalSigns(ID,"ID");

        }



        int count = TimeStampVS.size();

        for (int i =0; i<count; i++){

            historyDateList.add(new HistoryDate(TimeStampVS.get(i)));

        }

        HistoryDateAdapter historyDateAdapter = new HistoryDateAdapter(this, historyDateList, IDVS);

        recyclerView.setAdapter(historyDateAdapter);

        TextView noRecords = findViewById(R.id.no_records);

        if (historyDateList.size() == 0)
            noRecords.setVisibility(View.VISIBLE);


    }
}
