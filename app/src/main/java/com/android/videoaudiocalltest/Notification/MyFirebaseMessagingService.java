package com.android.videoaudiocalltest.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import com.android.videoaudiocalltest.Calls.AnsweredVideo;
import com.android.videoaudiocalltest.Calls.IncomingVideo;
import com.android.videoaudiocalltest.Calls.VideoCalls;
import com.android.videoaudiocalltest.Models.User;
import com.android.videoaudiocalltest.R;
import com.android.videoaudiocalltest.Util.Common;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import io.paperdb.Paper;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    //log for debugging
    private static final String TAG = "MyFirebaseMessagingServ";

    //important string
    private String currentUid = Paper.book().read(Common.USER_ID);
    private User currentUser = Paper.book().read(Common.PAPER_USER);


    //check app state
    private boolean isMakingVideoCall(){

        boolean isActivityRunning = VideoCalls.isMakingVideoCall;

        if (isActivityRunning){

            return true;

        }

        return false;

    }

    //check app state
    private boolean isReceivingVideoCall(){

        boolean isActivityRunning = AnsweredVideo.isReceivingVideoCall;

        if (isActivityRunning){

            return true;

        }

        return false;

    }

    @Override
    public void onNewToken(@NonNull String s) {
        if (currentUid != null) {
            Map<String, Object> newFcmIdMap = new HashMap<>();
            newFcmIdMap.put("fcm_token", s);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection(Common.USER_NODE)
                    .document(currentUid);
            userRef.update(newFcmIdMap);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        //get data type
        String dataType = remoteMessage.getData().get("data_type");

        //type of notification
        if (dataType.equalsIgnoreCase("Video Call")){

            //get values
            String theTitle = remoteMessage.getData().get("title");
            String theMessage = remoteMessage.getData().get("message");
            String caller = remoteMessage.getData().get("user_name");
            String callerId = remoteMessage.getData().get("user_id");
            String room = remoteMessage.getData().get("room_name");
            String sessionId = remoteMessage.getData().get("call_session_id");

            //confirm application state
            if (!isMakingVideoCall() && !isReceivingVideoCall()){

                Intent notificationIntent = new Intent(this, IncomingVideo.class);

                //add flags and get extras from payload
                notificationIntent.putExtra(Common.CALL_NAME_INTENT, caller);
                notificationIntent.putExtra(Common.CALL_USER_INTENT, callerId);
                notificationIntent.putExtra(Common.CALL_ROOM_INTENT, room);
                notificationIntent.putExtra(Common.CALL_SESSION_INTENT, sessionId);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(notificationIntent);

                /*PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, Common.VIDEO_NOTIFICATION_CHANNEL)
                                .setSmallIcon(R.drawable.ic_stat_notification)
                                .setContentTitle("Incoming call")
                                .setContentText(caller + " is inviting you to a video chat")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_CALL)

                                // Use a full-screen intent only for the highest-priority alerts where you
                                // have an associated activity that you would like to launch after the user
                                // interacts with the notification. Also, if your app targets Android 10
                                // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                                // order for the platform to invoke this notification.
                                .setFullScreenIntent(fullScreenPendingIntent, true);

                Notification incomingCallNotification = notificationBuilder.build();
                startForeground(1, incomingCallNotification);*/



                /*// assuming your main activity
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Common.VIDEO_NOTIFICATION_CHANNEL);

                //build notification
                Intent notificationIntent = new Intent(this, IncomingVideo.class);

                //add flags and get extras from payload
                notificationIntent.putExtra(Common.CALL_NAME_INTENT, caller);
                notificationIntent.putExtra(Common.CALL_USER_INTENT, callerId);
                notificationIntent.putExtra(Common.CALL_ROOM_INTENT, room);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                //create pending intent
                PendingIntent notificationPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                notificationIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setCategory(Notification.CATEGORY_CALL)
                        .setOngoing(true)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setTicker("Hearty365")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                        .setContentTitle(theTitle)
                        .setContentText(theMessage)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setFullScreenIntent(notificationPendingIntent,true)
                        .addAction(R.drawable.ic_answer_call, "Accept", notificationPendingIntent)
                        .addAction(R.drawable.ic_end_call, "Reject", null)
                        .setContentInfo(theMessage);


                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notificationBuilder.build());*/

            } else {

                //create map
                Map<String, Object> sessionMap = new HashMap<>();
                sessionMap.put("session_status", Common.CALL_BUSY_STATUS);

                //update session
                DocumentReference sessionRef = FirebaseFirestore.getInstance().collection(Common.CALL_SESSION_NODE).document(sessionId);
                sessionRef.update(sessionMap);

            }

        }

    }

}
