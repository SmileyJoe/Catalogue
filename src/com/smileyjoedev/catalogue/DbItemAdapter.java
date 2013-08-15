package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import com.smileyjoedev.genLibrary.Camera;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.Notify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbItemAdapter {
	
	/*****************************************
	 * VARIABLES
	 ****************************************/
	
	private Context context;
	private SQLiteDatabase db;
	private DbHelper dbHelper;
	private Cursor cursor;
	private int idCol;
	private int titleCol;
	private int descriptionCol;
	private int pdtCreateCol;
	private int pdtUpdateCol;
	private DbLocationAdapter locationAdapter;
	private DbCategoryAdapter categoryAdapter;
	private DbNfcAdapter nfcAdapter;
	private DbBarcodeAdapter barcodeAdapter;
	
	/*****************************************
	 * CONSTRUCTOR
	 ****************************************/
	
	public DbItemAdapter(Context context) {
		this.context = context;
		this.dbHelper = new DbHelper(context);
		this.db = dbHelper.getWritableDatabase();
		this.locationAdapter = new DbLocationAdapter(context);
		this.categoryAdapter = new DbCategoryAdapter(context);
		this.nfcAdapter = new DbNfcAdapter(context);
		this.barcodeAdapter = new DbBarcodeAdapter(context);
	}
	
	/******************************************
	 * GET
	 *****************************************/
	
	public Item getDetails(long itemId){
		this.setCursor("WHERE _id = '" + itemId + "' ");
		return this.sortCursor();
	}
	
	public ArrayList<Item> get(){
		this.setCursor("");
		return this.sortCursorArrayList();
	}
	
	public ArrayList<Item> getByCategory(long catId){
		if(catId == 0){
			this.setCursorRelCatUncategorized();
		} else {
			this.setCursorRelCat("WHERE relCat.category_id = '" + catId + "'");
		}
		
		return this.sortCursorArrayList();
	}
	
	public ArrayList<Item> getByLocation(long locId){
		if(locId == 0){
			this.setCursorRelLocNoLocation();
		} else {
			this.setCursorRelLoc("WHERE relLoc.location_id = '" + locId + "'");
		}
		
		return this.sortCursorArrayList();
	}
	
	public ArrayList<Item> search(String searchQuery){
		this.setCursor("WHERE item_title LIKE '%" + searchQuery + "%' OR item_description LIKE '%" + searchQuery + "%'");
		return this.sortCursorArrayList();
	}
	
	/******************************************
	 * SAVE
	 *****************************************/
	
	public long save(Item item) {
		Camera camera = new Camera(Constants.PHOTO_PATH);
		long dbId = 0;
		ContentValues values = createContentValues(item);
		dbId = db.insert("item", null, values);
		camera.renamePhoto(Long.toString(dbId));
		if(dbId > 0){
			item.setId(dbId);
			for(int i = 0; i < item.getLocations().size(); i++){
				if(item.getLocations().get(i).getLocation().getId() != 0){
					this.locationAdapter.saveItemRel(item.getLocations().get(i).getLocation().getId(), dbId, item.getLocations().get(i).getQuantity());
				}
			}
			for(int i = 0; i < item.getCategories().size(); i++){
				if(item.getCategories().get(i).getId() != 0){
					this.categoryAdapter.saveItemRel(item.getCategories().get(i).getId(), dbId);
				}
			}
			
			this.saveNfc(item);
			this.saveBarcode(item);
			Notify.toast(this.context, R.string.toast_item_saved, item.getTitle());
		} else {
			Notify.toast(this.context, R.string.toast_item_saved_error, item.getTitle());
		}
		
		return dbId;
	}
	
	private void saveNfc(Item item){
		if(item.hasNfc()){
			item.getNfc().setRelId(item.getId());
			item.getNfc().setRelTypeId(Constants.ITEM);
			this.nfcAdapter.save(item.getNfc());
		}
	}
	
	private void saveBarcode(Item item){
		if(item.hasBarcode()){
			item.getBarcode().setRelId(item.getId());
			item.getBarcode().setRelTypeId(Constants.ITEM);
			this.barcodeAdapter.save(item.getBarcode());
		}
	}
	
	/******************************************
	 * UPDATE
	 *****************************************/
	
	public void update(Item item) {
		Camera camera = new Camera(Constants.PHOTO_PATH);
		ContentValues values = createContentValues(item);
		db.update("item", values, " _id = '" + item.getId() + "' ", null);
		
		if(camera.isTempExists()){
			camera.setName(Long.toString(item.getId()));
			camera.deletePhoto();
			camera.renamePhoto(Long.toString(item.getId()));
		}
		
		this.locationAdapter.deleteItemRel(item.getId());
		this.categoryAdapter.deleteItemRel(item.getId());
		this.nfcAdapter.delete(item.getNfc());
		this.barcodeAdapter.delete(item.getBarcode());
		
		for(int i = 0; i < item.getLocations().size(); i++){
			this.locationAdapter.saveItemRel(item.getLocations().get(i).getLocation().getId(), item.getId(), item.getLocations().get(i).getQuantity());
		}
		for(int i = 0; i < item.getCategories().size(); i++){
			this.categoryAdapter.saveItemRel(item.getCategories().get(i).getId(), item.getId());
		}
		
		this.saveNfc(item);
		this.saveBarcode(item);
		
		Notify.toast(this.context, R.string.toast_item_updated, item.getTitle());
	}
	
	/******************************************
	 * DELETE
	 *****************************************/
	
	public void delete(Item item){
		Camera camera = new Camera(Constants.PHOTO_PATH);
		db.delete("item", " _id='" + item.getId() + "' ", null);
		camera.setName(Long.toString(item.getId()));
		camera.deletePhoto();
		this.locationAdapter.deleteItemRel(item.getId());
		this.categoryAdapter.deleteItemRel(item.getId());
		this.nfcAdapter.delete(item.getNfc());
		this.barcodeAdapter.delete(item.getBarcode());
		Notify.toast(this.context, R.string.toast_item_deleted, item.getTitle());
	}
	
	public void deleteRelCategory(long itemId, long categoryId){
		this.categoryAdapter.deleteItemRel(itemId, categoryId);
	}
	
	public void deleteRelLocation(long itemId, long locationId){
		this.locationAdapter.deleteItemRel(itemId, locationId);
	}
	
	/******************************************
	 * GENERAL
	 *****************************************/
	
	private void setCursor(String where){
		Debug.d("WHERE ", where);
		this.cursor = this.db.rawQuery(
				"SELECT _id, item_title, item_description, item_pdt_create, item_pdt_update "
				+ "FROM item " 
				+ " " + where + " "
				+ "ORDER BY item_title ASC", null);
	}
	
	private void setCursorRelLoc(String where){
		Debug.d("WHERE ", where);
		this.cursor = this.db.rawQuery(
				"SELECT it._id, it.item_title, it.item_description, it.item_pdt_create, it.item_pdt_update "
				+ "FROM item it " 
				+ "JOIN item_rel_location relLoc ON relLoc.item_id = it._id"
				+ " " + where + " "
				+ "ORDER BY item_title ASC", null);
	}
	
	private void setCursorRelLocNoLocation(){
		this.cursor = this.db.rawQuery(
				"SELECT it._id, it.item_title, it.item_description, it.item_pdt_create, it.item_pdt_update "
				+ "FROM item it " 
				+ "LEFT JOIN item_rel_location relLoc ON relLoc.item_id = it._id "
				+ "WHERE relLoc.location_id IS NULL "
				+ "ORDER BY item_title ASC", null);
	}
	
	private void setCursorRelCat(String where){
		Debug.d("WHERE ", where);
		this.cursor = this.db.rawQuery(
				"SELECT it._id, it.item_title, it.item_description, it.item_pdt_create, it.item_pdt_update "
				+ "FROM item it " 
				+ "JOIN item_rel_category relCat ON relCat.item_id = it._id"
				+ " " + where + " "
				+ "ORDER BY item_title ASC", null);
	}
	
	private void setCursorRelCatUncategorized(){
		this.cursor = this.db.rawQuery(
				"SELECT it._id, it.item_title, it.item_description, it.item_pdt_create, it.item_pdt_update "
				+ "FROM item it " 
				+ "LEFT JOIN item_rel_category relCat ON relCat.item_id = it._id "
				+ "WHERE relCat.category_id IS NULL "
				+ "ORDER BY item_title ASC", null);
	}
	
	private void setColoumns(){
		this.idCol = this.cursor.getColumnIndex("_id");
		this.titleCol = this.cursor.getColumnIndex("item_title");
		this.descriptionCol = this.cursor.getColumnIndex("item_description");
		this.pdtCreateCol = this.cursor.getColumnIndex("item_pdt_create");
		this.pdtUpdateCol = this.cursor.getColumnIndex("item_pdt_update");
	}
	
	private Item getItemData(){
		Item item = new Item();
		
		item.setId(this.cursor.getLong(this.idCol));
		item.setTitle(this.cursor.getString(this.titleCol));
		item.setDesc(this.cursor.getString(this.descriptionCol));
		item.setPdtCreate(this.cursor.getLong(this.pdtCreateCol));
		item.setPdtUpdate(this.cursor.getLong(this.pdtUpdateCol));
		item.setLocations(this.locationAdapter.getForItem(item.getId()));
		item.setCategories(this.categoryAdapter.getForItem(item.getId()));
		item.setNfc(this.nfcAdapter.getDetailsByRel(item.getId(), Constants.ITEM));
		item.setBarcode(this.barcodeAdapter.getDetailsByRel(item.getId(), Constants.ITEM));
		
		return item;
	}
	
	private ArrayList<Item> sortCursorArrayList(){
		ArrayList<Item> items = new ArrayList<Item>();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				do{
					items.add(this.getItemData());
				}while(this.cursor.moveToNext());
			}
		}
		
		return items;
	}
	
	private Item sortCursor(){
		Item item = new Item();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				item = this.getItemData();
			}
		}
		
		return item;
	}
	
	private ContentValues createContentValues(Item item) {
		ContentValues values = new ContentValues();
		
		values.put("item_title", item.getTitle().trim());
		values.put("item_description", item.getDesc().trim());
		values.put("item_pdt_create", item.getPdtCreate());
		values.put("item_pdt_update", item.getPdtUpdate());
		
		return values;
	}
	
	/******************************************
	 * CHECK
	 *****************************************/
	

}
