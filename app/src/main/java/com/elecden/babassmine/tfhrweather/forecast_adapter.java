package com.elecden.babassmine.tfhrweather;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by babassmine on 2/22/15.
 */
public class forecast_adapter extends ArrayAdapter<HourlyForecast> {
    public forecast_adapter(Context context, List<HourlyForecast> objects) {
        super(context, 0, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        HourlyForecast hour = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hourly_forecast, parent, false);
        }
        // Lookup view for data population
        TextView hour_ = (TextView) convertView.findViewById(R.id.date);
        TextView temp = (TextView) convertView.findViewById(R.id.temp);
        TextView humidity = (TextView) convertView.findViewById(R.id.humidity);
        TextView condition = (TextView) convertView.findViewById(R.id.cond);
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        // Populate the data into the template view using the data object
        hour_.setText(hour.hour);
        hour_.setTextColor(Color.BLACK);
        temp.setText(hour.temp);
        if (Integer.valueOf(String.valueOf(temp.getText()))<35){
            temp.setTextColor(Color.BLUE);
        } else if (Integer.valueOf(String.valueOf(temp.getText()))>=35 && Integer.valueOf(String.valueOf(temp.getText()))<60){
            temp.setTextColor(Color.YELLOW);
        }else if (Integer.valueOf(String.valueOf(temp.getText()))>=60){
            temp.setTextColor(Color.RED);
        }

        humidity.setText(hour.hum);
        humidity.setTextColor(Color.BLACK);
        condition.setText(hour.cond);
        condition.setTextColor(Color.BLACK);
        icon.setImageBitmap(hour.icon);

        // Return the completed view to render on screen
        return convertView;
    }
}
