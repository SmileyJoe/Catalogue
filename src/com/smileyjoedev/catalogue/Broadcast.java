package com.smileyjoedev.catalogue;

import android.content.Context;
import android.content.Intent;

public class Broadcast {

	public static final String CATEGORY_CHANGED = "com.smileyjoedev.catalogue.CATEGORY_CHANGED";
	public static final String LOCATION_CHANGED = "com.smileyjoedev.catalogue.LOCATION_CHANGED";
	
	public static void categoryChanged(Context context, long categoryId){
		Intent broadcast = new Intent();
        broadcast.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId);
        broadcast.setAction(CATEGORY_CHANGED);
        context.sendBroadcast(broadcast);
	}
	
	public static void locationChanged(Context context, long locationId){
		Intent broadcast = new Intent();
        broadcast.putExtra(Constants.EXTRA_LOCATION_ID, locationId);
        broadcast.setAction(LOCATION_CHANGED);
        context.sendBroadcast(broadcast);
	}
	
}
