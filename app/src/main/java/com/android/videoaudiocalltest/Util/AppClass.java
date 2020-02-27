package com.android.videoaudiocalltest.Util;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.twilio.video.OpusCodec;
import com.twilio.video.Vp8Codec;

import io.paperdb.Paper;

public class AppClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*---   PAPER   ---*/
        Paper.init(getApplicationContext());


        /*-------PICASSO--------*/
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        
        /*---   NOTIFICATION CHANNELS   ---*/
        createNotificationChannel();


        //create video channel
        createVideoChannel();


        //create audio channel
        createAudioChannel();


        //set default settings
        setDefaultSettings();

    }

    private void createAudioChannel() {

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Common.AUDIO_NOTIFICATION_CHANNEL, "Audio Channel", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Audio Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    private void createVideoChannel() {

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Common.VIDEO_NOTIFICATION_CHANNEL, "Video Channel", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Video Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    private void setDefaultSettings() {

        //audio codec
        Paper.book().write(Common.PREF_AUDIO_CODEC, OpusCodec.NAME);

        //video codec
        Paper.book().write(Common.PREF_VIDEO_CODEC, Vp8Codec.NAME);

        //auto subscription
        Paper.book().write(Common.PREF_ENABLE_AUTOMATIC_SUBSCRIPTION, true);

        //max audio bitrate
        Paper.book().write(Common.PREF_SENDER_MAX_AUDIO_BITRATE, 0);

        //max video bitrate
        Paper.book().write(Common.PREF_SENDER_MAX_VIDEO_BITRATE, 0);

        //vp8 simulcast
        Paper.book().write(Common.PREF_VP8_SIMULCAST, false);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    Common.DEFAULT_NOTIFICATION_CHANNEL,
                    "DefaultChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}


