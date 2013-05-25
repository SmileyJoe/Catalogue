package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.smileyjoedev.genLibrary.Debug;

public class ItemListFragment extends SherlockListFragment{

	private ArrayList<Item> items;
	private DbItemAdapter itemAdapter;
	private long categoryId;
	private long locationId;
	private Context context;
	private ItemListAdapter adapter;
	private boolean onCategories;
	private boolean onLocations;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Debug.d("## onCreate");
		this.itemAdapter = new DbItemAdapter(this.context);
		this.getItems();
		this.adapter = new ItemListAdapter(this.context, this.items);
		
		setListAdapter(adapter);
	}
	
	public void getItems(){
		
		if(this.onCategories){
			Debug.d("On Categories");
			if(this.categoryId == 0){
				this.items = this.itemAdapter.get();
			} else {
				this.items = this.itemAdapter.getByCategory(this.categoryId);
			}
			
		} else if (this.onLocations){
			Debug.d("on Locations");
			if(this.locationId == 0){
				this.items = this.itemAdapter.get();
			} else {
				this.items = this.itemAdapter.getByLocation(this.locationId);
			}
			
		} else {
			Debug.d("On Nothing");
			this.items = this.itemAdapter.get();
		}
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(getActivity(),getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		
		try {
			CategoryDataInterface hostInterface;
	        hostInterface = (CategoryDataInterface) activity;
	        this.categoryId = hostInterface.getCategoryId();
	        Debug.d("## onCategories");
	        this.onCategories = true;
	        this.onLocations = false;
	    } catch(ClassCastException e) {
	    	this.onCategories = false;
	    	this.categoryId = -1;
	    }
	    
	    try {
	    	LocationDataInterface hostInterface;
	        hostInterface = (LocationDataInterface) activity;
	        this.locationId = hostInterface.getLocationId();
	        Debug.d("## onLocations");
	        this.onLocations = true;
	        this.onCategories = false;
	    } catch(ClassCastException e) {
	    	this.onLocations = false;
	    	this.locationId = -1;
	    }
	    
	    if(!this.onCategories && !this.onLocations){
	    	throw new ClassCastException(activity.toString() + " must implement CategoryDataInterface or LocationDataInterface");
	    }
	    
	    this.context = activity.getApplicationContext();
	}
	
	public void updateView(){
		this.items.clear();
		this.getItems();
		this.adapter.setData(this.items);
		this.adapter.notifyDataSetChanged();
	}
	
	public void changeCategory(long categoryId){
		this.categoryId = categoryId;
		this.updateView();
	}
	
	public void changeLocation(long locationId){
		this.locationId = locationId;
		this.updateView();
	}
	
	public void getCategory(){
		
	}
	
}

