package com.smileyjoedev.catalogue.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.Views;
import com.smileyjoedev.catalogue.db.DbLocationAdapter;
import com.smileyjoedev.catalogue.fragments.LocationListFragment;
import com.smileyjoedev.catalogue.interfaces.LocationDataInterface;
import com.smileyjoedev.catalogue.objects.Location;
import com.smileyjoedev.genLibrary.KeyBoard;

public class LocationSelector extends Activity implements OnClickListener, LocationDataInterface {
	
	private HorizontalScrollView hsvBreadCrumb;
	private Button btSave;
	private Button btCancel;
	private DbLocationAdapter locationAdapter;
	private ArrayList<Location> locations;
	private Views views;
	private long locationId;
	
	private BroadcastReceiver locationChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	locationId = intent.getLongExtra(Constants.EXTRA_LOCATION_ID, 0);
        	if(locationId == 0){
        		showSave(false);
        	} else {
        		showSave(true);
        	}

        	
        	getLocationListFragment().updateBreadCrumbView(hsvBreadCrumb);
        	
        }
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_selector);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        KeyBoard.forceClose(getWindow());

        try{
        	Bundle extras = getIntent().getExtras();
    		this.locationId = extras.getLong("loc_id");
        } catch(NullPointerException e){
        	this.locationId = 0;
        }
        
        this.initialize();
        this.populateView();
    }
    
    @Override
    protected void onResume() {
    	Intents.filterLocationChanged(this, this.locationChanged);
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
    
    public void initialize(){
    	this.hsvBreadCrumb = (HorizontalScrollView) findViewById(R.id.hsv_bread_crumb);
    	
    	this.btSave = (Button) findViewById(R.id.bt_save);
    	this.btSave.setOnClickListener(this);
    	this.btCancel = (Button) findViewById(R.id.bt_cancel);
    	this.btCancel.setOnClickListener(this);
    	
    	this.locationAdapter = new DbLocationAdapter(this);
    	
    	this.locations = new ArrayList<Location>();
    	
    	this.views = new Views(this);
    }
    
    
    public void populateView(){
    	this.locations = this.locationAdapter.get(this.locationId);
    	
    	if(this.locationId == 0){
    		this.btSave.setVisibility(View.GONE);
    	}
    	
    	this.getLocationListFragment().updateBreadCrumbView(this.hsvBreadCrumb);
    }

	@Override
	public void onClick(View v) {
		Intent resultIntent = new Intent();
		switch(v.getId()){
			case R.id.bt_save:
				resultIntent.putExtra("loc_id", this.locationId);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
				break;
			case R.id.bt_cancel:
				setResult(Activity.RESULT_CANCELED, resultIntent);
				finish();
				break;
		}
		
	}
	
	public void showSave(boolean show){
		if(show){
			this.btSave.setVisibility(View.VISIBLE);
		} else {
			this.btSave.setVisibility(View.GONE);
		}
	}
	
	@Override
	public long getLocationId() {
		return this.locationId;
	}
	
	@Override
	public void onBackPressed() {
		if(!getLocationListFragment().showPreviousLocation()){
			super.onBackPressed();
		}
	}
	
	private LocationListFragment getLocationListFragment(){
		return (LocationListFragment) getFragmentManager().findFragmentById(R.id.frag_location_list);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case Constants.ACTIVITY_LOCATION_NEW:
				if(resultCode == Activity.RESULT_OK){
					getLocationListFragment().updateView();
				} else {
				}
				break;
		}
	}
	
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_add_location:
        		startActivityForResult(Intents.locationNew(this, this.locationId), Constants.ACTIVITY_LOCATION_NEW);
				return true;
			case android.R.id.home:
				this.onBackPressed();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
       	inflater.inflate(R.menu.location_selector, menu);
        
        return super.onCreateOptionsMenu(menu);
    }

}