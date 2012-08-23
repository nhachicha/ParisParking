package dev.nhachicha.parisparking.preference;
/*
Copyright 2012 Nabil HACHICHA

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;

import dev.nhachicha.parisparking.R;
import dev.nhachicha.parisparking.util.Constant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
/**
 * @author Nabil HACHICHA
 * http://nhachicha.wordpress.com
 */ 

@EActivity
public class Settings extends SherlockPreferenceActivity {
    private SharedPreferences mSharedPrefs;
    private Editor mEditor;
    private EditTextPreference mPref;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPrefs.edit();
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        final ActionBar ab = getSherlock().getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        
        mPref = (EditTextPreference) findPreference("pref_nbr_result");
        mPref.setOnPreferenceChangeListener(onPreferenceChangeListener);
        mPref.setSummary(getText(R.string.pref_current_value) + mSharedPrefs.getString("pref_nbr_result", ""+Constant.DEFAULT_SEARCH_NUMBER_OF_RESULT));
        
    }
    
    Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            
            int nbRslt = Constant.SEARCH_NUMBER_MAX;
            try {
                nbRslt = Integer.parseInt(newValue.toString());
                if (nbRslt<1 || nbRslt > Constant.SEARCH_NUMBER_MAX) {
                    nbRslt = Constant.SEARCH_NUMBER_MAX;
                }
            } catch (Exception e) {
                e.printStackTrace();
                //use default value
            
            } finally {
                mEditor.putString("pref_nbr_result", ""+nbRslt);
                mEditor.commit();
                preference.setSummary(getText(R.string.pref_current_value).toString() + nbRslt);
            }
            return false;
        }
    };
    
    @OptionsItem(android.R.id.home)
    void menuHome() {
        Intent intent = new Intent(this, dev.nhachicha.parisparking.MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
}
