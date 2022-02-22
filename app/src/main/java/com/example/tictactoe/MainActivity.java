/*
Colors used:
    R0, G64, B85 = #004055 (background, dark)
    R0, G133, B176 = #0085B0 (background, light)
    R0, G153 B204 #0099CC (background, buttons)
    R255, G255, B255 = #FFFFFF (text, white)
 */

package com.example.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.utils.SharedPreference;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private ImageButton imageButton;
    private String text;
    private SharedPreference sharedPreference;
    Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.startGame);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreference = new SharedPreference();
                text = sharedPreference.getValue(context);
                if ("3x3".equals(text)) {
                    startGame();
                } else if ("4x4".equals(text)) {
                    startGame4x4();
                } else if ("5x5".equals(text)) {
                    startGame5x5();
                } else {
                    startGame();
                }
            }
        });

        imageButton = findViewById(R.id.rulebook);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { readRules(); }
        });

        imageButton = findViewById(R.id.settings);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openSettings(); }
        });

        imageButton = findViewById(R.id.account);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openAccount(); }
        });

    }

    public void startGame() {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }

    public void startGame4x4() {
        Intent intent = new Intent(this, Game4x4.class);
        startActivity(intent);
    }

    public void startGame5x5() {
        Intent intent = new Intent(this, Game5x5.class);
        startActivity(intent);
    }

    public void readRules() {
        Intent intent = new Intent(this, Rules.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void openAccount() {
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
    }
}