package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoe.utils.DBHelper;
import com.example.tictactoe.utils.SharedPreference;

public class AccountRecovery extends AppCompatActivity {

    Button submitButton;
    TextView username, email, recoveryQuestion;
    DBHelper DB;
    private SharedPreference sharedPreference;
    public String usernameToDisplay= "";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USERNAMETODISPLAY = "usernameToDisplay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_recovery);

        sharedPreference = new SharedPreference();

        submitButton = (Button) findViewById(R.id.submitbtn);
        username = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        recoveryQuestion = (TextView) findViewById(R.id.recoveryQuestion);
        DB = new DBHelper(this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String mail = email.getText().toString();
                String recovery = recoveryQuestion.getText().toString();

                Boolean recoverySuccesful = DB.accountrecovery(user, mail, recovery);
                if (recoverySuccesful == true) {
                    Toast.makeText(getApplicationContext(), "It's your lucky day!", Toast.LENGTH_SHORT).show();
                    usernameToDisplay = user;
                    saveToSharedPrefs();
                    navigateToResetPassword();// go to activity that displays username and password
                } else {
                    Toast.makeText(getApplicationContext(), "Nice try...But no...", Toast.LENGTH_SHORT).show();
                    username.setText("");
                    email.setText("");
                    recoveryQuestion.setText("");
                }
            }
        });
    }

    public void navigateToResetPassword() {
        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
        finish();
    }

    public void saveToSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USERNAMETODISPLAY, usernameToDisplay);

        editor.apply();
    }
}