package dev.nhachicha.parisparking.find;
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
import dev.nhachicha.parisparking.result.ResultFragment;
import dev.nhachicha.parisparking.result.ResultFragment_;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
/**
 * @author Nabil HACHICHA
 * http://nhachicha.wordpress.com
 */

@EFragment
public class FindFragment extends SherlockListFragment {
    private static ProgressDialog mPd;
    private static List<Address> mFoundAdresses;
    private double lat;
    private double lon;
    protected static final int LOCATIONS_DIALOG = 0;
    private static String[] mItems;
    
    private Location mBestLocation = null;
    private Location mTmpLocation = null;
    private LocationManager mLocationManager;
    private SearchGpsDialogFragment mDialog;
    
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private boolean mForceStop = false;    
    
    
    public static final int[] TITLES = 
    {
        R.string.find_by_current_location_title,   
        R.string.find_by_address_title
    };
    
    public static final int[] DETAILS = 
    {
        R.string.find_by_current_location_detail,   
        R.string.find_by_address_detail
    };
    
    public static final int[] ICONS = 
    {
        R.drawable.ic_action_locate,   
        R.drawable.ic_action_search
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final ActionBar ab = getSherlockActivity().getSupportActionBar();
        ab.setDisplayUseLogoEnabled(true);
        setListAdapter(new MyAdapter(getActivity()));
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
       // Check if the GPS setting is currently enabled on the device.
        mLocationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            // call enableLocationSettings()
            new EnableGpsDialogFragment().show(getFragmentManager(), "enableGpsDialog");
            
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);
        }
        
        // Get a reference to the LocationManager object.
        //mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(mListener);
    }

    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        switch (position) {
            case 0: {
                mForceStop = false;
                getCurrentPosition();
                
                mDialog = new SearchGpsDialogFragment();
                mDialog.setCancelable(false);
                mDialog.show(getFragmentManager(),"SearchGpsDialogFragment");
                break;
            }
            
            case 1: {
                new GetAddressDialogFragment().show(getFragmentManager(),"GetAddressDialogFragment");
                break;
            }
        }
    }

    private class MyAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        
        public MyAdapter(Context context) {
            super();
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.main_list_item, null);
            } 
            ((TextView)(convertView.findViewById(R.id.mytext1))).setText(TITLES[position]);
            ((TextView)(convertView.findViewById(R.id.mytext2))).setText(DETAILS[position]);
            ((ImageView)(convertView.findViewById(R.id.icon))).setBackgroundResource(ICONS[position]);

            return convertView;
        }
    }
    
    
    @Background
    void getLocation (String address) {
        manageProgressBar(true);
        
        try {
            Geocoder gc = new Geocoder(getActivity());
            mFoundAdresses = gc.getFromLocationName(address+" Paris, France", 3); // Search addresses
            if (null != mFoundAdresses && !mFoundAdresses.isEmpty()) {
                
                mItems = new String [mFoundAdresses.size()];
                for (int i = 0; i < mFoundAdresses.size(); ++i) {
                    // Save results as Longitude and Latitude
                    Address x = mFoundAdresses.get(i);
                    lat = x.getLatitude();
                    lon = x.getLongitude();
                    if (null == x.getAddressLine(0) && null == x.getAddressLine(1)) {
                        //just display Lat/Long
                        mItems [i] ="Lat: " + lat + " Long: " + lon;
                    } else {
                        String part1  = x.getAddressLine(0);
                        String part2  = x.getAddressLine(1);
                        mItems [i] = ((null != part1)?part1:"") + " " +((null !=part2)?part2:"");
                    }
                }
                
            } else {
                //no message
            }
            
            
        } catch (IOException e) {
            // Unable to parse response from server (Check the network)
            e.printStackTrace();
            
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            manageProgressBar(false);
        }
        
    }
    
    @Background
    void getCurrentPosition() {
            while(!mForceStop) {
                if (mTmpLocation != null) {
                    mBestLocation = getBetterLocation(mTmpLocation, mBestLocation);
                    if (mBestLocation.getAccuracy()<11) {//10 meters accuracy
                        updateUI(0);
                        break;
                    }
                 }
            }
    }
    
    @UiThread
    void manageProgressBar(boolean show) {
        if (show) {
            mPd = ProgressDialog.show(getActivity(), 
                                      getText(R.string.dlg_search_address_title),
                                      getText(R.string.dlg_search_address_msg),
                                      true,
                                      false); 
        } else {
            mPd.dismiss();
            if (null != mFoundAdresses && !mFoundAdresses.isEmpty()) {
                DialogFragment newFragment = MyAlertDialogFragment.newInstance();
                newFragment.show(getFragmentManager(), "MyAlertDialogFragment");
                
            } else {
                Toast.makeText(getActivity(), R.string.dlg_search_no_loc_found, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @UiThread
    public void updateUI(int action) {
        mDialog.dismiss();
        
        switch(action) {
            case 0: {
                ResultFragment fragment = new ResultFragment_();
                Bundle args = new Bundle();
                args.putDoubleArray("location", new double[]{mBestLocation.getLatitude(), mBestLocation.getLongitude()});
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
                break;
            }
        }
    }
    
    /**
     * Dialog to prompt users to enable GPS on the device.
     */
    static class EnableGpsDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.enable_gps)
                    .setMessage(R.string.enable_gps_dialog)
                    .setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(settingsIntent);
                        }
                    })
                    .create();
        }
    }
    
    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance() {
            return new MyAlertDialogFragment();
        }
        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.dlg_search_choose_address)//don't work if we pass plain string 
            .setItems(mItems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                     // Set the chosen location 
                    Address x = mFoundAdresses.get(which);
                    double[] location = new double[2];
                    
                    ResultFragment fragment = new ResultFragment_();
                    Bundle args = new Bundle();
                    location[0] = x.getLatitude();
                    location[1] = x.getLongitude();
                    args.putDoubleArray("location", location);
                    fragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
                    
                 }
            })
            .create();
        }
    }
    
    public class SearchGpsDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getText(R.string.dlg_standard_loading));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    mForceStop = true;
                }
            });
            return dialog;
            
        }
    }
    
    public class GetAddressDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
            final EditText address =  (EditText) textEntryView.findViewById(R.id.address_edit);
            return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.alert_dialog_text_entry)
                .setView(textEntryView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getLocation(address.getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
            
        }
    }
    
    private final LocationListener mListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            updateUILocation(location);
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    
    private void updateUILocation(Location location) {
         mTmpLocation = location;
    }
    
    /** Determines whether one Location reading is better than the current Location fix.
     * Code taken from
     * http://developer.android.com/guide/topics/location/obtaining-user-location.html
     *
     * @param newLocation  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new
     *        one
     * @return The better Location object based on recency and accuracy.
     */
   protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
       if (currentBestLocation == null) {
           // A new location is always better than no location
           return newLocation;
       } else if (newLocation == null) {
           return currentBestLocation;
       }

       // Check whether the new location fix is newer or older
       long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
       boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
       boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
       boolean isNewer = timeDelta > 0;

       // If it's been more than two minutes since the current location, use the new location
       // because the user has likely moved.
       if (isSignificantlyNewer) {
           return newLocation;
       // If the new location is more than two minutes older, it must be worse
       } else if (isSignificantlyOlder) {
           return currentBestLocation;
       }

       // Check whether the new location fix is more or less accurate
       int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
       boolean isLessAccurate = accuracyDelta > 0;
       boolean isMoreAccurate = accuracyDelta < 0;
       boolean isSignificantlyLessAccurate = accuracyDelta > 200;

       // Check if the old and new location are from the same provider
       boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
               currentBestLocation.getProvider());

       // Determine location quality using a combination of timeliness and accuracy
       if (isMoreAccurate) {
           return newLocation;
       } else if (isNewer && !isLessAccurate) {
           return newLocation;
       } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
           return newLocation;
       }
       return currentBestLocation;
   }
    
   /** Checks whether two providers are the same */
   private boolean isSameProvider(String provider1, String provider2) {
       if (provider1 == null) {
         return provider2 == null;
       }
       return provider1.equals(provider2);
   }
   
    
}
