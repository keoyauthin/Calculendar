package com.kennethfechter.datepicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.view.GestureDetector.SimpleOnGestureListener;

import com.kennethfechter.datepicker.DataLayer.Calculation;
import com.kennethfechter.datepicker.DataLayer.CalculationListAdapter;
import com.kennethfechter.datepicker.DataLayer.Options;
import com.kennethfechter.datepicker.Services.CalculationService;
import com.kennethfechter.datepicker.Services.DatabaseService;
import com.kennethfechter.datepicker.Services.PreferencesService;
import com.kennethfechter.datepicker.Views.CalculendarAbout;
import com.kennethfechter.datepicker.Views.CalculendarSettings;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalculendarMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener, CalculationListAdapter.ItemChangedInterface, RecyclerView.OnItemTouchListener{

    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private RecyclerView calculationsList;
    LinearLayoutManager llm;
    CalculationListAdapter cla;
    GestureDetectorCompat gestureDetector;
    FloatingActionButton fab;

    Animation floatingActionButtonGrow;
    Animation floatingActionButtonShrink;


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            if(!currentArchiveState){
                fab.startAnimation(floatingActionButtonShrink);
                fab.setVisibility(View.INVISIBLE);
            }

            if(currentArchiveState){
                inflater.inflate(R.menu.menu_action_archives, menu);
            } else {
                inflater.inflate(R.menu.menu_action, menu);
            }
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            List<Integer> selectedItems = cla.GetSelectedItems();
            switch (item.getItemId()) {
                case R.id.deleteItem:
                    for(int i = selectedItems.size() - 1; i >= 0; i--){
                        cla.getCalculationAtPosition(selectedItems.get(i)).DeleteCalculation();
                    }
                    mode.finish(); // Action picked, so close the CAB
                    UpdateListView(currentArchiveState); // update list view because we just changed things
                    return true;
                case R.id.unarchiveItem:
                    for(int i = selectedItems.size() - 1; i >= 0; i--){
                        cla.getCalculationAtPosition(selectedItems.get(i)).UnarchiveCalculation();
                    }
                    UpdateListView(currentArchiveState); // update list view because we just changed things
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.archiveItem:
                    for(int i = selectedItems.size() - 1; i >= 0; i--){
                        cla.getCalculationAtPosition(selectedItems.get(i)).ArchiveCalculation();
                    }
                    UpdateListView(currentArchiveState); // update list view because we just changed things
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(!currentArchiveState){
                fab.startAnimation(floatingActionButtonGrow);
                fab.setVisibility(View.VISIBLE);
            }
            mActionMode = null;
            cla.ClearSelections();
        }
    };

    private boolean currentArchiveState;

    private TextView displayNameText;
    private TextView personalizationText;

    private PreferencesService prefService;

    ActionMode mActionMode;

    @Override
    public void ItemChanged()
    {
        UpdateListView(currentArchiveState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculendar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefService = new PreferencesService(this);
        calculationsList = (RecyclerView) findViewById(R.id.calculationsList);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCalculation();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        Bundle extras = getIntent().getExtras();

        setTitle("Current Calculations");

        floatingActionButtonGrow = AnimationUtils.loadAnimation(this, R.anim.animation_fab_grow);
        floatingActionButtonShrink = AnimationUtils.loadAnimation(this, R.anim.animation_fab_shrink);

        llm = new LinearLayoutManager(this);
        calculationsList.setLayoutManager(llm);
        UpdateListView(false);

        calculationsList.addOnItemTouchListener(this);
        gestureDetector = new GestureDetectorCompat(this, new RecyclerViewOnGestureListener());

        View headerLayout = navigationView.getHeaderView(0); // 0-index header
        displayNameText = (TextView) headerLayout.findViewById(R.id.displayNameText);
        personalizationText = (TextView) headerLayout.findViewById(R.id.personalizationText);
        UpdateTextFromPreferences();

        currentArchiveState = false;
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(floatingActionButtonGrow);

        if (extras != null)
        {
            String targetAction = extras.getString("targetAction");
            if (targetAction != null)
            {
                if (targetAction.equals("create"))
                {
                    CreateCalculation();
                }
                else if (targetAction.equals("viewArchive"))
                {
                    currentArchiveState = true;
                    UpdateListView(true);
                    fab.startAnimation(floatingActionButtonShrink);
                    fab.setVisibility(View.INVISIBLE);
                    setTitle("Archived Calculations");
                    navigationView.getMenu().getItem(1).setChecked(true);
                }
            }
        }

    }

    private void UpdateTextFromPreferences() {
        displayNameText.setText(prefService.getStringPreference("display_name","New User"));
        personalizationText.setText(prefService.getStringPreference("personalization_message",""));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        UpdateTextFromPreferences();
    }

    private void myToggleSelection(int idx) {
        cla.ToggleSelections(idx);
        if(cla.getSelectedItemCount() == 0){
            mActionMode.finish();
            return;
        }
        String title = getString(R.string.selected_count, ""+cla.getSelectedItemCount());
        mActionMode.setTitle(title);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calc_main) {
            UpdateListView(false);
            currentArchiveState = false;
            fab.setVisibility(View.VISIBLE);
            fab.startAnimation(floatingActionButtonGrow);
            setTitle("Current Calculations");
        } else if (id == R.id.nav_calc_archive) {
            UpdateListView(true);
            currentArchiveState = true;
            fab.startAnimation(floatingActionButtonShrink);
            fab.setVisibility(View.INVISIBLE);
            setTitle("Archived Calculations");
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, CalculendarSettings.class);
            startActivity(intent);
        } else if(id == R.id.nav_about) {
            Intent intent = new Intent(this, CalculendarAbout.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void CreateCalculation(){
        // Show Dialogs in order
        showStartDatePickerDialog(startDate, endDate);
    }


    private void UpdateListView(boolean archives){
        cla = new CalculationListAdapter(DatabaseService.getCalculations(archives),this,this,false,false);
        calculationsList.setAdapter(cla);
    }


    private void showStartDatePickerDialog(final Calendar startDate, final Calendar endDate)
    {
        final Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        startDate.set(year,monthOfYear, dayOfMonth);
                        showEndDatePickerDialog(endDate);
                        view.dismiss();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
        dpd.dismissOnPause(true);
        dpd.setTitle("Choose Start Date");
    }

    private void showEndDatePickerDialog(final Calendar endDate){
        final Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        endDate.set(year,monthOfYear, dayOfMonth);
                        showOptionsDialog();
                        view.dismiss();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
        dpd.dismissOnPause(true);
        dpd.setTitle("Choose End Date");
    }

    private void showOptionsDialog(){
        final boolean areDatesValid = CalculationService.isEndDateAfterStartDate(startDate, endDate);
        final List<String> customDates = new ArrayList<String>();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.options_dialog_layout, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(CalculendarMain.this);
                builder1.setMessage("Delete " + selectedDate.getText());
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String removedDate = (String)selectedDate.getText();
                                customDates.remove(removedDate);
                                customDatesListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, customDates));
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
                                if(CalculationService.isCustomDateBetweenStartandEnd(startDate,endDate,selectedCustomDate)){
                                    String selectedDay = CalculationService.dateStringConverter(selectedCustomDate);
                                    if(!customDates.contains(selectedDay))
                                    {
                                        customDates.add(selectedDay);
                                        Toast.makeText(getApplicationContext(), "Custom Date: " + selectedDay + " Added" , Toast.LENGTH_LONG).show();
                                        customDatesListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, customDates));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Custom Date is already in exclusion list" , Toast.LENGTH_LONG).show();

                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Custom date must be between start and end dates" , Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
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
                    showEndDatePickerDialog(endDate);
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
                    if(areDatesValid){
                        Options options =  new Options(chkSaturdays.isChecked(),chkSundays.isChecked(),chkCustomDates.isChecked(),customDates);
                        cla.AddInternalItem(CalculationService.calculateInterval(startDate,endDate,options).save(),cla.getItemCount());
                    }
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

    private void onClick(View view){
        int idx = calculationsList.getChildAdapterPosition(view);
        if(mActionMode != null){
            myToggleSelection(idx);
            return;
        }
    }


    private class RecyclerViewOnGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e){
            View view = calculationsList.findChildViewUnder(e.getX(), e.getY());
            if(view != null){
                onClick(view);
            } else {
                if(mActionMode != null){
                    cla.ClearSelections();
                    mActionMode.finish();
                }
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e){
            View view = calculationsList.findChildViewUnder(e.getX(), e.getY());
            if(view != null){
                if (mActionMode != null) {
                    return;
                }
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = startSupportActionMode(mActionModeCallback);
                int idx = calculationsList.getChildAdapterPosition(view);
                myToggleSelection(idx);
            }
            super.onLongPress(e);
        }
    }
}
