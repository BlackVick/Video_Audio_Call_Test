package com.android.videoaudiocalltest.Notification;

import com.android.videoaudiocalltest.Models.User;
import com.android.videoaudiocalltest.Util.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import io.paperdb.Paper;

public class FCMHelper {

    private String currentUid = Paper.book().read(Common.USER_ID);

    public void enableFCM(){

        // Enable FCM via enable Auto-init service which generate new token and receive in FCMService
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        //get server key
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference serverKeyRef = db.getReference(Common.FCM_KEY_NODE);
        serverKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String serverKey = dataSnapshot.child("the_key").getValue().toString();

                Paper.book().write(Common.SERVER_KEY, serverKey);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void disableFCM(){

        // Disable auto init
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);

        try {
            // Remove InstanceID initiate to unsubscribe all topic
            // TODO: May be a better way to use FirebaseMessaging.getInstance().unsubscribeFromTopic()
            FirebaseInstanceId.getInstance().deleteInstanceId();

            //nullify fcm id in db
            Map<String, Object> nullFcmIdMap = new HashMap<>();
            nullFcmIdMap.put("fcm_token", "");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection(Common.USER_NODE)
                    .document(currentUid);
            userRef.update(nullFcmIdMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
