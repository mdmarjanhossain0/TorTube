package com.victoriya.tortube.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.victoriya.tortube.R;

import java.lang.reflect.Array;

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
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey){
            Log.d(TAG,rootKey);
            setPreferencesFromResource(R.xml.root_preferences,rootKey);

            pref=PreferenceManager.getDefaultSharedPreferences(getContext());

            EditTextPreference bufferSizePref=findPreference(getString(R.string.buffer_size));
            if(bufferSizePref!=null){
                int bufferSize=pref.getInt(getString(R.string.buffer_size),1);
                bindOnPreferenceChanges(bufferSizePref);
                bufferSizePref.setSummary(bufferSize+" MB");
                bufferSizePref.setText(String.valueOf(bufferSize));
            }

            EditTextPreference streamingSpeedPref=findPreference(getString(R.string.streaming_speed));
            if(streamingSpeedPref!=null){
                streamingSpeedPref.setDefaultValue((Integer)0);
                bindOnPreferenceChanges(streamingSpeedPref);
            }

            EditTextPreference seedingSpeedPref=findPreference(getString(R.string.seeding_speed));
            if(seedingSpeedPref!=null){
                seedingSpeedPref.setDefaultValue((Integer)0);
                bindOnPreferenceChanges(seedingSpeedPref);
            }

            EditTextPreference portNumberPref=findPreference(getString(R.string.port_number));
            if(portNumberPref!=null){
                portNumberPref.setDefaultValue((Integer)2000);
                bindOnPreferenceChanges(portNumberPref);
            }

            SwitchPreference saveStatusPref=findPreference(getString(R.string.save_status));
            if(saveStatusPref!=null){
                saveStatusPref.setDefaultValue((Boolean)false);
                bindOnPreferenceChanges(saveStatusPref);
            }

        }
        private void bindOnPreferenceChanges(Preference pref){
            pref.setOnPreferenceChangeListener(this);
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue){
            if(preference.getKey().equals(getString(R.string.buffer_size))){

                int bufferSize=Integer.parseInt((String)newValue);
                pref.edit()
                        .putInt("buffer_size",bufferSize).apply();

                preference.setSummary(bufferSize+" MB");
            }
            else if(preference.getKey().equals(getString(R.string.streaming_speed))){
                int streamingSpeed=Integer.parseInt((String)newValue);
                pref.edit().putInt(getString(R.string.streaming_speed),streamingSpeed);

                preference.setSummary(streamingSpeed+" KiB");
            }
            else if(preference.getKey().equals(getString(R.string.seeding_speed))){
                Integer seedingSpeed=Integer.parseInt((String)newValue);
                pref.edit().putInt(getString(R.string.seeding_speed),seedingSpeed);

                preference.setSummary(seedingSpeed+" KiB");
            }
            else if(preference.getKey().equals(getString(R.string.port_number))){
                int portNumber=Integer.parseInt((String)newValue);
                pref.edit().putInt(getString(R.string.port_number),portNumber);

                preference.setSummary(portNumber);
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