package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.utils.DBHelper;
import com.example.tictactoe.utils.SharedPreference;
import com.google.android.material.button.MaterialButton;

public class Account extends AppCompatActivity {

    TextView username, password;
    MaterialButton loginbtn;
    Button signUpButton, forgotpwdButton;
    DBHelper DB;

    private SharedPreference sharedPreference;
    public Boolean loggedInLocal = false;
    public String activeUser = "";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LOGGEDINLOCAL = "loggedInLocal";
    public static final String ACTIVEUSER = "activeUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        sharedPreference = new SharedPreference();

        //If already logged in with local account, jump to User activity
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loggedInLocal = sharedPreferences.getBoolean(LOGGEDINLOCAL, false);
        if (loggedInLocal) {
            navigateToUserActivity();
        }

        //Declarations of TextViews
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginbtn = (MaterialButton) findViewById(R.id.loginbtn);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        forgotpwdButton = (Button) findViewById(R.id.forgotpwd);
        DB = new DBHelper(this);

        //Local login
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("") || pass.equals(""))
                    Toast.makeText(Account.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else {
                    Boolean checkuserpass = DB.checkusernamepassword(user, pass);
                    if (checkuserpass == true) {
                        Toast.makeText(Account.this, "Sing in successful", Toast.LENGTH_SHORT).show();
                        loggedInLocal = true;
                        activeUser = user;
                        saveToSharedPrefs();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), User.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Account.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Create a new local account, jump to SingUp activity
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignupActivity();
            }
        });

        //In case you password it lost, jump to the password recovery page
        forgotpwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPWDRecovery();
            }
        });
    }

    public void navigateToUserActivity() {
        finish();
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
    }

    public void saveToSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(LOGGEDINLOCAL, loggedInLocal);
        editor.putString(ACTIVEUSER, activeUser);

        editor.apply();
    }

    public void goToSignupActivity() {
        finish();
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void goToPWDRecovery() {
        finish();
        Intent intent = new Intent(this, AccountRecovery.class);
        startActivity(intent);
    }
}