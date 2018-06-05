package com.biosense.adminappv1.biosense;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class BiodataActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Calendar calendar;
    private int year = 1997, month = 0, day = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biodata);

        getSupportActionBar().hide();

        Typeface brandonG = Typeface.createFromAsset(getAssets(), "fonts/Brandon.otf");
        Typeface openS = Typeface.createFromAsset(getAssets(), "fonts/OSLight.ttf");

        TextView biodataTitle = findViewById(R.id.biodata_title);
        TextView biodataDetail = findViewById(R.id.biodata_detail);
        EditText weightInput = findViewById(R.id.weight_input);
        EditText heightInput = findViewById(R.id.height_input);
        EditText dateOfBirthInput = findViewById(R.id.dateofbirth_input);
        RadioButton femaleBtn = findViewById(R.id.female);
        RadioButton maleBtn = findViewById(R.id.male);

        biodataTitle.setTypeface(brandonG);
        biodataDetail.setTypeface(openS);

        weightInput.setTypeface(openS);
        heightInput.setTypeface(openS);
        dateOfBirthInput.setTypeface(openS);
        femaleBtn.setTypeface(openS);
        maleBtn.setTypeface(openS);

        //Database Management

        BioDataHelper bioDataHelper = new BioDataHelper(this);
        //bioDataHelper.addDataToBioData("123", "21", "21", "13/1/1997", "M");

        Intent intent = getIntent();
        String ID = intent.getStringExtra("PERSON_ID");

        if (bioDataHelper.idExistsInBioData(ID)){

            biodataDetail.setText("Data of the person already exists. Change the data if needed and press next.");
            weightInput.setText(bioDataHelper.getDataFromBioData(ID, "Weight"));
            heightInput.setText(bioDataHelper.getDataFromBioData(ID, "Height"));
            dateOfBirthInput.setText(bioDataHelper.getDataFromBioData(ID, "DOB"));

            switch (bioDataHelper.getDataFromBioData(ID, "Gender")){
                case "M":

                    femaleBtn.setChecked(false);
                    maleBtn.setChecked(true);
                    break;

                case "F":

                    femaleBtn.setChecked(true);
                    maleBtn.setChecked(false);
                    break;

                default:
                    Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
            }



        }


    }

    public void male_btn(View view){

        RadioButton femaleBtn = findViewById(R.id.female);
        femaleBtn.setChecked(false);

        EditText weightInput = findViewById(R.id.weight_input);
        EditText heightInput = findViewById(R.id.height_input);

        weightInput.clearFocus();
        heightInput.clearFocus();
    }

    public void female_btn(View view){

        RadioButton maleBtn = findViewById(R.id.male);
        maleBtn.setChecked(false);

        EditText weightInput = findViewById(R.id.weight_input);
        EditText heightInput = findViewById(R.id.height_input);

        weightInput.clearFocus();
        heightInput.clearFocus();

    }

    public void biodata_next(View view){

        BioDataHelper bioDataHelper = new BioDataHelper(this);

        Intent intent = getIntent();
        String ID = intent.getStringExtra("PERSON_ID");

        EditText weightInput = findViewById(R.id.weight_input);
        EditText heightInput = findViewById(R.id.height_input);
        EditText dateOfBirthInput = findViewById(R.id.dateofbirth_input);
        RadioButton femaleBtn = findViewById(R.id.female);
        RadioButton maleBtn = findViewById(R.id.male);


        if (femaleBtn.isChecked() == true){
            String gender = "F";
        } else if (maleBtn.isChecked() == true){
            String gender = "M";
        }

        if (!areEmpty()){

            String weight = weightInput.getText().toString();
            String height = heightInput.getText().toString();
            String dateOfBirth = dateOfBirthInput.getText().toString();
            String gender = "N";

            if (femaleBtn.isChecked() == true){
                gender = "F";
            } else if (maleBtn.isChecked() == true){
                gender = "M";
            }

            if (bioDataHelper.idExistsInBioData(ID)){

                bioDataHelper.updateDataInBioData(ID, weight, height, dateOfBirth, gender);
                Toast.makeText(this, "Data updated.", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getBaseContext(), TutorialActivity.class);
                Intent prevIntent = getIntent();
                i.putExtra("PERSON_ID", prevIntent.getStringExtra("PERSON_ID"));
                startActivity(i);

            } else{

                bioDataHelper.addDataToBioData(ID, weight, height, dateOfBirth, gender);
                Toast.makeText(this, "Data saved.", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getBaseContext(), TutorialActivity.class);
                Intent prevIntent = getIntent();
                i.putExtra("PERSON_ID", prevIntent.getStringExtra("PERSON_ID"));
                startActivity(i);

            }
        }



    }

    public boolean areEmpty(){

        EditText weightInput = findViewById(R.id.weight_input);
        EditText heightInput = findViewById(R.id.height_input);
        EditText dateOfBirthInput = findViewById(R.id.dateofbirth_input);
        RadioButton femaleBtn = findViewById(R.id.female);
        RadioButton maleBtn = findViewById(R.id.male);

        boolean output = true;

        if (heightInput.getText().toString().length() == 0){
            Toast.makeText(this, "Please enter the height.", Toast.LENGTH_SHORT).show();
        } else if (weightInput.getText().toString().length() == 0){
            Toast.makeText(this, "Please enter the weight.", Toast.LENGTH_SHORT).show();
        } else if (dateOfBirthInput.getText().toString().length() == 0){
            Toast.makeText(this, "Please select the date of birth.", Toast.LENGTH_SHORT).show();
        } else if (femaleBtn.isChecked() == false && maleBtn.isChecked() == false){
            Toast.makeText(this, "Please select the gender.", Toast.LENGTH_SHORT).show();
        } else {
            output = false;
        }

        return output;

    }






    //Calendar View

    @SuppressWarnings("deprecation")
    public void dateofbirth_touch(View view){

        EditText weightInput = findViewById(R.id.weight_input);
        EditText heightInput = findViewById(R.id.height_input);

        weightInput.clearFocus();
        heightInput.clearFocus();

        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {

                    arg2 = arg2 + 1;

                    EditText editText = findViewById(R.id.dateofbirth_input);
                    editText.setText(arg3 + "/" + arg2 + "/" + arg1);
                }
    };


}
