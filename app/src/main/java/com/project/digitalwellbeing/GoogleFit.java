package com.project.digitalwellbeing;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.GoogleFitDetails;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.GoogleFitData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.project.digitalwellbeing.utils.CommonDataArea.editor;
import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class GoogleFit extends AppCompatActivity {

    private static final int REQUEST_OAUTH = 500;
    private static int totalCount = 0;
    private static String TAG = "DigitalWellBeingTag";
    GoogleApiClient mClient;
    DonutProgress stepProgress, calorieProgress;
    Button goButton;
    private int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private FitnessOptions fitnessOptions;
    private boolean authInProgress = false;
    private EditText dateFrom;
    private DatePickerDialog picker;
    private String selecteddate = "";
    private SimpleDateFormat dateformat;
    private PieChart pieChart;
    private List<GoogleFitDetails> googleDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_fit);
        if (ContextCompat.checkSelfPermission(GoogleFit.this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GoogleFit.this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    3);
        }

        stepProgress = (DonutProgress) findViewById(R.id.donut_steps);
        calorieProgress = (DonutProgress) findViewById(R.id.donut_calorie);
        dateFrom = (EditText) findViewById(R.id.date_from);
        goButton = (Button) findViewById(R.id.go_button);

        dateFrom.setText(CommonDataArea.getDAte("dd-MM-yyyy"));
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(GoogleFit.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                if (monthOfYear < 10 && dayOfMonth < 10)
                                    dateFrom.setText("0" + dayOfMonth + "-" + "0" + (monthOfYear + 1) + "-" + year);
                                else if (dayOfMonth < 10)
                                    dateFrom.setText("0" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                else if (monthOfYear < 10)
                                    dateFrom.setText(dayOfMonth + "-" + "0" + (monthOfYear + 1) + "-" + year);
                                else
                                    dateFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        ;
        createGoogleApiClient();

        new GoogleFitData().connectGoogle(GoogleFit.this, getParsedData());


        pieChart = (PieChart) findViewById(R.id.idPieChart);

        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(35f);
        // pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Activities");
        pieChart.setCenterTextSize(15);
        //
//        try {
//            getGoogleFitDetails();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getcallDetails(dateFrom.getText().toString());

//                Intent intent = getIntent();
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                finish();
//                startActivity(intent);
            }
        });
        getcallDetails(CommonDataArea.getDAte("dd-MM-yyyy"));
        //drawPieChart();
    }

    public Date getParsedData() {
        String stringDate = dateFrom.getText().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = formatter.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        return date;
    }

    public void createGoogleApiClient() {
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.CONFIG_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .useDefaultAccount()
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {

                                authInProgress = true;
                                sharedPreferences = getSharedPreferences(
                                        CommonDataArea.prefName, Context.MODE_PRIVATE);
                                CommonDataArea.editor = sharedPreferences.edit();
                                editor.putBoolean(CommonDataArea.ISGOOGLESUCCESS, true);
                                editor.commit();

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
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            GoogleFit.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(GoogleFit.this, REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                ).build();
        mClient.connect();
    }


    public List<GoogleFitDetails> getcallDetails(String fetchDate) {


        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = formatter.parse(fetchDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        dateformat = new SimpleDateFormat("yyyy-MM-dd");
        selecteddate = dateformat.format(date.getTime());
        AppDataBase appDataBase = AppDataBase.getInstance(GoogleFit.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        googleDetails = digitalWellBeingDao.getGoogleData(selecteddate);
        drawPieChart();
        return googleDetails;
    }

    public void drawPieChart() {
        stepProgress.setDonut_progress(String.valueOf(0));
        calorieProgress.setDonut_progress(String.valueOf(0));
        calorieProgress.setText("Calories " + Math.round(0));
        stepProgress.setText("Calories " + Math.round(0));
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();
        if (googleDetails != null) {
            if (googleDetails.size() > 0) {
                yVals1.add(new PieEntry(googleDetails.get(0).getWalking(), "walking"));
                yVals1.add(new PieEntry(googleDetails.get(0).getRunning(), "Running"));
                yVals1.add(new PieEntry(googleDetails.get(0).getStill(), "Idle"));
                stepProgress.setDonut_progress(String.valueOf(googleDetails.get(0).getTotalSteps()));
                // stepProgress.setInnerBottomText("Steps " + googleDetails.get(0).getTotalSteps());
                stepProgress.setText("Steps " + googleDetails.get(0).getTotalSteps());
                calorieProgress.setDonut_progress(String.valueOf(Math.round(googleDetails.get(0).getTotalCalorie())));
                calorieProgress.setText("Calories " + Math.round(googleDetails.get(0).getTotalCalorie()));

            }
        }


//        ArrayList<String> xVals = new ArrayList<String>();
//        xVals.add("Walking");
//        xVals.add("Running");
//        xVals.add("Still");

        PieDataSet dataSet = new PieDataSet(yVals1, "Activities");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);


        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(Color.parseColor("#8966f6"));
        colors.add(Color.parseColor("#bf69e3"));
        colors.add(Color.parseColor("#a2a2cb"));


        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);


// instantiate pie data object now
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);

        // update pie chart
        pieChart.invalidate();
    }


}
