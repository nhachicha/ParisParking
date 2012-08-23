package dev.nhachicha.parisparking;

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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Nabil HACHICHA
 * http://nhachicha.wordpress.com
 */


@EActivity(R.layout.activity_main)
public class MainActivity extends SherlockFragmentActivity {
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        dev.nhachicha.parisparking.find.FindFragment_ fragment = new dev.nhachicha.parisparking.find.FindFragment_();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @OptionsItem(android.R.id.home)
    void menuHome() {
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    @OptionsItem(R.id.menu_preference)
    void menuPreferences() {
        Intent intent = new Intent (this, dev.nhachicha.parisparking.preference.Settings_.class);
        startActivity(intent);
    }
    
    @OptionsItem(R.id.menu_apropos)
    void menuAbout() {
        dev.nhachicha.parisparking.about.AboutFragment_ fragment = new dev.nhachicha.parisparking.about.AboutFragment_();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
    }
}
