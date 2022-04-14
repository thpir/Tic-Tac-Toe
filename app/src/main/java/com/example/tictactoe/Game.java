package com.example.tictactoe;

import android.content.SearchRecentSuggestionsProvider;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class Game extends AppCompatActivity implements View.OnClickListener {
    
    private final Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCount;
    private int player1Points;
    private int player2Points;
    private TextView textViewPlayer1;
    private TextView textViewPlayer2;
    private String TEXTVIEWPLAYER1;
    private String TEXTVIEWPLAYER2;
    private boolean switch1OnOff;
    private boolean switch2OnOff;
    public boolean loggedInLocal;
    public String activeUser;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCH1 = "switch1";
    public static final String SWITCH2 = "switch2";
    public static final String LOGGEDINLOCAL   = "loggedInLocal";
    public static final String ACTIVEUSER = "activeUser";

    GridLayout myGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Here we're going to set the playfield perfectly square and auto-scalable depending on the device its display
        myGridLayout = (GridLayout)findViewById(R.id.playField3x3);

        myGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int pLength;
                        final int MARGIN = 5;

                        int pWidth = myGridLayout.getWidth();
                        int pHeight = myGridLayout.getHeight();

                        if (pWidth>=pHeight) {
                            pLength = pHeight;
                        } else {
                            pLength = pWidth;
                        }
                        ViewGroup.LayoutParams pParams = myGridLayout.getLayoutParams();
                        pParams.width = pLength;
                        pParams.height = pLength;
                        myGridLayout.setLayoutParams(pParams);
                    }
                }
        );

        //Here we're going to check if the switch "keep screen always on" in the settings activity is activated. (=switch2OnOff) If this is the case the screen will be kept on after a longer period of inactivity
        //We're also going to check if the "single player mode" is activated (= switch1OnOff)
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switch1OnOff = sharedPreferences.getBoolean(SWITCH1, false);
        switch2OnOff = sharedPreferences.getBoolean(SWITCH2, false);

        if (switch2OnOff) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Activate screen always on
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Deactivate screen always on
        }
        
        textViewPlayer1 = findViewById(R.id.text_view_p1); //define the text views on the activity screen that were created in the game.xml file
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        textViewPlayer1.setBackgroundColor(Color.rgb(0, 153, 204));

        loggedInLocal = sharedPreferences.getBoolean(LOGGEDINLOCAL, false);
        activeUser = sharedPreferences.getString(ACTIVEUSER, "");

        if (loggedInLocal) {
            TEXTVIEWPLAYER1 = activeUser + " (=x): ";
            textViewPlayer1.setText(TEXTVIEWPLAYER1 + " 0");
        } else {
            TEXTVIEWPLAYER1 = "Player 1 (=X): ";
            textViewPlayer1.setText(TEXTVIEWPLAYER1 + " 0");
        }

        if (switch1OnOff) {
            TEXTVIEWPLAYER2 = "Android (=O): ";
            textViewPlayer2.setText(TEXTVIEWPLAYER2 + " 0");
        } else {
            TEXTVIEWPLAYER2 = "Player 2 (=O): ";
            textViewPlayer2.setText(TEXTVIEWPLAYER2 + " 0");
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID); //define all the buttons in the 3x3 grid that were created in the game.xml file
                buttons[i][j].setOnClickListener(this);
            }
        }

        Button buttonReset = findViewById(R.id.resetButton);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGame();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (!((Button) view).getText().toString().equals("")) {
            return;
        }

        if (!switch1OnOff) {
            if (player1Turn) {
                ((Button) view).setText("X");
            } else {
                ((Button) view).setText("O");
            }

            roundCount++;

            if (checkForWin()) {
                if (player1Turn) {
                    player1Wins();
                } else {
                    player2Wins();
                }
            } else if (roundCount == 9) {
                draw();
            } else {
                player1Turn = !player1Turn;
                setColorPlayersTurn();
            }
        } else {
            ((Button) view).setText("X");

            roundCount++;

            if (checkForWin()) {
                player1Wins();
            } else if (roundCount == 9) {
                draw();
            } else {
                player1Turn = !player1Turn;
            }

            singlePlayerMode();

            roundCount++;

            if (checkForWin()) {
                player2Wins();
            } else if (roundCount == 9) {
                draw();
            } else {
                player1Turn = true;
            }
        }
    }


    private void singlePlayerMode() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        //Attempt to win function
            // Row check

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals("O")
                    && field[i][1].equals("O")
                    && field[i][2].equals("")) {
                buttons[i][2].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals("O")
                    && field[i][1].equals("")
                    && field[i][2].equals("O")) {
                buttons[i][1].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals("")
                    && field[i][1].equals("O")
                    && field[i][2].equals("O")) {
                buttons[i][0].setText("O");
                return;
            }
        }

            // Column check

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals("O")
                    && field[1][i].equals("O")
                    && field[2][i].equals("")) {
                buttons[2][i].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals("O")
                    && field[1][i].equals("")
                    && field[2][i].equals("O")) {
                buttons[1][i].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals("")
                    && field[1][i].equals("O")
                    && field[2][i].equals("O")) {
                buttons[0][i].setText("O");
                return;
            }
        }

            // Diagonal check \

        if (field[0][0].equals("O")
                && field[1][1].equals("O")
                && field[2][2].equals("")) {
            buttons[2][2].setText("O");
            return;
        }

        if (field[0][0].equals("O")
                && field[1][1].equals("")
                && field[2][2].equals("O")) {
            buttons[1][1].setText("O");
            return;
        }

        if (field[0][0].equals("")
                && field[1][1].equals("O")
                && field[2][2].equals("O")) {
            buttons[0][0].setText("O");
            return;
        }

            // Diagonal check /

        if (field[0][2].equals("O")
                && field[1][1].equals("O")
                && field[2][0].equals("")) {
            buttons[2][0].setText("O");
            return;
        }

        if (field[0][2].equals("O")
                && field[1][1].equals("")
                && field[2][0].equals("O")) {
            buttons[1][1].setText("O");
            return;
        }

        if (field[0][2].equals("")
                && field[1][1].equals("O")
                && field[2][0].equals("O")) {
            buttons[0][2].setText("O");
            return;
        }

        // Attempt to Block
            // Row check

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals("X")
                    && field[i][1].equals("X")
                    && field[i][2].equals("")) {
                buttons[i][2].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals("X")
                    && field[i][1].equals("")
                    && field[i][2].equals("X")) {
                buttons[i][1].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals("")
                    && field[i][1].equals("X")
                    && field[i][2].equals("X")) {
                buttons[i][0].setText("O");
                return;
            }
        }

            // Column check

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals("X")
                    && field[1][i].equals("X")
                    && field[2][i].equals("")) {
                buttons[2][i].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals("X")
                    && field[1][i].equals("")
                    && field[2][i].equals("X")) {
                buttons[1][i].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals("")
                    && field[1][i].equals("X")
                    && field[2][i].equals("X")) {
                buttons[0][i].setText("O");
                return;
            }
        }

            // Diagonal check \

        if (field[0][0].equals("X")
                && field[1][1].equals("X")
                && field[2][2].equals("")) {
            buttons[2][2].setText("O");
            return;
        }

        if (field[0][0].equals("X")
                && field[1][1].equals("")
                && field[2][2].equals("X")) {
            buttons[1][1].setText("O");
            return;
        }

        if (field[0][0].equals("")
                && field[1][1].equals("X")
                && field[2][2].equals("X")) {
            buttons[0][0].setText("O");
            return;
        }

            // Diagonal check /

        if (field[0][2].equals("X")
                && field[1][1].equals("X")
                && field[2][0].equals("")) {
            buttons[2][0].setText("O");
            return;
        }

        if (field[0][2].equals("X")
                && field[1][1].equals("")
                && field[2][0].equals("X")) {
            buttons[1][1].setText("O");
            return;
        }

        if (field[0][2].equals("")
                && field[1][1].equals("X")
                && field[2][0].equals("X")) {
            buttons[0][2].setText("O");
            return;
        }

        // Try two-in-a-row _ & |

        for (int i = 0; i < 3; i++) { // check buttons in column 0 and see if the button to the right is still empty
            if (field[i][0].equals("O")
                    && field[i][1].equals("")) {
                buttons[i][1].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) { // check buttons in column 1 and see if the button to the left is still empty
            if (field[i][1].equals("O")
                    && field[i][0].equals("")) {
                buttons[i][0].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) { // check buttons in column 1 and see if the button to the right is still empty
            if (field[i][1].equals("O")
                    && field[i][2].equals("")) {
                buttons[i][2].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) { // check buttons in column 2 and see if the button to the left is still empty
            if (field[i][2].equals("O")
                    && field[i][1].equals("")) {
                buttons[i][1].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) { // check buttons in row 0 and see if the button below is still empty
            if (field[0][i].equals("O")
                    && field[1][i].equals("")) {
                buttons[1][i].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) { // check buttons in row 1 and see if the button above is still empty
            if (field[1][i].equals("O")
                    && field[0][i].equals("")) {
                buttons[0][i].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) { // check buttons in row 1 and see if the button above is still empty
            if (field[1][i].equals("O")
                    && field[2][i].equals("")) {
                buttons[2][i].setText("O");
                return;
            }
        }

        for (int i = 0; i < 3; i++) { // check buttons in row 2 and see if the button above is still empty
            if (field[2][i].equals("O")
                    && field[1][i].equals("")) {
                buttons[1][i].setText("O");
                return;
            }
        }

            //Try two-in-a-row / & \

        if (field[0][0].equals("O") // check button 00 and check if button 11 is still empty
                && field[1][1].equals("")){
            buttons[1][1].setText("O");
            return;
        }

        if (field[1][1].equals("O") // check button 11 and check if button 00 is still empty
                && field[0][0].equals("")){
            buttons[0][0].setText("O");
            return;
        }

        if (field[1][1].equals("O") // check button 11 and check if button 22 is still empty
                && field[2][2].equals("")){
            buttons[2][2].setText("O");
            return;
        }

        if (field[2][2].equals("O") // check button 22 and check if button 11 is still empty
                && field[1][1].equals("")){
            buttons[1][1].setText("O");
            return;
        }

        if (field[0][2].equals("O") // check button 02 and check if button 11 is still empty
                && field[1][1].equals("")){
            buttons[1][1].setText("O");
            return;
        }

        if (field[1][1].equals("O") // check button 11 and check if button 02 is still empty
                && field[0][2].equals("")){
            buttons[0][2].setText("O");
            return;
        }

        if (field[1][1].equals("O") // check button 11 and check if button 20 is still empty
                && field[2][0].equals("")){
            buttons[2][0].setText("O");
            return;
        }

        if (field[2][0].equals("O") // check button 20 and check if button 11 is still empty
                && field[1][1].equals("")){
            buttons[1][1].setText("O");
            return;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++){
                if (field[i][j].equals("")) {
                    buttons[i][j].setText("O");
                    return;
                }
            }
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) { // check rows for win
            if (field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) { // check columns for win
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) // check diagonal left to right for win
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {
            return true;
        }

        return field[0][2].equals(field[1][1]) // check diagonal left to right for win
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("");
    }

    private void player1Wins() {
        player1Points++;
        Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
        LottieAnimationView animationView = findViewById(R.id.animationView);
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);
        animationView.bringToFront();
    }

    private void player2Wins() {
        player2Points++;
        Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
        LottieAnimationView animationView = findViewById(R.id.animationView);
        animationView.playAnimation();
        animationView.setVisibility(View.VISIBLE);
        animationView.bringToFront();
    }

    private void draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePointsText() {
        textViewPlayer1.setText(TEXTVIEWPLAYER1 + player1Points);
        textViewPlayer2.setText(TEXTVIEWPLAYER2 + player2Points);
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        roundCount = 0;
        player1Turn = true;
        setColorPlayersTurn();
    }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        resetBoard();
        setColorPlayersTurn();
    }

    private void setColorPlayersTurn() {
        if(!player1Turn) {
            textViewPlayer1.setBackgroundColor(Color.TRANSPARENT);
            textViewPlayer2.setBackgroundColor(Color.rgb(0, 153, 204));
        } else {
            textViewPlayer1.setBackgroundColor(Color.rgb(0, 153, 204));
            textViewPlayer2.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("roundCount", roundCount);
        outState.putInt("player1Points", player1Points);
        outState.putInt("player2points", player2Points);
        outState.putBoolean("player1Turn", player1Turn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        roundCount = savedInstanceState.getInt("roundCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Turn = savedInstanceState.getBoolean("player1Turn");
    }
}