package com.android.videoaudiocalltest.Notification;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface FCMInterface {

    @POST("send")
    Call<ResponseBody> send(

            @HeaderMap Map<String, String> headers,
            @Body FirebaseCloudMessage message

    );

}
