package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.Set;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public static String getOptionCharCase(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String charCaseKey = context.getString(R.string.char_case_key);
        return settings.getString(charCaseKey, null);
    }

    public static Set<String> getOptionCategories(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String categoriesKey = context.getString(R.string.categories_key);
        return settings.getStringSet(categoriesKey, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String categoriesKey = getString(R.string.categories_key);

        if (key.equals(categoriesKey)) {
            Set<String> selectedCategories = sharedPreferences.getStringSet(categoriesKey, null);

            if (selectedCategories.size() < 1) {
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(SettingsActivity.this);

                errorDialog.setTitle(R.string.more_categories_title);
                errorDialog.setMessage(R.string.more_categories_message);
                errorDialog.setCancelable(false);
                errorDialog.setPositiveButton(R.string.ok, null);

                errorDialog.show();
            }
        }
    }
}
