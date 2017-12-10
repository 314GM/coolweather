package com.example.h314gm.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 314GM on 2017/12/3.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class  More
    {
        @SerializedName("txt")
        public String info;

    }

}
