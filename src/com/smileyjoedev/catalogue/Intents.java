package com.smileyjoedev.catalogue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class Intents {

	public static Intent categoryList(Context context){
		Intent intent = new Intent(context, CategoryList.class);
		return intent;
	}
	
	public static Intent locationList(Context context){
		Intent intent = new Intent(context, LocationList.class);
		return intent;
	}
	
	public static Intent itemList(Context context){
		Intent intent = new Intent(context, ItemAdd.class);
		return intent;
	}
	
	public static Intent categoryNew(Context context, long categoryId){
		Intent intent = new Intent(context, CategoryNew.class);
		
		Bundle extras = new Bundle();
		extras.putLong(Constants.EXTRA_CATEGORY_ID, categoryId);
		extras.putBoolean(Constants.EXTRA_IS_EDIT, false);
		intent.putExtras(extras);
		
		return intent;
	}
	
	public static Intent categoryEdit(Context context, long categoryId){
		Intent intent = new Intent(context, CategoryNew.class);
		
		Bundle extras = new Bundle();
		extras.putLong(Constants.EXTRA_CATEGORY_ID, categoryId);
		extras.putBoolean(Constants.EXTRA_IS_EDIT, true);
		intent.putExtras(extras);
		
		return intent;
	}
	
	public static Intent itemAdd(Context context){
		Intent intent = new Intent(context, ItemAdd.class);
		
		return intent;
	}
	
	public static Intent itemEdit(Context context, long itemId){
		Intent intent = new Intent(context, ItemAdd.class);
		
		Bundle extras = new Bundle();
		extras.putLong(Constants.EXTRA_ITEM_ID, itemId);
		intent.putExtras(extras);
		
		return intent;
	}
	
	public static Intent categorySelector(Context context, long catId){
		Intent intent = new Intent(context, CategorySelector.class);
		
		Bundle extras = new Bundle();
		extras.putLong("cat_id", catId);
		intent.putExtras(extras);
		
		return intent;
	}
	
	public static Intent locationSelector(Context context, long locId){
		Intent intent = new Intent(context, LocationSelector.class);
		
		Bundle extras = new Bundle();
		extras.putLong("loc_id", locId);
		intent.putExtras(extras);
		
		return intent;
	}
	
	public static Intent locationNew(Context context, long locationId){
		Intent intent = new Intent(context, LocationNew.class);
		
		Bundle extras = new Bundle();
		extras.putLong(Constants.EXTRA_LOCATION_ID, locationId);
		extras.putBoolean(Constants.EXTRA_IS_EDIT, false);
		intent.putExtras(extras);
		
		return intent;
	}
	
	public static Intent locationEdit(Context context, long locId){
		Intent intent = new Intent(context, LocationAdd.class);
		
		Bundle extras = new Bundle();
		extras.putLong("loc_id", locId);
		intent.putExtras(extras);
		
		return intent;
	}

	public static Intent itemView(Context context, long itemId){
		Intent intent = new Intent(context, ItemView.class);
		
		Bundle extras = new Bundle();
		extras.putLong(Constants.EXTRA_ITEM_ID, itemId);
		intent.putExtras(extras);
		
		return intent;
	}
	
	public static void filterCategoryChanged(Context context, BroadcastReceiver receiver){
		IntentFilter filter = new IntentFilter();
        filter.addAction(Broadcast.CATEGORY_CHANGED);
        context.registerReceiver(receiver, filter);
	}
	
	public static void filterLocationChanged(Context context, BroadcastReceiver receiver){
		IntentFilter filter = new IntentFilter();
        filter.addAction(Broadcast.LOCATION_CHANGED);
        context.registerReceiver(receiver, filter);
	}
	
}