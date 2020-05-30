package com.project.digitalwellbeing.remote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.data.model.FCMMessage;
import com.project.digitalwellbeing.data.model.FCMMessageData;
import com.project.digitalwellbeing.data.model.FCMMessageNotification;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Communicator {


    private static final String SERVER_URL = "https://fcm.googleapis.com/fcm/";
    private final HttpLoggingInterceptor logging;
    private final ILogin service;

    public Communicator(final Context context) {
        this.context = context;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Headers headers = request.headers().newBuilder().add("Authorization", "key=" + context.getResources().getString(R.string.firebasekey)).add("Content-Type", "application/json").build();
                request = request.newBuilder().headers(headers).build();
                return chain.proceed(request);
            }
        };
        httpClient.addInterceptor(interceptor);
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL)
                .client(httpClient.build())
                .build();

        service = retrofit.create(ILogin.class);
    }

    Context context;

    public void sendMessage(FCMMessage fcmMessage) {

        Call<FCMMessage> stimulationSessionsCall = service.sendMessage(fcmMessage);
        stimulationSessionsCall.enqueue(new Callback<FCMMessage>() {
            @Override
            public void onResponse(Call<FCMMessage> call, Response<FCMMessage> response) {
                Log.i("Tag", "Request data " + response.raw());


            }

            @Override
            public void onFailure(Call<FCMMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("gh", "No Prescriptions");
            }
        });
    }


}
