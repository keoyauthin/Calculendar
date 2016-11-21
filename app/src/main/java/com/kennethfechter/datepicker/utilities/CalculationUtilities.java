package com.kennethfechter.datepicker.utilities;

import android.annotation.SuppressLint;
import android.content.Context;

import com.kennethfechter.datepicker.entities.Calculation;
import com.kennethfechter.datepicker.entities.Options;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalculationUtilities {

    public static Calculation CalculateInterval(Calendar startDate, Calendar endDate, Options options) {
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

        return new Calculation(ConvertDateToString(startDate), ConvertDateToString(endDate), ""+daysBetween,options.save());
    }

    public static void UpdateCalculation(Calculation calculationToUpdate, Options updatedOptions){
        calculationToUpdate.UpdateOptions(updatedOptions.save());
        long saturdays = 0;
        long sundays = 0;
        long daysBetween = 0;
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(ConvertStringToDate(calculationToUpdate.GetEndDate()));
        Calendar internalDate =  Calendar.getInstance();
        internalDate.setTime(ConvertStringToDate(calculationToUpdate.GetStartDate()));
        internalDate = (Calendar)internalDate.clone();
        while(internalDate.before(endCalendar.clone())){
            if(internalDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                saturdays++;
            }
            if(internalDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                sundays++;
            }
            internalDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }

        if(updatedOptions.GetExcludeSaturdays()){
            daysBetween -=  saturdays;
        }

        if(updatedOptions.GetExcludeSundays()){
            daysBetween -= sundays;
        }

        if(updatedOptions.GetExcludeCustomDays()){
            daysBetween -= updatedOptions.GetExclusionDaysCount();
        }

        calculationToUpdate.UpdateCalculatedInterval(""+daysBetween);
    }

    public static boolean IsEndDateValid(Calendar startDate, Calendar endDate){
        Calendar internalDate = (Calendar) startDate.clone();
        return internalDate.before(endDate);
    }

    public static boolean IsCustomDateValid(Calendar startDate, Calendar endDate, Calendar customDate) {
        Calendar internalStartDate = (Calendar) startDate.clone();
        Calendar internalEndDate = (Calendar) endDate.clone();
        return ((customDate.after(internalStartDate)) && (customDate.before(internalEndDate)));
    }

    @SuppressLint("SimpleDateFormat")
    public static String ConvertDateToString(Calendar dateToConvert){
        return new SimpleDateFormat("E MMM d, yyyy").format(dateToConvert.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static Date ConvertStringToDate(String stringToConvert){
        try {
            return new SimpleDateFormat("E MMM d, yyyy").parse(stringToConvert);
        } catch (Exception ex){
            return null;
        }
    }

    public static Long GetArchiverInterval(Calendar startDate, Calendar endDate){
        long daysBetween = 0;
        Calendar internalDate = (Calendar) startDate.clone();
        while (internalDate.before(endDate)) {
            internalDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }
}
