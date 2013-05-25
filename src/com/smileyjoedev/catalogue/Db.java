package com.smileyjoedev.catalogue;

import com.smileyjoedev.genLibrary.Debug;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class Db {
	private static final String CREATE_ITEM = "CREATE TABLE item (_id integer primary key autoincrement, item_title text not null, item_description text not null, item_pdt_create long not null, item_pdt_update long not null);";
	private static final String CREATE_LOCATION = "CREATE TABLE location (_id integer primary key autoincrement, location_title text not null, location_parent_id long not null);";
	private static final String CREATE_CATEGORY = "CREATE TABLE category (_id integer primary key autoincrement, category_title text not null, category_parent_id long not null);";
	private static final String CREATE_ITEM_REL_CATEGORY = "CREATE TABLE item_rel_category(_id integer primary key autoincrement, category_id long not null, item_id long not null);";
	private static final String CREATE_ITEM_REL_LOCATION = "CREATE TABLE item_rel_location(_id integer primary key autoincrement, item_id long not null, location_id long not null, item_quantity long not null);";
	
 	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_ITEM);
		database.execSQL(CREATE_LOCATION);
		database.execSQL(CREATE_CATEGORY);
		database.execSQL(CREATE_ITEM_REL_CATEGORY);
		database.execSQL(CREATE_ITEM_REL_LOCATION);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
	}
	
}
