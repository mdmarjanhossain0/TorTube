package com.victoriya.tortube.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.takisoft.preferencex.EditTextPreference;
import com.takisoft.preferencex.PreferenceFragmentCompat;
import com.takisoft.preferencex.SwitchPreferenceCompat;
import com.victoriya.tortube.R;


public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {


        private static final String TAG=SettingsActivity.class.getSimpleName();
        private SharedPreferences pref;

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            pref = PreferenceManager.getDefaultSharedPreferences(getContext());

            EditTextPreference bufferSizePref = findPreference(getString(R.string.buffer_size));
            if (bufferSizePref != null) {

                long bufferSize = pref.getLong(getString(R.string.buffer_size), 1L);

                bufferSizePref.setSummary(bufferSize + " MB");
                bufferSizePref.setText(String.valueOf(bufferSize));
                bindOnPreferenceChanges(bufferSizePref);
            }

            EditTextPreference streamingSpeedPref = findPreference(getString(R.string.streaming_speed));
            if (streamingSpeedPref != null) {

                int streamSpeed=pref.getInt(getString(R.string.streaming_speed),0);
                streamingSpeedPref.setSummary(streamSpeed+" KB/s");
                streamingSpeedPref.setText(String.valueOf(streamSpeed));
                bindOnPreferenceChanges(streamingSpeedPref);
            }

            EditTextPreference seedingSpeedPref = findPreference(getString(R.string.seeding_speed));
            if (seedingSpeedPref != null) {

                int seedingSpeed=pref.getInt(getString(R.string.seeding_speed),0);
                seedingSpeedPref.setSummary(seedingSpeed+" KB/s");
                seedingSpeedPref.setText(String.valueOf(seedingSpeed));
                bindOnPreferenceChanges(seedingSpeedPref);
            }

            EditTextPreference portNumberPref = findPreference(getString(R.string.port_number));
            if (portNumberPref != null) {

                int portNumber=pref.getInt(getString(R.string.port_number),2000);
                portNumberPref.setSummary(Integer.toString(portNumber));
                portNumberPref.setText(String.valueOf(portNumber));
                bindOnPreferenceChanges(portNumberPref);
            }

            SwitchPreferenceCompat saveStatusPref = findPreference(getString(R.string.save_status));
            if (saveStatusPref != null) {
                bindOnPreferenceChanges(saveStatusPref);
            }

        }

        @Override
        public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences,rootKey);
        }

        private void bindOnPreferenceChanges(Preference pref){
            pref.setOnPreferenceChangeListener(this);
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue){

            if(preference.getKey().equals(getString(R.string.buffer_size))){

                long bufferSize=Long.parseLong((String)newValue);
                pref.edit()
                        .putLong("buffer_size",bufferSize).apply();
                preference.setSummary(bufferSize+" MB");
            }

            else if(preference.getKey().equals(getString(R.string.streaming_speed))){

                int streamingSpeed=Integer.parseInt((String)newValue);
                pref.edit()
                        .putInt(getString(R.string.streaming_speed),streamingSpeed).apply();

                preference.setSummary(streamingSpeed+" KB/s");
            }

            else if(preference.getKey().equals(getString(R.string.seeding_speed))){

                Integer seedingSpeed=Integer.parseInt((String)newValue);
                pref.edit()
                        .putInt(getString(R.string.seeding_speed),seedingSpeed).apply();

                preference.setSummary(seedingSpeed+" KB/s");
            }

            else if(preference.getKey().equals(getString(R.string.port_number))){

                int portNumber=Integer.parseInt((String)newValue);
                pref.edit()
                        .putInt(getString(R.string.port_number),portNumber).apply();

                preference.setSummary(Integer.toString(portNumber));
            }

            else if(preference.getKey().equals(getString(R.string.save_status))){

                boolean saveStatus=(Boolean)newValue;
                Log.d("TAG",newValue.toString());
                pref.edit().putBoolean(getString(R.string.save_status),saveStatus);
            }
            return true;
        }

    }
}