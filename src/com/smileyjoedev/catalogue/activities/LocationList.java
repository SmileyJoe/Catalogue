package com.smileyjoedev.catalogue.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;

import com.smileyjoedev.catalogue.Broadcast;
import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.GenViews;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.TabListener;
import com.smileyjoedev.catalogue.fragments.ItemListFragment;
import com.smileyjoedev.catalogue.fragments.LocationListFragment;
import com.smileyjoedev.catalogue.interfaces.LocationDataInterface;
import com.smileyjoedev.genLibrary.Debug;

public class LocationList extends Base implements LocationDataInterface {

	private long locationId = 0;
	
    private BroadcastReceiver locationChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	locationId = intent.getLongExtra(Constants.EXTRA_LOCATION_ID, 0);
        	if(locationId == 0){
        		getActionBar().getTabAt(0).setText("Locations");
        	} else {
        		getActionBar().getTabAt(0).setText("Sub-Locations");
        	}
        	
        	try{
        		getItemFragment().changeLocation(locationId);
        	} catch(NullPointerException e){
        		e.printStackTrace();
        	}
        	
        	updateBreadCrumb();
        	
        	Debug.d(locationId);
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list);
        this.enableDrawer();
        GenViews.createBreadCrumbActionBar(this, getActionBar());
        
        try{
        	Bundle extras = getIntent().getExtras();
        	if(extras.containsKey(Constants.EXTRA_LOCATION_ID)){
        		this.locationId = extras.getLong(Constants.EXTRA_LOCATION_ID);
        	} else {
        		this.locationId = 0;
        	}
        } catch(NullPointerException e){
        	this.locationId = 0;
        }
        
        this.init();
        this.setTabs();
	}
	
	private void init(){
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
       	inflater.inflate(R.menu.location_list, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_add_location:
        		startActivityForResult(Intents.locationNew(this, getLocationFragment().getLocationId()), Constants.ACTIVITY_LOCATION_NEW);
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
	
	public void setTabs(){
	  	FragmentTabHost thContent = (FragmentTabHost) findViewById(R.id.th_content);
	  	thContent.setup(this, getFragmentManager(), R.id.fl_tab_content);
	  	
	  	GenViews.addLocationListTab(thContent, this.locationId);
	  	GenViews.addItemListTab(thContent);
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
					FragmentTabHost thContent = (FragmentTabHost) findViewById(R.id.th_content);
					
					switch(Integer.parseInt(thContent.getCurrentTabTag().toString())){
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
        this.updateBreadCrumb();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(this.locationChanged);
        super.onPause();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putLong(Constants.EXTRA_LOCATION_ID, this.locationId);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	if(savedInstanceState != null){
    		this.locationId = savedInstanceState.getLong(Constants.EXTRA_LOCATION_ID);
    	}
    }
    
    private LocationListFragment getLocationFragment(){
    	return (LocationListFragment) getFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_LOCATION));
    }
    
    private ItemListFragment getItemFragment() throws NullPointerException{
    	try{
    		return (ItemListFragment) getFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_ITEM));
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

    private void updateBreadCrumb(){
    	try{
    		getLocationFragment().updateBreadCrumbView((HorizontalScrollView) findViewById(R.id.hsv_actionbar_breadcrumb));
    	} catch(NullPointerException e) {
    		e.printStackTrace();
    	}    	
    }
}
