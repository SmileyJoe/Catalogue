package com.smileyjoedev.catalogue.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.smileyjoedev.catalogue.Broadcast;
import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.R.color;
import com.smileyjoedev.catalogue.R.string;
import com.smileyjoedev.catalogue.adapters.LocationListAdapter;
import com.smileyjoedev.catalogue.db.DbLocationAdapter;
import com.smileyjoedev.catalogue.interfaces.LocationDataInterface;
import com.smileyjoedev.catalogue.interfaces.SearchDataInterface;
import com.smileyjoedev.catalogue.objects.Location;
import com.smileyjoedev.genLibrary.Debug;

public class LocationListFragment extends SherlockListFragment{

	private ArrayList<Location> locations;
	private ArrayList<Location> breadCrumb;
	private DbLocationAdapter locationAdapter;
	private long locationId;
	private Location location;
	private Context context;
	private LocationListAdapter adapter;
	private ArrayList<Long> locationIdTrail;
	private LinearLayout llBreadCrumb;
	private String searchTerm;
	private boolean onSearch;
	private Location selectedLocation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.restoreSavedState(savedInstanceState);
		this.init();
		this.getLocations();
		this.adapter = new LocationListAdapter(this.context, this.locations);
		
		this.populateBreadCrumb();
		setListAdapter(adapter);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(Constants.EXTRA_ON_SEARCH, this.onSearch);
		
		outState.putString(Constants.EXTRA_SEARCH_TERM, this.searchTerm);
		outState.putLong(Constants.EXTRA_LOCATION_ID, this.locationId);
		
