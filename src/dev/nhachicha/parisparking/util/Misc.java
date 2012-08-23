package dev.nhachicha.parisparking.util;

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

import dev.nhachicha.parisparking.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * @author Nabil HACHICHA
 * http://nhachicha.wordpress.com
 */

public class Misc {

    /*1 mile ~  1.609344 kilometer;*/
    
    public static final String getHumanReadableDistance (Context ctx, double distance) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        int selectedUnitSys = Integer.parseInt(prefs.getString("pref_units_system", "0"));
        
        if (0 == selectedUnitSys) {//metric
            if (distance > 1000) {
                return Math.round(distance / 1000) + " " + ctx.getText(R.string.unit_kilo);
                
            } else {
                return Math.round(distance) + " " + ctx.getText(R.string.unit_meter);
            }
            
        } else {//imperial
            distance *= 1.09361; //meter to yard
            
            if (distance > 1760) {// 1 mile == 1760 yard
                return Math.round(distance/1760) + " " + ctx.getText(R.string.unit_mile);
                
            } else {
                return Math.round(distance) + " " + ctx.getText(R.string.unit_yard);
            }
        }
    }
}
