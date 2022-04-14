package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tictactoe.utils.DBHelper;
import com.example.tictactoe.utils.SharedPreference;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends AppCompatActivity {

    TextView name;
    Button signoutbtn;
    DBHelper DB;

    private SharedPreference sharedPreference;
    public boolean loggedInLocal;
    public String activeUser;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LOGGEDINLOCAL = "loggedInLocal";
    public static final String ACTIVEUSER = "activeUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        sharedPreference = new SharedPreference();
        name = findViewById(R.id.username);
        signoutbtn = findViewById(R.id.signoutbtn);
        DB = new DBHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loggedInLocal = sharedPreferences.getBoolean(LOGGEDINLOCAL, false);
        if (loggedInLocal){
            activeUser = sharedPreferences.getString(ACTIVEUSER, "");
            name.setText(activeUser);
        }

        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                loggedInLocal = sharedPreferences.getBoolean(LOGGEDINLOCAL, false);
                activeUser = sharedPreferences.getString(ACTIVEUSER, "");
                loggedInLocal = false;
                activeUser = "";
                saveToSharedPrefs();
                openAccount();
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

    public void openAccount() {
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
        finish();
    }
}