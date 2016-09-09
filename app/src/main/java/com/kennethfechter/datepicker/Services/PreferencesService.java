package com.kennethfechter.datepicker.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by kfechter on 6/4/2016.
 */

public class PreferencesService {

    SharedPreferences appPreferences;
    SharedPreferences.Editor appPrefsEditor;

    public PreferencesService(Context context){
        appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        appPrefsEditor = appPreferences.edit();
    }

    public long GetLongPreference(String key, long defaultValue){
        return appPreferences.getLong(key, defaultValue);
    }

    public String getStringPreference(String key, String defaultValue){
        return appPreferences.getString(key, defaultValue);
    }

    public boolean getBooleanPreference(String key, boolean defaultValue){
        return appPreferences.getBoolean(key,defaultValue);
    }
}
