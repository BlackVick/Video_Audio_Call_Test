package com.android.videoaudiocalltest.Util;

import android.content.Context;

import com.android.videoaudiocalltest.Models.User;

import java.util.Calendar;

public class Common {

    //account info
    public static final String USER_ID = "USER_ID";
    public static final String PAPER_USER = "PAPER_USER";


    //database nodes
    public static final String USER_NODE = "Users";


    //intent keys
    public static final String CALL_INTENT = "CallIntent";


    //notification channel
    public static String DEFAULT_NOTIFICATION_CHANNEL = "com.android.videoaudiocalltest.DEFAULT_CHANNEL";


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
