package com.kennethfechter.datepicker.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kennethfechter.datepicker.R;

public class CalculendarInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculendar_info);
        String version;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex){
            version = getString(R.string.fallback_version_name);
        }

        String verTitle = String.format(getString(R.string.calculendar_info_title), version);
        TextView infoHeader = (TextView)findViewById(R.id.infoHeader);
        infoHeader.setText(verTitle);
    }
}
