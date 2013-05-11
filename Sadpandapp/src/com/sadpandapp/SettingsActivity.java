package com.sadpandapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import com.sadpandapp.R;


public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	public static final String KEY_USERNAME = "pref_username";
	public static final String KEY_PASSWORD = "pref_password";
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		//Set each preference summary with his value
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        EditTextPreference editTextPref = (EditTextPreference) findPreference(KEY_USERNAME);
        editTextPref
                .setSummary(sp.getString(KEY_USERNAME, "Some Default Text"));
        editTextPref = (EditTextPreference) findPreference(KEY_PASSWORD);
        editTextPref
                .setSummary(sp.getString(KEY_PASSWORD, "Some Default Text"));

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
	    super.onResume();
	    // Set up a listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@SuppressWarnings("deprecation")	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
	  // Update summary value
	  EditTextPreference pref = (EditTextPreference)findPreference(key);
	  pref.setSummary(pref.getText());
	  this.onContentChanged();
	}
}
