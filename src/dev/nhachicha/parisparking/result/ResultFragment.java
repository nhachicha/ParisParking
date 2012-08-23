package dev.nhachicha.parisparking.result;
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
import com.actionbarsherlock.app.SherlockListFragment;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;

import dev.nhachicha.parisparking.R;
import dev.nhachicha.parisparking.util.Constant;
import dev.nhachicha.parisparking.util.IconContextMenu;
import dev.nhachicha.parisparking.util.Misc;
import dev.nhachicha.parisparking.util.RESTHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 * @author Nabil HACHICHA
 * http://nhachicha.wordpress.com
 */

@EFragment(R.layout.fragment_result)
public class ResultFragment extends SherlockListFragment {
    private final int MENU_ITEM_ACTION_NAV = 1;
    private final int MENU_ITEM_ACTION_MAP = MENU_ITEM_ACTION_NAV+1;
    private IconContextMenu iconContextMenu = null;
    
    private List<double[]> mLocations = new ArrayList<double[]>();
    private List<String> mDistances = new ArrayList<String> ();
    private double[] mPosition = null;
    
    @Background
    void findNearParking () {
        try {
            Context ctx= getActivity();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            
            int nbOfResult = Integer.parseInt(prefs.getString("pref_nbr_result", "" + Constant.DEFAULT_SEARCH_NUMBER_OF_RESULT));
            String payload = String.format(Locale.ENGLISH, Constant.JSON_SEARCH_PAYLOAD_TEMPLATE, 
                                            mPosition[1],
                                            mPosition[0],
                                            nbOfResult);
            
            String json = RESTHelper.postJson(Constant.JSON_SEARCH_URL, payload);
            JSONObject jObj = new JSONObject(json);
            JSONArray jArr = (JSONArray)jObj.get("results");
            int length = jArr.length();
            
            for (int i=0; i<length; i++) {
                JSONArray loc = ((JSONObject)jArr.get(i)).getJSONObject("obj").getJSONArray("loc");
                double distance = (Double) ((JSONObject)jArr.get(i)).get("dis");
                
                mLocations.add(new double[]{(Double) loc.get(1), (Double) loc.get(0)});
                mDistances.add(Misc.getHumanReadableDistance(ctx, distance));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshList ();
    }
    
      
    @UiThread
    void refreshList() {
        setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, mDistances));
        
        if (Constant.DEVELOPER_MODE) {// Inform the list we provide context menus for items
            getListView().setOnCreateContextMenuListener(this);
        }
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if(null != args) {
            mPosition = (double[])args.get("location");
        } 
        
        
        final ActionBar ab = getSherlockActivity().getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        findNearParking ();
        
        Resources res = getResources();
        iconContextMenu = new IconContextMenu(getActivity());
        iconContextMenu.addItem(res, getText(R.string.action_start_nav), R.drawable.ic_feature_navigation, MENU_ITEM_ACTION_NAV);
        iconContextMenu.addItem(res, getText(R.string.action_start_map), R.drawable.ic_feature_map, MENU_ITEM_ACTION_MAP);
        
        iconContextMenu.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
                        @Override
                        public void onClick(int menuId) {
                                double[] loc = mLocations.get(iconContextMenu.getPosition());
                                switch(menuId) {
                                case MENU_ITEM_ACTION_NAV:
                                        String url = "google.navigation:q=" + loc[0] + ","   + loc[1];
                                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(i);
                                        break;
                                case MENU_ITEM_ACTION_MAP:
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("geo:0,0?q="+loc[0]+","+loc[1]+" (" + getText(R.string.parking_marker) + ")"));
                                        startActivity(intent);
                                        break;

                                }
                        }
                });
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) 
    {    
        new ContextMenuActionFragment(position).show(getFragmentManager(), "ContextMenuActionFragment");
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (Constant.DEVELOPER_MODE) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
        
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (Constant.DEVELOPER_MODE) {
            switch (item.getItemId()) {
                case R.id.context_view:
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q="+mPosition[0]+","+mPosition[1]+" (" + getText(R.string.parking_marker) + ")"));
                    startActivity(intent);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
            
        } else {
            return super.onContextItemSelected(item);
        }
        
    }
    
    public class ContextMenuActionFragment extends DialogFragment {
        private int mPosition = -1;// selected item position 
        public ContextMenuActionFragment (int position) {
                mPosition = position;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return iconContextMenu.createMenu(getText(R.string.action_menu_title), mPosition);
        }
    }
}
