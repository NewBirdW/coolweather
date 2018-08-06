package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 77492 on 2018/8/3.
 */

public class AQI {

    @SerializedName("city")
    public AQICity aqiCity;

    public class AQICity{

        public String aqi;

        public String pm25;
    }
}
