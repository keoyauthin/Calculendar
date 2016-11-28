package com.kennethfechter.datepicker.entities;

import com.kennethfechter.datepicker.utilities.CalculationUtilities;
import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.Date;

public class Calculation extends SugarRecord {
    private String startDate;
    private String endDate;
    private String calculatedInterval;
    private Long options;
    private String createdOn;

    public Calculation() {}

    public Calculation(String startDate, String endDate, String calculatedInterval, Long options){
        this.startDate = startDate;
        this.endDate = endDate;
        this.calculatedInterval = calculatedInterval;
        this.options = options;
        this.createdOn = CalculationUtilities.ConvertDateToString(Calendar.getInstance());
    }

    public String GetStartDate() {
        return this.startDate;
    }

    public String GetEndDate() {
        return this.endDate;
    }

    public Date GetCreatedDate() {return CalculationUtilities.ConvertStringToDate(this.createdOn);}

    public String GetCalculatedInterval() {
        return this.calculatedInterval;
    }

    public Options GetOptions() {return Options.findById(Options.class,this.options);}

    public void DeleteCalculation(){
        this.delete();
    }

    @SuppressWarnings("unused")
    public void UpdateCalculation(long updatedOptionsId, String updatedInterval){
        this.options = updatedOptionsId;
        this.calculatedInterval = updatedInterval;
        this.save();
    }
}
