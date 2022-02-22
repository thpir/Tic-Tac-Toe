package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tictactoe.utils.DBHelper;
import com.example.tictactoe.utils.SharedPreference;
import com.google.android.material.button.MaterialButton;

public class SignUp extends AppCompatActivity {

    EditText username, email, password, repeatPassword, recoveryQuestion;
    MaterialButton registerbtn;
    Button signInInstead;
    DBHelper DB;
    private SharedPreference sharedPreference;
    public Boolean loggedInLocal = false;
    public String activeUser = "";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LOGGEDINLOCAL   = "loggedInLocal";
    public static final String ACTIVEUSER = "activeUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sharedPreference = new SharedPreference();

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        repeatPassword = (EditText) findViewById(R.id.repeatPassword);
        recoveryQuestion = (EditText) findViewById(R.id.recoveryQuestion);

        registerbtn = (MaterialButton) findViewById(R.id.registerbtn);
        signInInstead = (Button) findViewById(R.id.singInInstead);
        DB = new DBHelper(this);
        registerbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repass = repeatPassword.getText().toString();
                String mail = email.getText().toString();
                String recovery = recoveryQuestion.getText().toString();

                if (user.equals("") || pass.equals("") || repass.equals("") || mail.equals("") || recovery.equals(""))
                    Toast.makeText(SignUp.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else  {
                    if (pass.equals(repass)) {
                        Boolean checkuser = DB.checkusername(user);
                        if (checkuser == false) {
                            Boolean insert = DB.insertData(user, pass, mail, recovery);
                            if (insert == true) {
                                Toast.makeText(SignUp.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                loggedInLocal = true;
                                activeUser = user;
                                saveToSharedPrefs();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), User.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignUp.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignUp.this, "User already exist! Please sign in", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUp.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        signInInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAccountActivity();
            }
        });

    }

    public void goToAccountActivity() {
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
    }

    public void saveToSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(LOGGEDINLOCAL, loggedInLocal);
        editor.putString(ACTIVEUSER, activeUser);

        editor.apply();
    }
}