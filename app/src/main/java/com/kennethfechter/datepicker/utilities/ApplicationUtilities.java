package com.kennethfechter.datepicker.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kennethfechter.datepicker.R;
import com.kennethfechter.datepicker.entities.Options;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ApplicationUtilities {

    private final Context mContext;
    private final SharedPreferences appPreferences;
    private final Calendar startDate = Calendar.getInstance();
    private final Calendar endDate = Calendar.getInstance();

    public ApplicationUtilities(Context context){
        this.mContext = context;
        appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public long getRetentionPeriod(){
        return Long.parseLong(appPreferences.getString("retention_period", "30"));
    }

    public boolean getAppArchiveSetting(){
        return appPreferences.getBoolean("auto_delete_items", false);
    }

    public void CreateCalculation(){
        this.ShowStartDatePickerDialog(this.startDate, this.endDate);
    }

    private void ShowStartDatePickerDialog(final Calendar startDate, final Calendar endDate){
        final Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                startDate.set(year,monthOfYear, dayOfMonth);
                ShowEndDatePickerDialog(endDate);
                view.dismiss();
            }
        }, now.get(Calendar.YEAR),
           now.get(Calendar.MONTH),
           now.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show(((Activity)mContext).getFragmentManager(), "Datepickerdialog");
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setTitle("Choose Start Date");
    }

    private void ShowEndDatePickerDialog(final Calendar endDate){
        final Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        endDate.set(year,monthOfYear, dayOfMonth);
                        ShowOptionsDialog();
                        view.dismiss();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show(((Activity)mContext).getFragmentManager(), "Datepickerdialog");
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setTitle("Choose End Date");
    }

    private void ShowOptionsDialog(){
        final boolean areDatesValid = CalculationUtilities.IsEndDateValid(startDate, endDate);
        final List<String> customDates = new ArrayList<>();
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        @SuppressLint("InflateParams") View dialogLayout = inflater.inflate(R.layout.options_dialog_layout, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setView(dialogLayout);
        final CheckBox chkCustomDates = (CheckBox) dialogLayout.findViewById(R.id.chkCustomDays);
        final CheckBox chkSaturdays = (CheckBox) dialogLayout.findViewById(R.id.chkSaturdays);
        final CheckBox chkSundays = (CheckBox) dialogLayout.findViewById(R.id.chkSundays);
        final Button buttonAddCustomDate = (Button) dialogLayout.findViewById(R.id.btnAddCustomDay);
        final ListView customDatesListView = (ListView) dialogLayout.findViewById(R.id.customDatesList);

        customDatesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final TextView selectedDate = (TextView)view;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setMessage("Delete " + selectedDate.getText());
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String removedDate = (String)selectedDate.getText();
                                customDates.remove(removedDate);
                                customDatesListView.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, customDates));
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                return true;
            }
        });

        buttonAddCustomDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar now = Calendar.getInstance();
                final Calendar selectedCustomDate = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                selectedCustomDate.set(year,monthOfYear,dayOfMonth);
                                if(CalculationUtilities.IsCustomDateValid(startDate,endDate,selectedCustomDate)){
                                    String selectedDay = CalculationUtilities.ConvertDateToString(selectedCustomDate);
                                    if(!customDates.contains(selectedDay))
                                    {
                                        customDates.add(selectedDay);
                                        Toast.makeText(mContext, "Custom Date: " + selectedDay + " Added" , Toast.LENGTH_LONG).show();
                                        customDatesListView.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, customDates));
                                    } else {
                                        Toast.makeText(mContext, "Custom Date is already in exclusion list" , Toast.LENGTH_LONG).show();

                                    }
                                } else {
                                    Toast.makeText(mContext, "Custom date must be between start and end dates" , Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(((Activity)mContext).getFragmentManager(), "Datepickerdialog");
                dpd.dismissOnPause(true);
                dpd.setTitle("Choose Custom Date");
            }
        });
        chkCustomDates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonAddCustomDate.setVisibility(View.VISIBLE);
                } else {
                    buttonAddCustomDate.setVisibility(View.INVISIBLE);
                }
            }
        });
        if(!areDatesValid){
            dialogBuilder.setPositiveButton("Change End Date", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ShowEndDatePickerDialog(endDate);
                    dialog.dismiss();
                }
            }).show();
            TextView validationTextView  = (TextView) dialogLayout.findViewById(R.id.validationText);
            chkCustomDates.setEnabled(false);
            chkSaturdays.setEnabled(false);
            chkSundays.setEnabled(false);
            validationTextView.setVisibility(View.VISIBLE);

        } else {
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Options options =  new Options(chkSaturdays.isChecked(),chkSundays.isChecked(),chkCustomDates.isChecked(),customDates);
                    CalculationUtilities.CalculateInterval(startDate,endDate,options).save();
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }
}
