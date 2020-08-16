package com.project.digitalwellbeing.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.GoogleFitDetails;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.project.digitalwellbeing.utils.CommonDataArea.editor;
import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class GoogleFitData {
    String TAG = "DigitalWellBeing";
    long total = 0;
    Context mContext;
    private boolean authInProgress = false;
    private int REQUEST_OAUTH = 100;
    private float expendedCalories = 0.0f;
    private GoogleApiClient mClient;
    private SimpleDateFormat dateformat;
    private String selecteddate;
    private float walkingCalorie = 0.0f;
    private float runningCalorie = 0.0f;
    private float stillCalorie = 0.0f;
    private float totalCalorie = 0.0f;

    public void connectGoogle(Context context, final Date c) {
        mClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.CONFIG_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .useDefaultAccount()
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                mContext = context;
                                dateformat = new SimpleDateFormat("yyyy-MM-dd");
                                selecteddate = dateformat.format(c.getTime());
                                sharedPreferences = context.getSharedPreferences(
                                        CommonDataArea.prefName, Context.MODE_PRIVATE);
                                CommonDataArea.editor = sharedPreferences.edit();
                                editor.putBoolean(CommonDataArea.ISGOOGLESUCCESS, true);
                                editor.commit();

                                //fetch calories for a date
                                fetchUserGoogleFitData(selecteddate);

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                ).addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                Toast.makeText(context, "Connection Failed.Please Try again", Toast.LENGTH_SHORT).show();
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
//                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
//                                            activity, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
//                                if (!authInProgress) {
//                                    try {
//                                        Log.i(TAG, "Attempting to resolve failed connection");
//                                        authInProgress = true;
//                                        //result.startResolutionForResult(activity, REQUEST_OAUTH);
//                                    } catch (IntentSender.SendIntentException e) {
//                                        Log.e(TAG,
//                                                "Exception while starting resolution activity", e);
//                                    }
//                                }
                            }
                        }
                ).build();
        mClient.connect();
    }


    public void fetchUserGoogleFitData(String date) {
        if (mClient != null && mClient.isConnected()) {

            Date d1 = null;
            try {

                d1 = dateformat.parse(date);
            } catch (Exception e) {

            }
            Calendar calendar = Calendar.getInstance();

            try {
                calendar.setTime(d1);
            } catch (Exception e) {
                calendar.setTime(new Date());
            }
            DataReadRequest readRequest = queryDateFitnessData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            new GetCaloriesAsyncTask(readRequest, mClient).execute();
            new FetchCalorieAsync().execute();
            new FetchStepsAsync().execute();

        }
    }

    private DataReadRequest queryDateFitnessData(int year, int month, int day_of_Month) {

        Calendar startCalendar = Calendar.getInstance(Locale.getDefault());
        startCalendar.set(Calendar.YEAR, year);
        startCalendar.set(Calendar.MONTH, month);
        startCalendar.set(Calendar.DAY_OF_MONTH, day_of_Month);
        startCalendar.set(Calendar.HOUR_OF_DAY, 23);
        startCalendar.set(Calendar.MINUTE, 59);
        startCalendar.set(Calendar.SECOND, 59);
        startCalendar.set(Calendar.MILLISECOND, 999);
        long endTime = startCalendar.getTimeInMillis();


        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        long startTime = startCalendar.getTimeInMillis();

        return new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
                //.aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                //.aggregate(DataType.TYPE_CALORIES_EXPENDED,DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                // .read(DataType.TYPE_CALORIES_EXPENDED)
                // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                // bucketByTime allows for a time span, whereas bucketBySession would allow
                // bucketing by "sessions", which would need to be defined in code.
                //.bucketByTime(1, TimeUnit.DAYS)
                .bucketByActivitySegment(1, TimeUnit.MILLISECONDS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

    }

    public class GetCaloriesAsyncTask extends AsyncTask<Void, Void, DataReadResult> {
        DataReadRequest readRequest;
        String TAG = GetCaloriesAsyncTask.class.getName();
        GoogleApiClient mClient = null;

        public GetCaloriesAsyncTask(DataReadRequest dataReadRequest_, GoogleApiClient googleApiClient) {
            this.readRequest = dataReadRequest_;
            this.mClient = googleApiClient;
        }

        @Override
        protected DataReadResult doInBackground(Void... params) {
            return Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);

        }

        @Override
        protected void onPostExecute(DataReadResult dataReadResult) {
            super.onPostExecute(dataReadResult);
            printData(dataReadResult);
        }


        private void printData(DataReadResult dataReadResult) {
            // [START parse_read_data_result]
            // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
            // as buckets containing DataSets, instead of just DataSets.
            if (dataReadResult.getBuckets().size() > 0) {
                Log.e(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    String bucketActivity = bucket.getActivity();
                    if (bucketActivity.contains(FitnessActivities.WALKING)) {
                        Log.e(TAG, "bucket type->" + bucket.getActivity());
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets) {
                            walkingCalorie = dumpDataSet(dataSet);
                        }
                    } else if (bucketActivity.contains(FitnessActivities.RUNNING)) {
                        Log.e(TAG, "bucket type->" + bucket.getActivity());
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets) {
                            runningCalorie = dumpDataSet(dataSet);
                        }
                    } else if (bucketActivity.contains(FitnessActivities.STILL)) {
                        Log.e(TAG, "bucket type->" + bucket.getActivity());
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets) {
                            stillCalorie = dumpDataSet(dataSet);
                        }
                    }
                }

                // expendedCalories => total active calories
                Log.e(TAG, "BurnedCalories->" + String.valueOf(expendedCalories));


            } else if (dataReadResult.getDataSets().size() > 0) {
                Log.e(TAG, "Number of returned DataSets is: "
                        + dataReadResult.getDataSets().size());
                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    dumpDataSet(dataSet);
                }
            }

            // [END parse_read_data_result]
        }

        // [START parse_dataset]
        private float dumpDataSet(DataSet dataSet) {
            Log.e(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());

            for (DataPoint dp : dataSet.getDataPoints()) {
                if (dp.getEndTime(TimeUnit.MILLISECONDS) > dp.getStartTime(TimeUnit.MILLISECONDS)) {
                    for (Field field : dp.getDataType().getFields()) {
                        expendedCalories = expendedCalories + dp.getValue(field).asFloat();
                    }
                }
            }
            return expendedCalories;
        }
    }

    private class FetchCalorieAsync extends AsyncTask<Object, Object, Float> {
        protected Float doInBackground(Object... params) {
            float total = 0.0f;
            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_CALORIES_EXPENDED);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                if (totalSet != null) {
                    totalCalorie = totalSet.isEmpty()
                            ? 0
                            : totalSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                    Log.d("MerlyCalorie", "calorie " + total);
                }
            } else {
                Log.w(TAG, "There was a problem getting the calories.");
            }
            return total;
        }


        @Override
        protected void onPostExecute(Float aLong) {
            super.onPostExecute(aLong);

            //Total calories burned for that day
            Log.i(TAG, "Total calories: " + aLong);

        }
    }

    private class FetchStepsAsync extends AsyncTask<Object, Object, Long> {
        protected Long doInBackground(Object... params) {

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                if (totalSet != null) {
                    total = totalSet.isEmpty()
                            ? 0
                            : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    Log.d("MerlyFit", "total " + total);

                }
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }
            return total;
        }


        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            //Total steps covered for that day
            Log.i(TAG, "Total steps: " + aLong);

            GoogleFitDetails googleFitDetails = new GoogleFitDetails();
            googleFitDetails.setGoogleFitID(CommonFunctionArea.idGenerator(mContext));
            googleFitDetails.setRunning(runningCalorie);
            googleFitDetails.setWalking(walkingCalorie);
            googleFitDetails.setStill(stillCalorie);
            googleFitDetails.setTotalCalorie(stillCalorie);
            googleFitDetails.setTotalSteps(Integer.parseInt(String.valueOf(total)));
            googleFitDetails.setDate(selecteddate);
            googleFitDetails.setAcknowledgement("0");
            googleFitDetails.setChildId(CommonFunctionArea.getDeviceUUID(mContext));
            AppDataBase appDataBase = AppDataBase.getInstance(mContext);
            DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
            if (digitalWellBeingDao.getGoogleData(selecteddate).size() <= 0)
                digitalWellBeingDao.insertGoogleDetails(googleFitDetails);
            else {
                digitalWellBeingDao.deleteGoogleDetails(selecteddate);
                digitalWellBeingDao.insertGoogleDetails(googleFitDetails);
            }

        }
    }


}
