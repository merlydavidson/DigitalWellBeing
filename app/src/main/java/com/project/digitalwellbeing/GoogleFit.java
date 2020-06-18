package com.project.digitalwellbeing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GoogleFit extends AppCompatActivity {

    private int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private FitnessOptions fitnessOptions;
    private String TAG = "DigitalWellBeingTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_fit);
        try {
            getGoogleFitDetails();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getGoogleFitDetails() throws ExecutionException, InterruptedException {
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                    account,
                    fitnessOptions);
        } else {
            accessGoogleFit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                try {
                    accessGoogleFit();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void accessGoogleFit() throws ExecutionException, InterruptedException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        GoogleSignInAccount account = GoogleSignIn
                .getAccountForExtension(this, fitnessOptions);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Task<DataReadResponse> response = Fitness.getHistoryClient(GoogleFit.this, account)
                        .readData(new DataReadRequest.Builder()
                                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                .build());

                DataReadResponse readDataResult = null;
                try {
                    readDataResult = Tasks.await(response);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DataSet dataSet = readDataResult.getDataSet(DataType.TYPE_ACTIVITY_SEGMENT);
                List<DataPoint> dataPoints=dataSet.getDataPoints();

            }
        }).start();



//        Fitness.getHistoryClient(this, account)
//                .readData(readRequest)
//                .addOnSuccessListener(response -> {
//                    // Use response data here
//
//                    List<DataSet> dataSets = response.getDataSets();
//
//
//                    Log.d(TAG, "OnSuccess()");
//                })
//                .addOnFailureListener(e -> {
//                    Log.d(TAG, "OnFailure()", e);
//                });
    }

}
