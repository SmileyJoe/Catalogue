package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.Screen;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

public class Views {
	
	private Context context;
	private Screen screen;
	private WindowManager windowManager;
	
	/******************************************************
	 * CONSTRUCTORS
	 *****************************************************/
	
	public Views(Context context) {
		this.context = context;
		this.initialize();
	}
	
	public Views(Context context, WindowManager windowManager) {
		this.context = context;
		this.windowManager = windowManager;
		this.initialize();
	}
	
	private void initialize() {
		this.screen = new Screen(this.windowManager);
	}
	


	public void categoryList(ArrayList<Category> categories, ListView lvCategoryList) {
//		if(this.checkListContents(categories, lvCategoryList, Constants.CATEGORY)){
			int first = lvCategoryList.getFirstVisiblePosition();
			View top_child = lvCategoryList.getChildAt(0);
			int top;
			
			if(top_child == null){
				top = 0;
			}else{
				top = top_child.getTop();
			}
			
			CategoryListAdapter adapter = new CategoryListAdapter(this.context, categories);
			lvCategoryList.setAdapter(adapter);
			lvCategoryList.setSelectionFromTop(first, top);
//		}
	}
	
	public void locationList(ArrayList<Location> locations, ListView lvLocationList) {
//		if(this.checkListContents(locations, lvLocationList, Constants.LOCATION)){
			int first = lvLocationList.getFirstVisiblePosition();
			View top_child = lvLocationList.getChildAt(0);
			int top;
			
			if(top_child == null){
				top = 0;
			}else{
				top = top_child.getTop();
			}
			
			LocationListAdapter adapter = new LocationListAdapter(this.context, locations);
			lvLocationList.setAdapter(adapter);
			lvLocationList.setSelectionFromTop(first, top);
//		}
	}
	
	public void itemList(ArrayList<Item> items, ListView lvItemList) {
//		if(this.checkListContents(items, lvItemList, Constants.ITEM)){
			int first = lvItemList.getFirstVisiblePosition();
			View top_child = lvItemList.getChildAt(0);
			int top;
			
			if(top_child == null){
				top = 0;
			}else{
				top = top_child.getTop();
			}
			
			ItemListAdapter adapter = new ItemListAdapter(this.context, items);
			lvItemList.setAdapter(adapter);
			lvItemList.setSelectionFromTop(first, top);
//		}
	}

	/***********************************************************
	 * PRIVATE
	 **********************************************************/
	
	private void emptyList(View List, int sectionId) {
		
		String message = new String();
		
		switch(sectionId){
			case Constants.CATEGORY:
				message = this.context.getText(R.string.tv_no_categories).toString();
				break;
			case Constants.LOCATION:
				message = this.context.getText(R.string.tv_no_locations).toString();
				break;
			case Constants.ITEM:
				message = this.context.getText(R.string.tv_no_items).toString();
				break;
		}
		
		TextView tv = new TextView(this.context);
		tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		tv.setText(message);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(15);
		tv.setPadding(10, 0, 10, 0);
		tv.setTypeface(null, Typeface.BOLD);
		tv.setId(sectionId);
		ViewGroup parent = (ViewGroup) List.getParent();
		parent.addView(tv);
		List.setVisibility(View.GONE);
	}
	
	private void notEmptyList(View List, int sectionId) {
		try{
			ViewGroup parent = (ViewGroup) List.getParent();
			TextView tv = (TextView) parent.findViewById(sectionId);
			tv.setVisibility(View.GONE);
			List.setVisibility(View.VISIBLE);
		}catch(Exception e){
			
		}
	}
	
	private boolean checkListContents(ArrayList<?> array, View list, int sectionId) {
		boolean isNotEmpty;
		
		if(array.size() == 0){
			this.emptyList(list, sectionId);
			isNotEmpty = false;
		}else{
			this.notEmptyList(list, sectionId);
			isNotEmpty = true;
		}
		
		return isNotEmpty;
	}
	
}
