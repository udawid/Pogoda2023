package com.example.pogoda2023;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText etCity;
    TextView tvResult;
    private final String URL="https://api.openweathermap.org/data/2.5/weather";
    private final String APPID="e53301e27efa0b66d05045d91b2742d3";
    String output= "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView ivIcon = findViewById(R.id.ivIcon);
        etCity = findViewById(R.id.editText_City);
        tvResult = findViewById(R.id.tvResult);
    }

    public void getWeatherDetails(View view) {
        String city = etCity.getText().toString().trim();
        String apiUrl = "";
        if(city.equals("")){
            clearResult();
            Toast.makeText(getApplicationContext(), R.string.brak, Toast.LENGTH_SHORT).show();
        } else {
            apiUrl = URL + "?q=" + city + "&units=metric&lang=pl&appid=" + APPID;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        // tablica weather z obiektem 0 zawiera opis pogody
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        // obiekt main zawiera dane szczegółowe
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp");
                        double feelsLike = jsonObjectMain.getDouble("feels_like");
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        // obiekt wind zawiera prędkość wiatru
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        //obiekt cloud zawiera zachmurzenie
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        //bezpośrednio można odczytać nazwę miasta
                        String cityName = jsonResponse.getString("name");

                        output += " Bieżąca pogoda w mieście " + cityName
                                + "\n Temperatura: " + temp + " ℃"
                                + "\n Temperatura odczuwalna: " + feelsLike + " ℃"
                                + "\n Wilgotność: " + humidity + "%"
                                + "\n Opis: " + description
                                + "\n Wiatr: " + wind + "m/s"
                                + "\n Zachmurzenie: " + clouds + "%"
                                + "\n Ciśnienie: " + pressure + " hPa";
                        tvResult.setText(output);
                        // pobierz kod ikony z obiektu "weather"
                        JSONArray jsonArrayIcon = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectIcon = jsonArrayIcon.getJSONObject(0);
                        String iconCode = jsonObjectIcon.getString("icon");

                        String imageUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";

                        ImageView ivIcon = findViewById(R.id.ivIcon);
                        Picasso.get().load(imageUrl).into(ivIcon);

                        GradientDrawable shape = new GradientDrawable();
                        shape.setShape(GradientDrawable.RECTANGLE);
                        shape.setCornerRadii(new float[] { 40, 40, 40, 40, 40, 40, 40, 40 });
                        shape.setColor(0x883DADF2);
                        tvResult.setBackground(shape);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    clearResult();
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

    private void clearResult() {
        output="";
        tvResult.setText(output);
        tvResult.setBackgroundColor(0x00FFFFFF);
    }

}