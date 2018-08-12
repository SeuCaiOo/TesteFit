package br.com.seucaioo.testegooglefit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private Button mButtonViewHeight;
    private Button mButtonViewWeight;

    private TextView tvWeight, tvHeight;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();
    }


    /** Init vars*/
    private void initViews() {
        mButtonViewHeight =  findViewById(R.id.btn_view_height);
        mButtonViewWeight = findViewById(R.id.btn_view_weight);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);

        mButtonViewHeight.setOnClickListener(this);
        mButtonViewWeight.setOnClickListener(this);
    }

    /** Interface */
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }




    //In use, call this every 30 seconds in active mode, 60 in ambient on watch faces
    private void displayWeightDataForToday() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(
                mGoogleApiClient, DataType.TYPE_WEIGHT ).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());
    }

    private void displayHeightDataForToday() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(
                mGoogleApiClient, DataType.TYPE_HEIGHT ).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());

        /*DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(
                mGoogleApiClient, DataType.TYPE_HEIGHT ).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());*/

        /*Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_WEIGHT, DataType.TYPE_WEIGHT)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
        */



    }


    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();




        for (final DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))
                    + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))
                    + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));

            for(final Field field : dp.getDataType().getFields()) {

                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));

                if(field.getName().equals("average")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (dp.getDataType().getName().equals("com.google.height.summary")) {
                                tvHeight.setText(String.valueOf(dp.getValue(field)));
                            } else if(dp.getDataType().getName().equals("com.google.weight.summary")) {
                                tvWeight.setText(String.valueOf(dp.getValue(field)));
                            }


                        }
                    });

                }

            }
        }
    }





    /** Interfaces*/
    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    /** Buttons */
    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.btn_view_height: {
                new ViewTodaysHeightTask().execute();
                break;
            }
            case R.id.btn_view_weight: {
                new ViewTodaysWeightTask().execute();
                break;
            }
        }
    }

    /** AssyncTasks */
    private class ViewTodaysHeightTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayHeightDataForToday();
            return null;
        }
    }

    private class ViewTodaysWeightTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayWeightDataForToday();
            return null;
        }
    }

}