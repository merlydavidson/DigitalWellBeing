package com.project.digitalwellbeing.remote;

import com.project.digitalwellbeing.data.model.FCMMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface ILogin {

    @POST("send")
    Call<FCMMessage> sendMessage(
            @Body FCMMessage fcmMessage
    );
}
