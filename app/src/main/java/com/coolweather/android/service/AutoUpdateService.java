package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import com.coolweather.android.gson.Utility;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateBingPic();
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;
        long alarmTrigger = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,alarmTrigger,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        final SharedPreferences sharedPreferences = getSharedPreferences("WeatherActivity",MODE_PRIVATE);
        String weatherString = sharedPreferences.getString("weather",null);
        if(weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+
                    "&key=bb4107b87ff7488c96c2be40746742ea";
            HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    SharedPreferences.Editor editor = getSharedPreferences("WeatherActivity",MODE_PRIVATE).edit();
                    editor.putString("weather",responseText);
                    editor.apply();
                }
            });
        }
    }

    private void updateBingPic(){
        String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOKHttpRequest(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String picText = response.body().string();
                SharedPreferences.Editor editor = getSharedPreferences("WeatherActivity",MODE_PRIVATE).edit();
                editor.putString("bing_pic",picText);
                editor.apply();
            }
        });
    }
}
