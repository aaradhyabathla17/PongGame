package com.techcom.ponggame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {
    private static final int DIALOG_GAMEOVER_ID = 100;
    PongView pongView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Initialize pongView and set it as the view
        pongView = new PongView(MainActivity.this, size.x, size.y,false);
        setContentView(pongView);

    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the pongView resume method to execute
        pongView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();
        pongView.pause();
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_GAMEOVER_ID:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Game Over.")
                        .setCancelable(false)
                        .setPositiveButton("Try Again",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        //PongView.resume();
                                        pongView.resume();

                                    }
                                })
                        .setNegativeButton("Exit",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        MainActivity.this.finish();

                                    }
                                });

                AlertDialog gameOverDialog = builder.create();
                return gameOverDialog;
            default:
                return null;
        }


    }
}
