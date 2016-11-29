package com.kennethfechter.datepicker;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

import com.kennethfechter.datepicker.activities.CalculendarInfo;
import com.kennethfechter.datepicker.activities.CalculendarSettings;
import com.kennethfechter.datepicker.adapters.CalculationListAdapter;
import com.kennethfechter.datepicker.utilities.ApplicationUtilities;
import com.kennethfechter.datepicker.utilities.DatabaseUtilities;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class CalculendarMain extends AppCompatActivity implements RecyclerView.OnItemTouchListener, ApplicationUtilities.ItemSavedInterface {

    private ApplicationUtilities appUtilities;
    private RecyclerView calculationsList;
    private CalculationListAdapter calculationsListAdapter;
    private ActionMode mActionMode;
    private FloatingActionButton floatingActionButton;
    private GestureDetectorCompat gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculendar_main);
        calculationsList = (RecyclerView) findViewById(R.id.calculationsList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        calculationsList.setLayoutManager(llm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appUtilities = new ApplicationUtilities(CalculendarMain.this, CalculendarMain.this);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUtilities.CreateCalculation();
            }
        });

        DatabaseUtilities.ArchiveAndScrub(appUtilities);
        UpdateListView();
        floatingActionButton.show();
        calculationsList.addOnItemTouchListener(this);
        gestureDetector = new GestureDetectorCompat(this, new RecyclerViewOnGestureListener());
        setTitle("My Calculations");
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String targetAction = extras.getString("targetAction");
            if(targetAction != null) {
                if (targetAction.equals("create")) {
                    appUtilities.CreateCalculation();
                }
            }
        }
    }

    @Override
    public void ItemSaved(){
        UpdateListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calculendar_main, menu);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, CalculendarSettings.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, CalculendarInfo.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void myToggleSelection(int idx){
        calculationsListAdapter.ToggleSelections(idx);
        if(calculationsListAdapter.getSelectedItemCount() == 0){
            mActionMode.finish();
            return;
        }

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        formatter.format(getString(R.string.selected_count), calculationsListAdapter.getSelectedItemCount());
        mActionMode.setTitle(sb.toString());
    }

    private void UpdateListView(){
        calculationsListAdapter =  DatabaseUtilities.GetCalculations(new CalculationListAdapter.ItemChangedInterface() {
            @Override
            public void ItemChanged() {
                UpdateListView();
            }});
        calculationsList.setAdapter(calculationsListAdapter);
    }

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            floatingActionButton.hide();
            inflater.inflate(R.menu.menu_action, menu);

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
            List<Integer> selectedItems = calculationsListAdapter.GetSelectedItems();
            switch (item.getItemId()) {
                case R.id.deleteItem:
                    for(int i = selectedItems.size() - 1; i >= 0; i--){
                        calculationsListAdapter.getCalculationAtPosition(selectedItems.get(i)).DeleteCalculation();
                    }
                    mode.finish(); // Action picked, so close the CAB
                    UpdateListView(); // update list view because we just changed things
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
           floatingActionButton.show();
            mActionMode = null;
            calculationsListAdapter.ClearSelections();
        }
    };

    private void onClick(View view){
        int idx = calculationsList.getChildAdapterPosition(view);
        if(mActionMode != null){
            myToggleSelection(idx);
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
                    calculationsListAdapter.ClearSelections();
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
                mActionMode = CalculendarMain.this.startSupportActionMode(mActionModeCallback);
                int idx = calculationsList.getChildAdapterPosition(view);
                myToggleSelection(idx);
            }
            super.onLongPress(e);
        }
    }

}
