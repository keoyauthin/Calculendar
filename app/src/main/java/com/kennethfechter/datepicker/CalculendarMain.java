package com.kennethfechter.datepicker;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kennethfechter.datepicker.adapters.CalculationListAdapter;
import com.kennethfechter.datepicker.utilities.ApplicationUtilities;
import com.kennethfechter.datepicker.utilities.DatabaseUtilities;

public class CalculendarMain extends AppCompatActivity {

    private ApplicationUtilities appUtilities;
    private RecyclerView calculationsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculendar_main);
        calculationsList = (RecyclerView) findViewById(R.id.calculationsList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        calculationsList.setLayoutManager(llm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appUtilities = new ApplicationUtilities(CalculendarMain.this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               appUtilities.CreateCalculation();
            }
        });

        DatabaseUtilities.ArchiveAndScrub(appUtilities);
        UpdateListView();
        fab.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calculendar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void UpdateListView(){
        calculationsList.setAdapter(DatabaseUtilities.GetCalculations(this, new CalculationListAdapter.ItemChangedInterface() {
            @Override
            public void ItemChanged() {
                UpdateListView();
            }
        }));
    }
}
