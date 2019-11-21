package com.techcom.ponggame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.UiThread;

import java.io.IOException;
import java.util.Map;

public class PongView extends SurfaceView {
    private static final int DIALOG_GAMEOVER_ID = 0;
    Thread mGameThread = null;
    SurfaceHolder mOurHolder;
    volatile boolean mPlaying;
    boolean mPaused;
    Canvas mCanvas;
    Paint mPaint;
    long mFPS;
    int mScreenX;
    int mScreenY;
    Bat mBat;
    Ball mBall;
    Context mcontext;
    boolean firstTry;
    SoundPool sp;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;

    // The mScore
    int mScore = 0;

    // Lives
    int mLives = 3;

    public PongView(Context context, int x, int y,boolean firstTry) {
        super(context);
        mcontext = context;
        mScreenX = x;
        mScreenY = y;
        this.firstTry=firstTry;
        mPaint = new Paint();
        mOurHolder = getHolder();
        mBat = new Bat(mScreenX, mScreenY);
        mBall = new Ball(mScreenX, mScreenY);
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }


        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("beep1.ogg");

            beep1ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.ogg");
            beep2ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep3.ogg");
            beep3ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("loseLife.ogg");
            loseLifeID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("explode.ogg");
            int explodeID = sp.load(descriptor, 0);

        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
*/
        setupAndRestart();
    }

    public void setupAndRestart() {

        // Put the mBall back to the start
        mBall.reset(mScreenX, mScreenY);

        // if game over reset scores and mLives
        if (mLives == 0) {
            mScore=0;
            mLives=3;


        }

    }

    public void update() {

        // Move the mBat if required
        mBat.update(mFPS);

        mBall.update(mFPS);
        if (RectF.intersects(mBall.getRect(), mBat.getRect())) {
            mBall.setRandomXVelocity();
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mBat.getRect().top - 2);
            mScore += 10;
            mBall.increaseVelocity();
            //   sp.play(beep1ID, 1, 1, 0, 0, 1);

        }
        if (mBall.getRect().bottom > mScreenY) {
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mScreenY - 2);
            mLives--;
            //   sp.play(loseLifeID, 1, 1, 0, 0, 1);
            if (mLives == 0) {
                mPaused = true;
                if(firstTry==true) {

                    Handler mainHandler = new Handler(Looper.getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Dialog d = onCreateDialog(DIALOG_GAMEOVER_ID);
                            d.show();
                        } // This is your code
                    };
                    mainHandler.post(myRunnable);
                }else
                {
                    setupAndRestart();
                }



            }


        }

        if (mBall.getRect().top < 0) {
            mBall.reverseYVelocity();
            mBall.clearObstacleY(12);
            // sp.play(beep2ID, 1, 1, 0, 0, 1);
        }
        if (mBall.getRect().left < 0) {
            mBall.reverseXVelocity();
            mBall.clearObstacleX(2);
            //sp.play(beep3ID, 1, 1, 0, 0, 1);
        }
        if (mBall.getRect().right > mScreenX) {
            mBall.reverseXVelocity();
            mBall.clearObstacleX(mScreenX - 22);
            //sp.play(beep3ID, 1, 1, 0, 0, 1);
        }


    }

    // Draw the newly updated scene
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (mOurHolder.getSurface().isValid()) {

            // Draw everything here

            // Lock the mCanvas ready to draw
            mCanvas = mOurHolder.lockCanvas();

            // Clear the screen with my favorite color
            mCanvas.drawColor(Color.argb(255, 120, 197, 87));

            // Choose the brush color for drawing
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mBat
            mCanvas.drawRect(mBat.getRect(), mPaint);

            // Draw the mBall
            mCanvas.drawRect(mBall.getRect(), mPaint);


            // Change the drawing color to white
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mScore
            mPaint.setTextSize(40);
            mCanvas.drawText("Score: " + mScore + "   Lives: " + mLives, 10, 50, mPaint);

            // Draw everything to the screen
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }

    }

    public void resume() {
        mPlaying = true;
        firstTry=true;
        mGameThread = new Thread(rn);
        mGameThread.start();
    }

    public void pause() {
        mPlaying = false;
        firstTry=false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }




    }

    Runnable rn = new Runnable() {
        @Override
        public void run() {
            while (mPlaying) {
                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Update the frame
                // Update the frame
                if (!mPaused) {
                    update();
                }
                // Draw the frame
                draw();

        /*
            Calculate the FPS this frame
            We can then use the result to
            time animations in the update methods.
        */
                long timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    mFPS = 1000 / timeThisFrame;
                }
            }


        }
    };

    // The SurfaceView class implements onTouchListener
// So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                mPaused = false;

                // Is the touch on the right or left?
                if (motionEvent.getX() > mScreenX / 2) {
                    mBat.setMovementState(mBat.RIGHT);
                } else {
                    mBat.setMovementState(mBat.LEFT);
                }

                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                mBat.setMovementState(mBat.STOPPED);
                break;
        }
        return true;
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_GAMEOVER_ID:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Pong Game.")
                        .setCancelable(false)
                        .setPositiveButton("New Game",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        //PongView.resume();
                                        setupAndRestart();


                                    }
                                })
                        .setNegativeButton("Exit",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        firstTry=false;
                                        Activity activity=(Activity) mcontext;
                                        activity.finish();

                                    }
                                });

                AlertDialog gameOverDialog = builder.create();
                return gameOverDialog;
            default:
                return null;
        }

    }
}

