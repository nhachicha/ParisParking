package dev.nhachicha.parisparking.about;
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
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.googlecode.androidannotations.annotations.EFragment;

import dev.nhachicha.parisparking.R;

import android.os.Bundle;
/**
 * @author Nabil HACHICHA
 * http://nhachicha.wordpress.com
 */

@EFragment(R.layout.fragment_about)
public class AboutFragment extends SherlockFragment {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final ActionBar ab = getSherlockActivity().getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        menu.setGroupEnabled(R.id.group_apropos, false);
    }
}
