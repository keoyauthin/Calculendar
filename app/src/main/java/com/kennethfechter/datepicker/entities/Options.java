package com.kennethfechter.datepicker.entities;

import com.orm.SugarRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Options extends SugarRecord {

    private boolean excludeSundays;
    private boolean excludeSaturdays;
    private boolean excludeCustomDays;
    private int customExclusionDaysCount;
    private String excludedDays;

    public Options() {}

    public Options(boolean excludeSaturdays, boolean excludeSundays, boolean excludeCustomDays, List<String> customExclusionDays){
        this.excludeSaturdays = excludeSaturdays;
        this.excludeSundays = excludeSundays;
        this.excludeCustomDays = excludeCustomDays;
        this.customExclusionDaysCount = customExclusionDays.size();
        this.excludedDays = android.text.TextUtils.join(";", customExclusionDays);
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

    public List<String> GetExcludedDates() { return excludedDays != null ? Arrays.asList(excludedDays.split("\\s*;\\s*")) : new ArrayList<String>();}
}
