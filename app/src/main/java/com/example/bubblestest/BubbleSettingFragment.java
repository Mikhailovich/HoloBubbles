package com.example.bubblestest;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;


public class BubbleSettingFragment extends PreferenceFragmentCompat {
@Override
public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.prefs, rootKey);
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                // Load the preferences from an XML resource
                addPreferencesFromResource(R.xml.prefs);
        }
}