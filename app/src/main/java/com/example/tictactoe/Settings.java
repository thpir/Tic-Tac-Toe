package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.tictactoe.utils.SharedPreference;

public class Settings extends AppCompatActivity {


    private String text;
    private Button button;
    private Switch switch1;
    private Switch switch2;
    private boolean switch1OnOff;
    private boolean switch2OnOff;

    private SharedPreference sharedPreference;
    Activity context = this;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCH1 = "switch1";
    public static final String SWITCH2 = "switch2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        button = findViewById(R.id.SaveSettings);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        sharedPreference = new SharedPreference();

        Spinner mySpinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                R.layout.my_spinner, getResources().getStringArray(R.array.gridSizes));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = mySpinner.getSelectedItem().toString();
                sharedPreference.save(context, text);
                saveDataSwitches();
            }
        });

        loadData();
        updateViews();
    }

    public void saveDataSwitches() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SWITCH1, switch1.isChecked());
        editor.putBoolean(SWITCH2, switch2.isChecked());

        editor.apply();

        Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switch1OnOff = sharedPreferences.getBoolean(SWITCH1, false);
        switch2OnOff = sharedPreferences.getBoolean(SWITCH2, false);
    }

    public void updateViews() {
        switch1.setChecked(switch1OnOff);
        switch2.setChecked(switch2OnOff);
    }
}