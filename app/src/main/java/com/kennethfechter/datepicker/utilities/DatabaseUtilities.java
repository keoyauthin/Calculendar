package com.kennethfechter.datepicker.utilities;

import com.kennethfechter.datepicker.entities.Calculation;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseUtilities {

    public static List<Calculation> getCalculations(boolean archivedItems){
        int archive = (archivedItems) ? 1 : 0;
        try {
            return Select.from(Calculation.class)
                    .where(Condition.prop("ITEM_ARCHIVED").eq(archive)).list();
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
            Archive(prefService);
            ScrubArchives(prefService);
        }

    }

    private static void Archive(ApplicationUtilities prefService){
        List<Calculation> items = getCalculations(false);
        Calendar startDate = Calendar.getInstance();
        for (Calculation calc:items) {
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(calc.GetCreatedDate());
            if(CalculationUtilities.GetArchiverInterval(startDate,endDate) > prefService.GetLongPreference("archive_period")){
                calc.ArchiveCalculation();
            }
        }
    }

    private static void ScrubArchives(ApplicationUtilities prefService){
        List<Calculation> items = getCalculations(false);
        Calendar startDate = Calendar.getInstance();
        for (Calculation calc:items) {
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(calc.GetArchivedDate());
            if(CalculationUtilities.GetArchiverInterval(startDate,endDate) > prefService.GetLongPreference("archive_retention_period")){
                calc.DeleteCalculation();
            }
        }
    }
}
