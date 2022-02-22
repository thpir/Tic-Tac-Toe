package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.utils.DBHelper;
import com.example.tictactoe.utils.SharedPreference;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;

public class Account extends AppCompatActivity {

    TextView username, password;
    MaterialButton loginbtn;
    Button signUpButton, forgotpwdButton;
    DBHelper DB;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleButton;
    ImageView facebookButton;
    CallbackManager callbackManager;

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

        //Declarations for Google Sign in
        googleButton = findViewById(R.id.googleButton);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        //Declarations for Facebook Sign in
        facebookButton = findViewById(R.id.facebookButton);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        navigateToUserActivity();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        //If already logged in with Google account, jump to User activity
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct!=null) {
            navigateToUserActivity();
        }

        //If already logged in with local account, jump to User activity
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loggedInLocal = sharedPreferences.getBoolean(LOGGEDINLOCAL, false);
        if (loggedInLocal) {
            navigateToUserActivity();
        }

        //If already logged in with Facebook account, jump to User activity
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null && accessToken.isExpired() == false) {
            navigateToUserActivity();
        }

        //Declarations of TextViews
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginbtn = (MaterialButton) findViewById(R.id.loginbtn);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        forgotpwdButton = (Button) findViewById(R.id.forgotpwd);
        DB = new DBHelper(this);

        //Google login
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singIn();
            }
        });

        //Facebook login
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Account.this, Arrays.asList("public_profile"));
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Google login
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToUserActivity();

            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

        //Facebook login
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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

    public void singIn() {
        Intent singInIntent = gsc.getSignInIntent();
        startActivityForResult(singInIntent, 1000);
    }
}