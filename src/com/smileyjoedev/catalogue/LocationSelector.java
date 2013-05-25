package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.KeyBoard;

public class LocationSelector extends SherlockFragmentActivity implements OnClickListener, LocationDataInterface {
	
	private HorizontalScrollView hsvBreadCrumb;
	private Button btSave;
	private Button btCancel;
	private DbLocationAdapter locationAdapter;
	private ArrayList<Location> locations;
	private Views views;
	private long locId;
	
	private BroadcastReceiver locationChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	locId = intent.getLongExtra(Constants.EXTRA_LOCATION_ID, 0);
        	if(locId == 0){
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        KeyBoard.forceClose(getWindow());

        try{
        	Bundle extras = getIntent().getExtras();
    		this.locId = extras.getLong("loc_id");
        } catch(NullPointerException e){
        	this.locId = 0;
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
    	this.locations = this.locationAdapter.get(this.locId);
    	
    	if(this.locId == 0){
    		this.btSave.setVisibility(View.GONE);
    	}
    	
    	this.getLocationListFragment().updateBreadCrumbView(this.hsvBreadCrumb);
    }

	@Override
	public void onClick(View v) {
		Intent resultIntent = new Intent();
		switch(v.getId()){
			case R.id.bt_save:
				resultIntent.putExtra("loc_id", this.locId);
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
		return this.locId;
	}
	
	@Override
	public void onBackPressed() {
		if(!getLocationListFragment().showPreviousLocation()){
			super.onBackPressed();
		}
	}
	
	private LocationListFragment getLocationListFragment(){
		return (LocationListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_location_list);
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
        		startActivityForResult(Intents.locationNew(this, this.locId), Constants.ACTIVITY_LOCATION_NEW);
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
        MenuInflater inflater = getSupportMenuInflater();
        
       	inflater.inflate(R.menu.location_selector, menu);
        
        return super.onCreateOptionsMenu(menu);
    }

}