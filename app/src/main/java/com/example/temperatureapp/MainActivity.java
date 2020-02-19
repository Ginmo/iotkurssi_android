package com.example.temperatureapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView textTemperature;
    RequestQueue rQueue;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textTemperature = findViewById(R.id.textTemperature);
        rQueue = Volley.newRequestQueue(this);

        startTimer();
    }

    private void getTemperature() {
        String url = "http://ec2-3-88-139-151.compute-1.amazonaws.com/temperature";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("temperature");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject temperature = jsonArray.getJSONObject(i);
                                String currentTemperature = temperature.getString("currentTemperature");
                                textTemperature.setText(currentTemperature + "°C");
                            }
                        } catch (JSONException e) {
                            Log.d("Error", "error1");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", "error2");
                error.printStackTrace();
            }
        });
        rQueue.add(request);
    }

    public void startTimer() {
        timer = new Timer();

        initializeTimerTask();

        timer.schedule(timerTask, 5000, 10000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getTemperature();
                        Log.d("Update", "Temperature");

                    }
                });
            }
        };
    }
}
