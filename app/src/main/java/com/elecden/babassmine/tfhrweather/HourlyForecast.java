package com.elecden.babassmine.tfhrweather;

import android.graphics.Bitmap;

/**
 * Created by babassmine on 2/22/15.
 */
public class HourlyForecast {
    public String hour;
    public String cond;
    public Bitmap  icon;
    public String temp;
    public String hum;

    HourlyForecast(String hour, String temp, String hum, String cond, Bitmap icon){
        this.hour = hour;
        this.cond = cond;
        this.icon = icon;
        this.temp = temp;
        this.hum = hum;
    }
}
