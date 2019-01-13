package com.intkhabahmed.solarcalculator.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String getFormattedDate(long timeInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd, YYYY", Locale.getDefault());
        return formatter.format(new Date(timeInMillis));
    }

    public static String getFormattedTime(long timeInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return formatter.format(new Date(timeInMillis));
    }
}
