package com.kennethfechter.datepicker.DataLayer;

import com.orm.SugarRecord;

/**
 * Created by kfechter on 5/31/2016.
 * Options class.
 */

public class Options extends SugarRecord {

    private boolean excludeSundays;
    private boolean excludeSaturdays;
    private boolean excludeCustomDays;
    private int customExclusionDaysCount;

    public Options(){}

    public Options(boolean excludeSaturdays, boolean excludeSundays, boolean excludeCustomDays, int customExclusionDaysCount){
        this.excludeSaturdays = excludeSaturdays;
        this.excludeSundays = excludeSundays;
        this.excludeCustomDays = excludeCustomDays;
        this.customExclusionDaysCount = customExclusionDaysCount;
    }

    public String GetExclusionText(){
        String selectedOptions;

        if(excludeSaturdays && excludeSundays &&  excludeCustomDays){
            selectedOptions = String.format("Selected Options: Saturdays, Sundays, %s Custom Days", this.customExclusionDaysCount);
        } else if(excludeSaturdays && excludeSundays) {
            selectedOptions = "Selected Options: Saturdays, Sundays";
        } else if(excludeSaturdays && excludeCustomDays){
            selectedOptions = String.format("Selected Options: Saturdays, %s Custom Days", this.customExclusionDaysCount);
        } else if(excludeSundays && excludeCustomDays){
            selectedOptions = String.format("Selected Options: Sundays, %s Custom Days", this.customExclusionDaysCount);
        } else if(excludeSundays){
            selectedOptions = "Selected Options: Sundays";
        } else if(excludeSaturdays) {
            selectedOptions = "Selected Options: Saturdays";
        } else if(excludeCustomDays){
            selectedOptions = String.format("Selected Options: %s Custom Days", this.customExclusionDaysCount);
        } else {
            selectedOptions = "No Exclusions Selected";
        }

        return selectedOptions;
    }

    public boolean GetExcludeSaturdays(){
        return excludeSaturdays;
    }

    public boolean GetExcludeSundays(){
        return excludeSundays;
    }

    public boolean GetExcludeCustomDays(){
        return excludeCustomDays;
    }

    public int GetExclusionDaysCount(){
        return customExclusionDaysCount;
    }
}
