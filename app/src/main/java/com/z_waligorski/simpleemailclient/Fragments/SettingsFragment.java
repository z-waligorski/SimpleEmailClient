package com.z_waligorski.simpleemailclient.Fragments;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.z_waligorski.simpleemailclient.R;
import com.z_waligorski.simpleemailclient.Service.StartInboxService;


public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    // PreferenceFragment provides special layout for preferences
    // PreferenceFragmentCompat is compatible with support.v4.app.Fragment
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Add data from special file in xml directory
        addPreferencesFromResource(R.xml.preferences);

        // Add listener for INTERVAL settings
        ListPreference preference = (ListPreference) findPreference("INTERVAL");
        preference.setOnPreferenceChangeListener(this);
    }

    // When user changes refreshing interval in settings, new AlarmManager should be created
    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        ListPreference pref = (ListPreference) preference;
        pref.setValue((String) o);

        StartInboxService service = new StartInboxService();
        service.setAlarm(getContext());
        return false;
    }
}
