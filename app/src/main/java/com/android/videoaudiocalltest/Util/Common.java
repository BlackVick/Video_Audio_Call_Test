package com.android.videoaudiocalltest.Util;

import android.content.Context;

import com.android.videoaudiocalltest.Models.User;
import com.android.videoaudiocalltest.Notification.FCMInterface;
import com.android.videoaudiocalltest.Notification.RetrofitClient;
import com.twilio.video.OpusCodec;
import com.twilio.video.Vp8Codec;

import java.util.Calendar;

public class Common {

    //account info
    public static final String USER_ID = "USER_ID";
    public static final String PAPER_USER = "PAPER_USER";


    //database nodes
    public static final String USER_NODE = "Users";
    public static final String FCM_KEY_NODE = "FCMAPIKEY";
    public static final String CALL_SESSION_NODE = "CallSessions";


    //intent keys
    public static final String CALL_NAME_INTENT = "CallName";
    public static final String CALL_ROOM_INTENT = "CallRoom";
    public static final String CALL_USER_INTENT = "CallUserIntent";
    public static final String CALL_SESSION_INTENT = "CallSessionIntent";

    //video call intent
    public static final String VIDEO_CALL_NAME_INTENT = "VideoCallName";
    public static final String VIDEO_CALL_ROOM_INTENT = "VideoCallRoom";
    public static final String VIDEO_CALL_USER_INTENT = "VideoCallUser";
    public static final String VIDEO_CALL_SESSION_INTENT = "VideoCallSession";

    //audio call intent
    public static final String AUDIO_CALL_NAME_INTENT = "AudioCallName";
    public static final String AUDIO_CALL_ROOM_INTENT = "AudioCallRoom";
    public static final String AUDIO_CALL_USER_INTENT = "AudioCallUser";
    public static final String AUDIO_CALL_SESSION_INTENT = "AudioCallSession";


    //audio call values
    public static final String CALL_SID_KEY = "CALL_SID";
    public static final String VOICE_CHANNEL_LOW_IMPORTANCE = "notification-channel-low-importance";
    public static final String VOICE_CHANNEL_HIGH_IMPORTANCE = "notification-channel-high-importance";
    public static final String INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE";
    public static final String CANCELLED_CALL_INVITE = "CANCELLED_CALL_INVITE";
    public static final String INCOMING_CALL_NOTIFICATION_ID = "INCOMING_CALL_NOTIFICATION_ID";
    public static final String ACTION_ACCEPT = "ACTION_ACCEPT";
    public static final String ACTION_REJECT = "ACTION_REJECT";
    public static final String ACTION_INCOMING_CALL_NOTIFICATION = "ACTION_INCOMING_CALL_NOTIFICATION";
    public static final String ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL";
    public static final String ACTION_CANCEL_CALL = "ACTION_CANCEL_CALL";
    public static final String ACTION_FCM_TOKEN = "ACTION_FCM_TOKEN";


    //call session status
    public static final String CALL_CONNECTING_STATUS = "Connecting";
    public static final String CALL_CONNECTED_STATUS = "Connected";
    public static final String CALL_BUSY_STATUS = "Busy";
    public static final String CALL_DECLINED_STATUS = "Declined";
    public static final String CALL_TIMEOUT_STATUS = "Timeout";


    //settings
    public static final String PREF_AUDIO_CODEC = "PREF_AUDIO_CODEC";
    public static final String PREF_VIDEO_CODEC = "PREF_VIDEO_CODEC";
    public static final String PREF_ENABLE_AUTOMATIC_SUBSCRIPTION = "enable_automatic_subscription";
    public static final String PREF_VP8_SIMULCAST = "vp8_simulcast";
    public static final String PREF_SENDER_MAX_AUDIO_BITRATE = "sender_max_audio_bitrate";
    public static final String PREF_SENDER_MAX_VIDEO_BITRATE = "sender_max_video_bitrate";


    //notification channel
    public static String DEFAULT_NOTIFICATION_CHANNEL = "com.android.videoaudiocalltest.DEFAULT_CHANNEL";
    public static String VIDEO_NOTIFICATION_CHANNEL = "com.android.videoaudiocalltest.VIDEO_CHANNEL";
    public static String AUDIO_NOTIFICATION_CHANNEL = "com.android.videoaudiocalltest.AUDIO_CHANNEL";
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    public static FCMInterface getFCMService()    {
        return RetrofitClient.getClient(BASE_URL).create(FCMInterface.class);
    }

    public static final String SERVER_KEY = "SERVER_KEY";


    //greeting algorithm
    public static String checkTime (Context context){

        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);

        String greeting = null;
        if(hours>=1 && hours<=12){
            greeting = "Good Morning " + getEmojiByUnicode(0x26c5);
        } else

        if(hours>=12 && hours<=16){
            greeting = "Good Afternoon " + getEmojiByUnicode(0x1F31E);
        } else

        if(hours>=16 && hours<=21){
            greeting = "Good Evening " + getEmojiByUnicode(0x1F318);
        } else

        if(hours>=21 && hours<=24){
            greeting = "Good Night " + getEmojiByUnicode(0x1F31B);
        } else {

            greeting = "Hello " + getEmojiByUnicode(0x1F44B);

        }

        return greeting;
    }


    //emoji decoder
    public static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

}
