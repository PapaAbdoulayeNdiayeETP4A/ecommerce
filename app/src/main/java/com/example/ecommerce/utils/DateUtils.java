// utils/DateUtils.java
package com.example.ecommerce.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.US);
    private static final SimpleDateFormat DISPLAY_DATE_TIME_FORMAT = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);

    public static String formatDate(Date date) {
        return DISPLAY_DATE_FORMAT.format(date);
    }

    public static String formatDateTime(Date date) {
        return DISPLAY_DATE_TIME_FORMAT.format(date);
    }

    public static String formatDateForApi(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatDateTimeForApi(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }

    public static Date parseDate(String dateStr) throws ParseException {
        return DATE_FORMAT.parse(dateStr);
    }

    public static Date parseDateTime(String dateTimeStr) throws ParseException {
        return DATE_TIME_FORMAT.parse(dateTimeStr);
    }

    public static String getRelativeTimeSpan(Date date) {
        long now = System.currentTimeMillis();
        long time = date.getTime();
        long diff = now - time;

        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "à l'instant";
        } else if (diff < TimeUnit.HOURS.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return "il y a " + minutes + (minutes > 1 ? " minutes" : " minute");
        } else if (diff < TimeUnit.DAYS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            return "il y a " + hours + (hours > 1 ? " heures" : " heure");
        } else if (diff < TimeUnit.DAYS.toMillis(7)) {
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return "il y a " + days + (days > 1 ? " jours" : " jour");
        } else {
            return formatDate(date);
        }
    }

    public static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar specifiedDate = Calendar.getInstance();
        specifiedDate.setTime(date);

        return today.get(Calendar.YEAR) == specifiedDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == specifiedDate.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isYesterday(Date date) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        Calendar specifiedDate = Calendar.getInstance();
        specifiedDate.setTime(date);

        return yesterday.get(Calendar.YEAR) == specifiedDate.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == specifiedDate.get(Calendar.DAY_OF_YEAR);
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }
}