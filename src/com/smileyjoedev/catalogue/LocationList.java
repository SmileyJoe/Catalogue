package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.Notify;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.widget.RelativeLayout;

public class LocationList extends SherlockFragmentActivity implements LocationDataInterface {

	private long locationId = 0;
	private ArrayList<Long> locationIdTrail;
	
    private BroadcastReceiver locationChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	locationId = intent.getLongExtra(Constants.EXTRA_LOCATION_ID, 0);
        	if(locationId == 0){
        		getSupportActionBar().getTabAt(0).setText("Locations");
        	} else {
        		getSupportActionBar().getTabAt(0).setText("Sub-Locations");
        	}
        	
        	try{
        		getItemFragment().changeLocation(locationId);
        	} catch(NullPointerException e){
        		e.printStackTrace();
        	}
        	
        	
        	Debug.d(locationId);
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.init();
        this.setTabs();
	}
	
	private void init(){
		this.locationIdTrail = new ArrayList<Long>();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        
       	inflater.inflate(R.menu.location_list, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_add_location:
        		startActivityForResult(Intents.locationNew(this, getLocationFragment().getLocationId()), Constants.ACTIVITY_LOCATION_NEW);
				return true;
			case android.R.id.home:
				this.onBackPressed();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
	
	public void setTabs(){
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
  	
	  	ActionBar.Tab newTab0 = getSupportActionBar().newTab();
	  	if(this.locationId == 0){
	  		newTab0.setText("Locations");
	  	} else {
	  		newTab0.setText("Sub-Locations");
	  	}
//	  	newTab0.setTabListener(this);
	  	newTab0.setTabListener(new TabListener<LocationListFragment>(
                this, Integer.toString(Constants.TAB_LOCATION), LocationListFragment.class, null));
	  	newTab0.setTag(Constants.TAB_LOCATION);
	  	ActionBar.Tab newTab1 = getSupportActionBar().newTab();
	  	newTab1.setText("Items");
//	  	newTab1.setTabListener(this);
	  	newTab1.setTabListener(new TabListener<ItemListFragment>(
                this, Integer.toString(Constants.TAB_ITEM), ItemListFragment.class, null));
	  	newTab1.setTag(Constants.TAB_ITEM);
	  	getSupportActionBar().addTab(newTab0);
	  	getSupportActionBar().addTab(newTab1);
	}

	@Override
	public long getLocationId() {
		return this.locationId;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case Constants.ACTIVITY_LOCATION_NEW:
				if(resultCode == Activity.RESULT_OK){
					ActionBar.Tab currentTab = getSupportActionBar().getSelectedTab();
					Debug.d();
					Debug.d(currentTab.getText().toString());
					
					switch(Integer.parseInt(currentTab.getTag().toString())){
						case Constants.TAB_LOCATION:
							LocationListFragment locationFragment = getLocationFragment();
							locationFragment.updateView();
							break;
					}
				} else {
				}
				break;
		}
	}
	
    @Override
    protected void onResume() {
        IntentFilter locationChangedFilter = new IntentFilter();
        locationChangedFilter.addAction(Broadcast.LOCATION_CHANGED);
        registerReceiver(this.locationChanged, locationChangedFilter);

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(this.locationChanged);
        super.onPause();
    }
    
    private LocationListFragment getLocationFragment(){
    	return (LocationListFragment) getSupportFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_LOCATION));
    }
    
    private ItemListFragment getItemFragment() throws NullPointerException{
    	try{
    		return (ItemListFragment) getSupportFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_ITEM));
    	} catch(NullPointerException e){
    		throw e;
    	}
    	
    }
    
    @Override
    public void onBackPressed() {
    	if(!getLocationFragment().showPreviousLocation()){
    		super.onBackPressed();
    	}
    }

}
