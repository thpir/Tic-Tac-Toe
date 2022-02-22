package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoe.utils.DBHelper;
import com.example.tictactoe.utils.SharedPreference;

public class ResetPassword extends AppCompatActivity {

    private Button resetPassBtn;
    public String usernameToDisplay= "";
    public EditText pass, repass;
    public Boolean loggedInLocal = false;
    public String activeUser = "";
    private SharedPreference sharedPreference;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USERNAMETODISPLAY = "usernameToDisplay";
    public static final String LOGGEDINLOCAL = "loggedInLocal";
    public static final String ACTIVEUSER = "activeUser";
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        sharedPreference = new SharedPreference();
        resetPassBtn = findViewById(R.id.resetPassButton);
        pass = findViewById(R.id.password);
        repass = findViewById(R.id.repeatPassword);
        DB = new DBHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        usernameToDisplay = sharedPreferences.getString(USERNAMETODISPLAY, "");

        resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = usernameToDisplay;
                String password = pass.getText().toString();
                String repassword = repass.getText().toString();
                if (password.equals(repassword)) {
                    Boolean checkpasswordupdate = DB.updatepassword(user, password);
                    if (checkpasswordupdate == true) {
                        Toast.makeText(ResetPassword.this, "Password Reset Successful", Toast.LENGTH_SHORT).show();
                        loggedInLocal = true;
                        activeUser = user;
                        saveToSharedPrefs();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), User.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ResetPassword.this, "Password Reset failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPassword.this, "Passwords Not Matching", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveToSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(LOGGEDINLOCAL, loggedInLocal);
        editor.putString(ACTIVEUSER, activeUser);

        editor.apply();
    }

}