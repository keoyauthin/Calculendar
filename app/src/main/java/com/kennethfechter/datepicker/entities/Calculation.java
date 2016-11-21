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
    private boolean itemArchived;
    private String archivedOn;
    private String createdOn;

    public Calculation() {}

    public Calculation(String startDate, String endDate, String calculatedInterval, Long options){
        this.startDate = startDate;
        this.endDate = endDate;
        this.calculatedInterval = calculatedInterval;
        this.options = options;
        this.itemArchived = false;
        this.createdOn = CalculationUtilities.ConvertDateToString(Calendar.getInstance());
    }

    public String GetStartDate() {
        return this.startDate;
    }

    public String GetEndDate() {
        return this.endDate;
    }

    public Date GetArchivedDate() {return CalculationUtilities.ConvertStringToDate(this.archivedOn);}

    public Date GetCreatedDate() {return CalculationUtilities.ConvertStringToDate(this.createdOn);}

    public String GetCalculatedInterval() {
        return this.calculatedInterval;
    }

    public Options GetOptions() {return Options.findById(Options.class,this.options);}

    public void ArchiveCalculation(){
        this.itemArchived = true;
        this.archivedOn = CalculationUtilities.ConvertDateToString(Calendar.getInstance());
        this.save();
    }

    public void DeleteCalculation(){
        this.delete();
    }

    public void UnarchiveCalculation(){
        this.itemArchived = false;
        this.archivedOn = "";
        this.save();
    }

    public void UpdateOptions(long updatedOptionsId)
    {
        this.options = updatedOptionsId;
        this.save();
    }

    public void UpdateCalculatedInterval(String updatedInterval)
    {
        this.calculatedInterval = updatedInterval;
        this.save();
    }
}
