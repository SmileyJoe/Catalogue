package com.smileyjoedev.catalogue.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.smileyjoedev.catalogue.BaseNav;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.genLibrary.Debug;

public class Base extends BaseNav implements OnItemClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
	}
	
	@Override
	public void setContentView(int layoutResId) {
		super.setContentView(layoutResId);
		
		setNavAdapter(new ArrayAdapter<String>(this, R.layout.nav_drawer_item, getResources().getStringArray(R.array.array_nav_menu)));
		
		setNavClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String[] items = getResources().getStringArray(R.array.array_nav_menu);
		String selectedItem = items[position].toLowerCase();
		
		if(selectedItem.equals("items")){
			startActivity(Intents.itemList(this));
		} else if(selectedItem.equals("locations")){
			startActivity(Intents.locationList(this));
		} else if(selectedItem.equals("categories")){
			startActivity(Intents.categoryList(this));
		} else if(selectedItem.equals("search")){
			startActivity(Intents.search(this));
		}
		
		this.hideNav();
		finish();
	}

}
