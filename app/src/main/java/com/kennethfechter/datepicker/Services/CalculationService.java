package com.kennethfechter.datepicker.Services;

import com.kennethfechter.datepicker.DataLayer.Calculation;
import com.kennethfechter.datepicker.DataLayer.Options;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kfechter on 5/31/2016.
 */

public class CalculationService {

    public static Calculation calculateInterval(Calendar startDate, Calendar endDate, Options options)
    {
        long saturdays = 0;
        long sundays = 0;
        long daysBetween = 0;
        Calendar internalDate = (Calendar) startDate.clone();
        while(internalDate.before(endDate)){
            if(internalDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                saturdays++;
            }
            if(internalDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                sundays++;
            }
            internalDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }

        if(options.GetExcludeSaturdays()){
            daysBetween -=  saturdays;
        }

        if(options.GetExcludeSundays()){
            daysBetween -= sundays;
        }

        if(options.GetExcludeCustomDays()){
            daysBetween -= options.GetExclusionDaysCount();
        }

        Calculation calculatedInterval = new Calculation(dateStringConverter(startDate),dateStringConverter(endDate),""+daysBetween,options.save());
        return calculatedInterval;
    }

    public static boolean isEndDateAfterStartDate(Calendar startDate, Calendar endDate)
    {
        Calendar internalDate = (Calendar) startDate.clone();
        return internalDate.before(endDate);
    }

    public static boolean isCustomDateBetweenStartandEnd(Calendar startDate, Calendar endDate, Calendar customDate){
        Calendar internalStartDate = (Calendar) startDate.clone();
        Calendar internalEndDate = (Calendar) endDate.clone();
        return ((customDate.after(internalStartDate)) && (customDate.before(internalEndDate)));
    }

    public static String dateStringConverter(Calendar selectedDay)
    {
        return new SimpleDateFormat("E MMM d, yyyy").format(selectedDay.getTime());
    }

    public static Date stringDateConverter(String dateToConvert){
        try {
            return new SimpleDateFormat("E MMM d, yyyy").parse(dateToConvert);
        } catch (Exception ex){
            return null;
        }
    }

    public static long archiverInterval(Calendar startDate, Calendar endDate) {
        long daysBetween = 0;
        Calendar internalDate = (Calendar) startDate.clone();
        while (internalDate.before(endDate)) {
            internalDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }
}
