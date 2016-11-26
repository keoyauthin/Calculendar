package com.kennethfechter.datepicker.utilities;

import android.content.Context;

import com.kennethfechter.datepicker.adapters.CalculationListAdapter;
import com.kennethfechter.datepicker.entities.Calculation;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseUtilities {

    private static List<Calculation> getCalculations(){
        try {
            return Select.from(Calculation.class).list();
        }
        catch (Exception ex)
        {
            return new ArrayList<>();
        }
    }

    public static Calculation getCalculation(Long itemId){
        return Calculation.findById(Calculation.class, itemId);
    }

    public static void ArchiveAndScrub(ApplicationUtilities prefService) {
        if (prefService.getBooleanPreference()) {
            ScrubItems(prefService);
        }

    }

    private static void ScrubItems(ApplicationUtilities prefService){
        List<Calculation> items = getCalculations();
        Calendar startDate = Calendar.getInstance();
        for (Calculation calc:items) {
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(calc.GetCreatedDate());
            if(CalculationUtilities.GetArchiverInterval(startDate,endDate) > prefService.GetLongPreference("archive_period")){
                calc.ArchiveCalculation();
            }
        }
    }

    public static CalculationListAdapter GetCalculations(Context context, CalculationListAdapter.ItemChangedInterface itemChangedListener){
       return new CalculationListAdapter(DatabaseUtilities.getCalculations(), context, itemChangedListener);
    }
}
