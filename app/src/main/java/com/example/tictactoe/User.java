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

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView name;
    Button signoutbtn;
    DBHelper DB;

    private SharedPreference sharedPreference;
    public boolean loggedIn;
    public boolean loggedInLocal;
    public String activeUser;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LOGGEDIN   = "loggedIn";
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
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        //Check which type of account that is signed in (local, Google or Facebook) and retrieve the name of the user
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loggedInLocal = sharedPreferences.getBoolean(LOGGEDINLOCAL, false);
        if (acct!=null) {
            String personName = acct.getGivenName();
            name.setText(personName);
            activeUser = personName;
            saveToSharedPrefs();
        } else if (loggedInLocal){
            activeUser = sharedPreferences.getString(ACTIVEUSER, "");
            name.setText(activeUser);
        } else {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();

            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            try {
                                String firstName = object.getString("first_name");
                                name.setText(firstName);
                                activeUser = firstName;
                                saveToSharedPrefs();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "first_name");
            request.setParameters(parameters);
            request.executeAsync();
        }

        loggedIn = true;
        saveToSharedPrefs();

        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                loggedInLocal = sharedPreferences.getBoolean(LOGGEDINLOCAL, false);
                loggedIn = sharedPreferences.getBoolean(LOGGEDIN, false);
                activeUser = sharedPreferences.getString(ACTIVEUSER, "");
                if (loggedInLocal) {
                    loggedInLocal = false;
                    loggedIn = false;
                    activeUser = "";
                    saveToSharedPrefs();
                    openAccount();
                } else {
                    loggedIn = false;
                    activeUser = "";
                    saveToSharedPrefs();
                    signOut();
                }
            }
        });
    }

    void signOut() {
        //if a Google account is signed in, follow the next steps to log out:
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct!=null) {
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    startActivity(new Intent(User.this, Account.class));
                }
            });
        } else { //if a Facebook account is signed in, follow the next steps to log out:
            LoginManager.getInstance().logOut();
            finish();
            startActivity(new Intent(User.this, Account.class));
        }

    }

    public void saveToSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(LOGGEDINLOCAL, loggedInLocal);
        editor.putBoolean(LOGGEDIN, loggedIn);
        editor.putString(ACTIVEUSER, activeUser);

        editor.apply();
    }

    public void openAccount() {
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
        finish();
    }
}