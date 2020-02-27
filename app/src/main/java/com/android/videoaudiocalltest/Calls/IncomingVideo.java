package com.android.videoaudiocalltest.Calls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.videoaudiocalltest.R;
import com.android.videoaudiocalltest.Util.Common;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class IncomingVideo extends AppCompatActivity {

    //widgets
    private TextView userName;
    private FloatingActionButton accept_call_fab, reject_call_fab;

    //tone
    private MediaPlayer mediaPlayer;
    private Vibrator vib;

    //intent data
    private String callerName;
    private String callerId;
    private String callRoom;
    private String callSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_video);


        //widgets
        userName = findViewById(R.id.userName);
        accept_call_fab = findViewById(R.id.accept_call_fab);
        reject_call_fab = findViewById(R.id.reject_call_fab);


        //accept call animation
        YoYo.with(Techniques.Shake)
                .duration(1500)
                .repeat(7)
                .delay(3000)
                .playOn(accept_call_fab);


        //add activity flag properties
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        getWindow().addFlags(flags);


        //intent data
        if (getIntent() != null){

            //get data
            callerName = getIntent().getStringExtra(Common.CALL_NAME_INTENT);
            callerId = getIntent().getStringExtra(Common.CALL_USER_INTENT);
            callRoom = getIntent().getStringExtra(Common.CALL_ROOM_INTENT);
            callSession = getIntent().getStringExtra(Common.CALL_SESSION_INTENT);

            //set name
            userName.setText(callerName);

            //set audio manager
            AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);


            //init ring
            mediaPlayer = MediaPlayer.create(this, R.raw.old_telephone);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            //vibration
            vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(2500);

            //reject
            reject_call_fab.setOnClickListener(view -> {

                //update declined
                Map<String, Object> upsateSess = new HashMap<>();
                upsateSess.put("session_status", Common.CALL_DECLINED_STATUS);
                DocumentReference theSessionRef = FirebaseFirestore.getInstance().collection(Common.CALL_SESSION_NODE)
                        .document(callSession);
                theSessionRef.update(upsateSess)
                        .addOnCompleteListener(task -> finish());


            });

            //accept
            accept_call_fab.setOnClickListener(view -> {

                //update UI
                Intent videoIntent = new Intent(IncomingVideo.this, AnsweredVideo.class);
                videoIntent.putExtra(Common.VIDEO_CALL_NAME_INTENT, callerName);
                videoIntent.putExtra(Common.VIDEO_CALL_ROOM_INTENT, callRoom);
                videoIntent.putExtra(Common.VIDEO_CALL_USER_INTENT, callerId);
                videoIntent.putExtra(Common.VIDEO_CALL_SESSION_INTENT, callSession);
                startActivity(videoIntent);
                overridePendingTransition(R.anim.slide_left, R.anim.fade_out);
                finish();

            });

            //set listener for results
            DocumentReference theSessionRef = FirebaseFirestore.getInstance().collection(Common.CALL_SESSION_NODE)
                    .document(callSession);
            theSessionRef.addSnapshotListener(IncomingVideo.this, (documentSnapshot, e) -> {

                if (e == null){

                    if (!documentSnapshot.exists()){

                        finish();

                    }

                }

            });


        } else {

            finish();

        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {

                    vib.cancel();
                    mediaPlayer.release();

                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {

                    vib.cancel();
                    mediaPlayer.release();

                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onStop() {

        if (this.isFinishing()) {
            mediaPlayer.release();
            vib.cancel();
        }

        super.onStop();
    }

    @Override
    protected void onPause() {

        if (this.isFinishing()) {
            mediaPlayer.release();
            vib.cancel();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (this.isFinishing()) {
            mediaPlayer.release();
            vib.cancel();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }
}