		outState.putSerializable(Constants.EXTRA_LOCATION_ID_TRAIL, this.locationIdTrail);
	}
	
	private void restoreSavedState(Bundle savedInstanceState){
		if(savedInstanceState != null){
			this.onSearch = savedInstanceState.getBoolean(Constants.EXTRA_ON_SEARCH);
			
			this.locationId = savedInstanceState.getLong(Constants.EXTRA_LOCATION_ID);
			this.searchTerm = savedInstanceState.getString(Constants.EXTRA_SEARCH_TERM);
			
			this.locationIdTrail = (ArrayList<Long>) savedInstanceState.getSerializable(Constants.EXTRA_LOCATION_ID_TRAIL);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.restoreSavedState(savedInstanceState);
		this.registerForContextMenu(this.getListView());
	}
	
	public void getLocations(){
		if(this.onSearch){
			this.locations = this.locationAdapter.search(this.searchTerm);
		} else {
			this.locations = this.locationAdapter.get(this.locationId);	
		}
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(this.onSearch){
			startActivity(Intents.locationList(this.context, this.locations.get(position).getId()));
		} else {
			this.locationIdTrail.add(this.locationId);
			this.locationId = this.locations.get(position).getId();
			this.location = this.locationAdapter.getDetails(this.locationId);
			this.addToBreadCrumb();
			this.updateView();
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int menuItemIndex = item.getItemId();

		switch(menuItemIndex){
			case Constants.CONTEXT_LOCATION_EDIT:
				startActivityForResult(Intents.locationEdit(this.context, this.selectedLocation.getId()), Constants.ACTIVITY_LOCATION_EDIT);
				break;
			case Constants.CONTEXT_LOCATION_DELETE:
				startActivityForResult(Intents.popupDelete(this.context, Constants.LOCATION), Constants.ACTIVITY_LOCATION_POPUP_DELETE);
				break;
		}
		return super.onContextItemSelected(item);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case Constants.ACTIVITY_LOCATION_EDIT:
				this.updateView();
				break;
			case Constants.ACTIVITY_LOCATION_POPUP_DELETE:
				if(resultCode == Activity.RESULT_OK){
					if(data.getBooleanExtra("result", false)){
						this.locationAdapter.delete(this.selectedLocation);
						this.updateView();
					}
				}
				break;
		}
		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			LocationDataInterface hostInterface;
	        hostInterface = (LocationDataInterface) activity;
	        this.locationId = hostInterface.getLocationId();
	    } catch(ClassCastException e) {
	        this.locationId = 0;
	    }
	    
	    try {
			SearchDataInterface hostInterface;
	        hostInterface = (SearchDataInterface) activity;
	        this.searchTerm = hostInterface.getSearchTerm();
	        this.onSearch = true;
	    } catch(ClassCastException e) {
	        this.searchTerm = "";
	        this.onSearch = false;
	    }
	    
	    this.context = activity.getApplicationContext();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		
		this.selectedLocation = this.locations.get(info.position);
		Debug.d(this.selectedLocation);
		
		menu.setHeaderTitle(this.getString(R.string.context_heading));
		
		menu.add(Menu.NONE, Constants.CONTEXT_LOCATION_EDIT, Constants.CONTEXT_LOCATION_EDIT, this.getString(R.string.context_edit));
		menu.add(Menu.NONE, Constants.CONTEXT_LOCATION_DELETE, Constants.CONTEXT_LOCATION_DELETE, this.getString(R.string.context_delete));
	}
	
	public void init(){
	    this.locationAdapter = new DbLocationAdapter(this.context);
	    this.locationIdTrail = new ArrayList<Long>();
	    this.location = this.locationAdapter.getDetails(this.locationId);
	    this.breadCrumb = this.locationAdapter.getBreadCrumb(this.locationId);
	    this.llBreadCrumb = new LinearLayout(this.context);
	}
	
	public void updateView(){
		this.locations.clear();
		this.getLocations();
		this.adapter.setData(this.locations);
		this.adapter.notifyDataSetChanged();
		Broadcast.locationChanged(this.context, this.locationId);
	}
	
	public long getLocationId(){
		return this.locationId;
	}
	
	public boolean showPreviousLocation(){
		return this.showPreviousLocation(true);
	}
	
	public boolean showPreviousLocation(boolean updateView){
		if(this.locationIdTrail.size() == 0){
			return false;
		} else {
			int lastPosition = this.locationIdTrail.size()-1;
			this.locationId = this.locationIdTrail.get(lastPosition);
			this.locationIdTrail.remove(lastPosition);
			this.removeFromBreadCrumb();
			if(updateView){
				this.updateView();
			}
			return true;
		}
		
	}
	
	
	
	
	public void addToBreadCrumb(){
		this.addBreadCrumbItem(this.location);
		this.breadCrumb.add(this.location);
		
	}
	
	private void removeFromBreadCrumb(){
		this.llBreadCrumb.removeViewAt(llBreadCrumb.getChildCount()-1);
		this.breadCrumb.remove(this.breadCrumb.size()-1);
	}
	
    private void populateBreadCrumb(){
    	TextView tv;
    	int textSize = 20;
    	
    	this.llBreadCrumb.removeAllViews();
    	
    	this.addBreadCrumbItem(new Location());
		
    	for(int i = this.breadCrumb.size(); i > 0; i--){
    		this.addBreadCrumbItem(this.breadCrumb.get(i-1));
    	}
    }
    
    private void addBreadCrumbItem(Location location){
    	TextView tv  = new TextView(this.context);
		tv.setTag(location.getId());
		tv.setOnClickListener(this.breadCrumbClickListener);
    	tv.setTextSize(20);		
    	tv.setTextColor(this.context.getResources().getColor(R.color.white));
    	if(location.getId() == 0){
    		tv.setText(this.getText(R.string.root_location_title).toString());
    	} else {
    		tv.setText("/" + location.getTitle());
    	}
		
    	this.llBreadCrumb.addView(tv);
    }
    
    private OnClickListener breadCrumbClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if(llBreadCrumb.getChildCount() > 1){
				locationId = Long.parseLong(v.getTag().toString());
				if(locationId != Long.parseLong(llBreadCrumb.getChildAt(llBreadCrumb.getChildCount()-1).getTag().toString())){
					removeAfterId(locationId);
				}
			}
		}
	};
	
	public void removeAfterId(long locationId){
		
		boolean found = false;
		boolean moreLocs = true;
		int pos = this.locationIdTrail.size()-1;
		
		while(!found && moreLocs){
			if(this.locationIdTrail.get(pos) != locationId){
				moreLocs = showPreviousLocation(false);
			} else {
				moreLocs = showPreviousLocation(true);
				found = true;
			}
			pos--;
		}
	}
    
    public void updateBreadCrumbView(final HorizontalScrollView wrapper){
    	wrapper.removeAllViews();
    	wrapper.addView(this.llBreadCrumb);
    	
    	Handler handler = new Handler();
    	Runnable r = new Runnable(){
    	    public void run(){
    	    	wrapper.fullScroll(HorizontalScrollView.FOCUS_RIGHT);                      
    	    }
    	};
    	handler.postDelayed(r, 100L);
    }
    
	public void updateSearchResults(String searchTerm){
		this.searchTerm = searchTerm;
		this.updateView();
	}
	
}

