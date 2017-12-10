package com.example.h314gm.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 314GM on 2017/12/3.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update
    {
        @SerializedName("loc")
        public String updataTime;
    }

}
