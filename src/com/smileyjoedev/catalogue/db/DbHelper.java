package com.smileyjoedev.catalogue.db;

import com.smileyjoedev.catalogue.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	public DbHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
	}
	
	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		Db.onCreate(database);
	}
	
	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Db.onUpgrade(database, oldVersion, newVersion);
	}
}
