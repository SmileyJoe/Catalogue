package com.smileyjoedev.catalogue.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.adapters.ItemListAdapter;
import com.smileyjoedev.catalogue.db.DbItemAdapter;
import com.smileyjoedev.catalogue.interfaces.CategoryDataInterface;
import com.smileyjoedev.catalogue.interfaces.LocationDataInterface;
import com.smileyjoedev.catalogue.interfaces.SearchDataInterface;
import com.smileyjoedev.catalogue.objects.Item;
import com.smileyjoedev.genLibrary.Debug;

public class ItemListFragment extends ListFragment{

	private ArrayList<Item> items;
	private DbItemAdapter itemAdapter;
	private long categoryId;
	private long locationId;
	private Context context;
	private ItemListAdapter adapter;
	private boolean onCategories;
	private boolean onLocations;
	private boolean onSearch;
	private String searchTerm;
	private Item selectedItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Debug.d("OnCreate");
		super.onCreate(savedInstanceState);
		Debug.d("## onCreate");
		this.restoreSavedState(savedInstanceState);
		this.init();
		
		setListAdapter(adapter);
	}
	
	public void init(){
		this.itemAdapter = new DbItemAdapter(this.context);
		this.getItems();
		this.adapter = new ItemListAdapter(this.context, this.items);
	}
	
	public void getItems(){
		Debug.d("getItems");
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
		} else if(this.onSearch){
			Debug.d("On Search");
			if(this.searchTerm.equals("")){
				this.items = this.itemAdapter.get();
			} else {
				this.items = this.itemAdapter.search(this.searchTerm);
			}
		} else {
			Debug.d("On Nothing");
			this.items = this.itemAdapter.get();
		}
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		Debug.d("onSaveInstanceState");
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(Constants.EXTRA_ON_CATEGORY, this.onCategories);
		outState.putBoolean(Constants.EXTRA_ON_LOCATION, this.onLocations);
		outState.putBoolean(Constants.EXTRA_ON_SEARCH, this.onSearch);
		
		outState.putString(Constants.EXTRA_SEARCH_TERM, this.searchTerm);
		outState.putLong(Constants.EXTRA_CATEGORY_ID, this.categoryId);
		outState.putLong(Constants.EXTRA_LOCATION_ID, this.locationId);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Debug.d("onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		this.restoreSavedState(savedInstanceState);
		this.registerForContextMenu(this.getListView());
	}
	
	private void restoreSavedState(Bundle savedInstanceState){
		if(savedInstanceState != null){
			this.onCategories = savedInstanceState.getBoolean(Constants.EXTRA_ON_CATEGORY);
			this.onLocations = savedInstanceState.getBoolean(Constants.EXTRA_ON_LOCATION);
			this.onSearch = savedInstanceState.getBoolean(Constants.EXTRA_ON_SEARCH);
			
			this.categoryId = savedInstanceState.getLong(Constants.EXTRA_CATEGORY_ID);
			this.locationId = savedInstanceState.getLong(Constants.EXTRA_LOCATION_ID);
			this.searchTerm = savedInstanceState.getString(Constants.EXTRA_SEARCH_TERM);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Debug.d("onListItemClick");
		startActivityForResult(Intents.itemView(this.context, ((Item) this.getListView().getItemAtPosition(position)).getId()), Constants.ACTIVITY_ITEM_VIEW);
	}

	@Override
	public void onAttach(Activity activity) {
		Debug.d("onAttach");
		super.onAttach(activity);
		
		try {
			CategoryDataInterface hostInterface;
	        hostInterface = (CategoryDataInterface) activity;
	        this.categoryId = hostInterface.getCategoryId();
	        Debug.d("## onCategories");
	        this.onCategories = true;
	        this.onLocations = false;
	        this.onSearch = false;
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
	        this.onSearch = false;
	    } catch(ClassCastException e) {
	    	this.onLocations = false;
	    	this.locationId = -1;
	    }
	    
	    try {
	    	SearchDataInterface hostInterface;
	        hostInterface = (SearchDataInterface) activity;
	        this.searchTerm = hostInterface.getSearchTerm();
	        Debug.d("## onSearch");
	        this.onLocations = false;
	        this.onCategories = false;
	        this.onSearch = true;
	    } catch(ClassCastException e) {
	    	this.onSearch = false;
	    	this.searchTerm = "";
	    }
	    
	    this.context = activity.getApplicationContext();
	}
	
	public void updateView(){
		Debug.d("updateView");
		Debug.d("## UpdateView");
		this.items.clear();
		this.getItems();
		Debug.d(this.items.size());
		this.adapter.setData(this.items);
		this.adapter.notifyDataSetChanged();
	}
	
	public void changeCategory(long categoryId){
		Debug.d("changeCategory");
		this.categoryId = categoryId;
		this.updateView();
	}
	
	public void changeLocation(long locationId){
		Debug.d("changeLocation");
		this.locationId = locationId;
		this.updateView();
	}
	
	public void updateSearchResults(String searchTerm){
		Debug.d("updateSearchResults");
		this.searchTerm = searchTerm;
		this.updateView();
	}
	
	public void getCategory(){
		Debug.d("getCategory");
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		
		this.selectedItem = this.items.get(info.position);
		Debug.d(this.selectedItem);
		
		menu.setHeaderTitle(this.getString(R.string.context_heading));
		
		menu.add(Menu.NONE, Constants.CONTEXT_ITEM_EDIT, Constants.CONTEXT_ITEM_EDIT, this.getString(R.string.context_edit));
		menu.add(Menu.NONE, Constants.CONTEXT_ITEM_DELETE, Constants.CONTEXT_ITEM_DELETE, this.getString(R.string.context_delete));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int menuItemIndex = item.getItemId();

		switch(menuItemIndex){
			case Constants.CONTEXT_ITEM_EDIT:
				startActivityForResult(Intents.itemEdit(this.context, this.selectedItem.getId()), Constants.ACTIVITY_ITEM_EDIT);
				break;
			case Constants.CONTEXT_ITEM_DELETE:
				startActivityForResult(Intents.popupDelete(this.context, Constants.ITEM), Constants.ACTIVITY_ITEM_POPUP_DELETE);
				break;
		}
		return super.onContextItemSelected(item);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case Constants.ACTIVITY_ITEM_EDIT:
				this.updateView();
				break;
			case Constants.ACTIVITY_ITEM_POPUP_DELETE:
				if(resultCode == Activity.RESULT_OK){
					if(data.getBooleanExtra("result", false)){
						this.itemAdapter.delete(this.selectedItem);
						this.updateView();
					}
				}
				break;
			case Constants.ACTIVITY_ITEM_VIEW:
				this.updateView();
				break;
		}
		
	}
	
}

