package com.palungo.coffee;

import android.app.Application;

import com.palungo.coffee.helpers.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Sanjay on 6/27/2016.
 */
public class GlobalApplication extends Application {

    public static GlobalApplication singleton;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private SimpleDateFormat dateFormat, dateFormat1;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = DatabaseHelper.getInstance(this);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat1 = new SimpleDateFormat("dd MMM, yyyy ");
        singleton = this;
    }

    public String getDate(Integer year, Integer month, Integer day) {
        if (year != null & month != null && day != null) {
            calendar = new GregorianCalendar(year, month, day);
        } else {
            calendar = new GregorianCalendar();
        }
        return dateFormat.format(calendar.getTime());
    }

    public String formatDate(String date) throws ParseException {
        Date d = dateFormat.parse(date);
        return dateFormat1.format(d);
    }
}
