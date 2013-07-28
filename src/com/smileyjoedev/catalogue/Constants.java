package com.smileyjoedev.catalogue;

import java.io.File;

import android.os.Environment;

public class Constants {
	
	public static final String DB_NAME = "catalogue";
	public static final int DB_VERSION = 1;
	public static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + File.separator + "com.joedev" + File.separator + "Catalogue" + File.separator;
	
	public static final int ACTIVITY_CATEGORY_LIST = 1;
	public static final int ACTIVITY_CATEGORY_NEW = 2;
	public static final int ACTIVITY_ITEM_LIST = 3;
	public static final int ACTIVITY_ITEM_ADD = 4;
	public static final int ACTIVITY_ITEM_EDIT = 5;
	public static final int ACTIVITY_ITEM_VIEW = 6;
	public static final int ACTIVITY_LOCATION_LIST = 7;
	public static final int ACTIVITY_LOCATION_NEW = 8;
	public static final int ACTIVITY_CATEGORY_SELECTOR = 9;
	public static final int ACTIVITY_LOCATION_SELECTOR = 10;
	public static final int ACTIVITY_TAKE_PHOTO = 11;
	public static final int ACTIVITY_CATEGORY_EDIT = 12;
	public static final int ACTIVITY_LOCATION_EDIT = 13;
	public static final int ACTIVITY_CATEGORY_POPUP_DELETE = 14;
	public static final int ACTIVITY_LOCATION_POPUP_DELETE = 15;
	public static final int ACTIVITY_ITEM_POPUP_DELETE = 16;
	public static final int ACTIVITY_ITEM_NEW = 17;
	
	public static final int CATEGORY = 1;
	public static final int LOCATION = 2;
	public static final int ITEM = 3;
	
	public static final int CONTEXT_CATEGORY_DELETE = 1;
	public static final int CONTEXT_CATEGORY_EDIT = 2;
	public static final int CONTEXT_LOCATION_DELETE = 3;
	public static final int CONTEXT_LOCATION_EDIT = 4;
	public static final int CONTEXT_ITEM_DELETE = 5;
	public static final int CONTEXT_ITEM_EDIT = 6;
	
	public static final int TAB_CATEGORY = 1;
	public static final int TAB_ITEM = 2;
	public static final int TAB_LOCATION = 3;
	
	public static final String EXTRA_LOCATION_ID = "location_id";
	public static final String EXTRA_CATEGORY_ID = "category_id";
	public static final String EXTRA_ITEM_ID = "item_id";
	public static final String EXTRA_IS_EDIT = "is_edit";
	public static final String EXTRA_SEARCH_TERM = "search_term";
	public static final String EXTRA_ON_CATEGORY = "on_category";
	public static final String EXTRA_ON_LOCATION = "on_location";
	public static final String EXTRA_ON_SEARCH = "on_search";
	public static final String EXTRA_LOCATION_ID_TRAIL = "location_id_trail";
	public static final String EXTRA_CATEGORY_ID_TRAIL = "category_id_trail";
	public static final String EXTRA_ITEM = "item";
	
}
