package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getLocations();
		this.adapter = new LocationListAdapter(this.context, this.locations);
		
		this.populateBreadCrumb();
		setListAdapter(adapter);
	}
	
	public void getLocations(){
		this.locations = this.locationAdapter.get(this.locationId);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(getActivity(),getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
		this.locationIdTrail.add(this.locationId);
		this.locationId = this.locations.get(position).getId();
		this.location = this.locationAdapter.getDetails(this.locationId);
		this.addToBreadCrumb();
		this.updateView();
		Broadcast.locationChanged(this.context, this.locationId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		LocationDataInterface hostInterface;
		try {
	        hostInterface = (LocationDataInterface) activity;
	    } catch(ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement LocationData");
	    }
	    
	    this.locationId = hostInterface.getLocationId();
	    this.context = activity.getApplicationContext();
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
				Broadcast.locationChanged(this.context, this.locationId);
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
	
}

