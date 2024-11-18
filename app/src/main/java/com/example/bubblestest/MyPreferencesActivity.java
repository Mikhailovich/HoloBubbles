package com.example.bubblestest;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.bubblestest.BubbleSettingFragment;

public class MyPreferencesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bubble_settings);

        //If you want to insert data in your settings
        BubbleSettingFragment settingsFragment = new BubbleSettingFragment();
        //getSupportFragmentManager().beginTransaction().replace(R.id.bubble_settings1,settingsFragment).commit();

        //Else
        getSupportFragmentManager().beginTransaction().replace(R.id.bubble_settings1,new BubbleSettingFragment()).commit();
    }

}