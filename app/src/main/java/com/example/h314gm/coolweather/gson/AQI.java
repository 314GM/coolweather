package com.example.h314gm.coolweather.gson;

/**
 * Created by 314GM on 2017/12/3.
 */

public class AQI {
    public AQICity city;

    public class AQICity
    {
        public String aqi;
        public String pm25;
    }
}
