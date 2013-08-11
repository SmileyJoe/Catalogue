package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.Notify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbLocationAdapter {
	
	/*****************************************
	 * VARIABLES
	 ****************************************/
	
	private Context context;
	private SQLiteDatabase db;
	private DbHelper dbHelper;
	private Cursor cursor;
	private int idCol;
	private int titleCol;
	private int parentIdCol;
	private DbNfcAdapter nfcAdapter;
	
	/*****************************************
	 * CONSTRUCTOR
	 ****************************************/
	
	public DbLocationAdapter(Context context) {
		this.context = context;
		this.dbHelper = new DbHelper(context);
		this.db = dbHelper.getWritableDatabase();
		this.nfcAdapter = new DbNfcAdapter(context);
	}
	
	/******************************************
	 * GET
	 *****************************************/
	
	public Location getDetails(long locationId){
		this.setCursor("WHERE _id = '" + locationId + "' ");
		return this.sortCursor();
	}
	
	public ArrayList<Location> get(long locationId){
		this.setCursor("WHERE location_parent_id = '" + locationId + "'");
		return this.sortCursorArrayList();
	}
	
	public ArrayList<Location> getBreadCrumb(long lastLocationId){
		ArrayList<Location> breadCrumb = new ArrayList<Location>();
		
		if(lastLocationId > 0){
			Location location = new Location();
			do{
				location = this.getDetails(lastLocationId);
				breadCrumb.add(location);
				lastLocationId = location.getParentId();
			} while (lastLocationId > 0);			
		}
		
		return breadCrumb;
	}
	
	public ArrayList<LocationQuantity> getForItem(long itemId){
		ArrayList<LocationQuantity> locQuans = new ArrayList<LocationQuantity>();
		Location location = new Location();
		LocationQuantity locQuan = new LocationQuantity();
		
		Cursor cur = this.db.rawQuery(
				"SELECT loc._id, loc.location_title, loc.location_parent_id, relItem.item_quantity "
				+ "FROM location loc "
				+ "JOIN item_rel_location relItem ON relItem.location_id = loc._id "
				+ "WHERE relItem.item_id = '" + itemId + "'"
				+ "ORDER BY location_title ASC", null);
		
		if(cur != null){
			cur.moveToFirst();
			if(cur.getCount() > 0){
				do{
					locQuan = new LocationQuantity();
					location = new Location();
					
					location.setId(cur.getLong(cur.getColumnIndex("_id")));
					location.setTitle(cur.getString(cur.getColumnIndex("location_title")));
					location.setParentId(cur.getLong(cur.getColumnIndex("location_parent_id")));
					
					locQuan.setLocation(location);
					
					locQuan.setQuantity(cur.getLong(cur.getColumnIndex("item_quantity")));
					locQuans.add(locQuan);
				}while(cur.moveToNext());
				
			}
		}
		
		return locQuans;
	}
	
	public long getNumberChildren(long locId){
		long num = 0;
		
		Cursor cur = this.db.rawQuery(
				"SELECT count(location_parent_id) as count "
				+ "FROM location " 
				+ "WHERE location_parent_id = '" + locId + "'", null);
		
		if(cur != null){
			cur.moveToFirst();
			if(cur.getCount() > 0){
				num = cur.getInt(cur.getColumnIndex("count"));
			}
		}
		
		return num;
	}
	
	public long getNumberItems(long locId){
		long num = 0;
		
		Cursor cur = this.db.rawQuery(
				"SELECT count(item_id) as count "
				+ "FROM item_rel_location " 
				+ "WHERE location_id = '" + locId + "'", null);
		
		if(cur != null){
			cur.moveToFirst();
			if(cur.getCount() > 0){
				num = cur.getInt(cur.getColumnIndex("count"));
			}
		}
		
		return num;
	}
	
	public ArrayList<Location> search(String searchQuery){
		this.setCursor("WHERE location_title LIKE '%" + searchQuery + "%'");
		return this.sortCursorArrayList();
	}
	
	private long[] getChildrenIds(long locId){
		long[] children;
		int count = 0;
		Cursor cur = this.db.rawQuery(
				"SELECT _id "
				+ "FROM location " 
				+ "WHERE location_parent_id = '" + locId + "'", null);
		
		children = new long[cur.getCount()];
		
		if(cur != null){
			cur.moveToFirst();
			if(cur.getCount() > 0){
				do{
					children[count] = cur.getLong(cur.getColumnIndex("_id"));
					count++;
				}while(cur.moveToNext());
			}
		}
		
		return children;
	}
	
	/******************************************
	 * SAVE
	 *****************************************/
	
	public long save(Location location) {
		long dbId = 0;
		if(location.getId() == 0){
			ContentValues values = createContentValues(location);
			dbId = db.insert("location", null, values);
			if(dbId > 0){
				location.setId(dbId);
				this.saveNfc(location);
				Notify.toast(this.context, R.string.toast_location_saved, location.getTitle());
			} else {
				Notify.toast(this.context, R.string.toast_location_saved_error, location.getTitle());
			}
		} else {
			this.update(location);
			dbId = location.getId();
		}
		
		return dbId;
	}
	
	public long saveItemRel(long locationId, long itemId, long quantity){
		long dbId = 0;
		ContentValues values = createItemRelContentValues(locationId, itemId, quantity);
		dbId = db.insert("item_rel_location", null, values);
		
		return dbId;
	}
	
	private void saveNfc(Location location){
		if(location.hasNfc()){
			location.getNfc().setRelId(location.getId());
			location.getNfc().setRelTypeId(Constants.LOCATION);
			this.nfcAdapter.save(location.getNfc());
		}
	}
	
	/******************************************
	 * UPDATE
	 *****************************************/
	
	public void update(Location location) {
		ContentValues values = createContentValues(location);
		db.update("location", values, " _id = '" + location.getId() + "' ", null);
		this.nfcAdapter.delete(location.getNfc());
		
		this.saveNfc(location);
		Notify.toast(this.context, R.string.toast_location_updated, location.getTitle());
	}
	
	/******************************************
	 * DELETE
	 *****************************************/
	
	public void delete(Location location){
		this.deleteChildren(location.getId());
		db.delete("location", " _id='" + location.getId() + "' ", null);
		db.delete("item_rel_location", " location_id='" + location.getId() + "' ", null);
		this.deleteChildren(location.getId());
		this.nfcAdapter.delete(location.getNfc());
		if(!location.getTitle().equals("")){
			Notify.toast(this.context, R.string.toast_location_deleted, location.getTitle());
		}
	}
	
	public void deleteItemRel(long itemId){
		db.delete("item_rel_location", " item_id='" + itemId + "' ", null);
	}
	
	public void deleteItemRel(long itemId, long locationId){
		db.delete("item_rel_location", " item_id='" + itemId + "' AND location_id='" + locationId + "' ", null);
	}
	
	public void deleteChildren(long locId){
		long[] childrenIds = this.getChildrenIds(locId);
		
		if(childrenIds.length > 0){
			for(int i = 0; i < childrenIds.length; i++){
				if(childrenIds[i] > 0){
					Location loc = new Location();
					loc.setId(childrenIds[i]);
					this.delete(loc);
				}
			}
		}
		
	}
	
	/******************************************
	 * GENERAL
	 *****************************************/
	
	private void setCursor(String where){
		Debug.d("WHERE ", where);
		this.cursor = this.db.rawQuery(
				"SELECT _id, location_title, location_parent_id "
				+ "FROM location " 
				+ " " + where + " "
				+ "ORDER BY location_title ASC", null);
	}
	
	private void setColoumns(){
		this.idCol = this.cursor.getColumnIndex("_id");
		this.titleCol = this.cursor.getColumnIndex("location_title");
		this.parentIdCol = this.cursor.getColumnIndex("location_parent_id");
	}
	
	private Location getLocationData(){
		Location location = new Location();
		
		location.setId(this.cursor.getLong(this.idCol));
		location.setTitle(this.cursor.getString(this.titleCol));
		location.setParentId(this.cursor.getLong(this.parentIdCol));
		location.setNumChildren(this.getNumberChildren(location.getId()));
		location.setNumItems(this.getNumberItems(location.getId()));
		location.setNfc(this.nfcAdapter.getDetailsByRel(location.getId(), Constants.LOCATION));
		
		return location;
	}
	
	private ArrayList<Location> sortCursorArrayList(){
		ArrayList<Location> categories = new ArrayList<Location>();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				do{
					categories.add(this.getLocationData());
				}while(this.cursor.moveToNext());
			}
		}
		
		return categories;
	}
	
	private Location sortCursor(){
		Location location = new Location();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				location = this.getLocationData();
			}
		}
		
		return location;
	}
	
	private ContentValues createContentValues(Location location) {
		ContentValues values = new ContentValues();
		
		values.put("location_title", location.getTitle().trim());
		values.put("location_parent_id", location.getParentId());
		
		return values;
	}
	
	private ContentValues createItemRelContentValues(long locationId, long itemId, long quantity){
		ContentValues values = new ContentValues();
		
		values.put("location_id", locationId);
		values.put("item_id", itemId);
		values.put("item_quantity", quantity);
		
		return values;
	}
	
	/******************************************
	 * CHECK
	 *****************************************/
	

}
