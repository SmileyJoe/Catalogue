package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.Notify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbCategoryAdapter {
	
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
	private DbBarcodeAdapter barcodeAdapter;
	
	/*****************************************
	 * CONSTRUCTOR
	 ****************************************/
	
	public DbCategoryAdapter(Context context) {
		this.context = context;
		this.dbHelper = new DbHelper(context);
		this.db = dbHelper.getWritableDatabase();
		this.nfcAdapter = new DbNfcAdapter(context);
		this.barcodeAdapter = new DbBarcodeAdapter(context);
	}
	
	/******************************************
	 * GET
	 *****************************************/
	
	public Category getDetails(long categoryId){
		this.setCursor("WHERE _id = '" + categoryId + "' ");
		return this.sortCursor();
	}
	
	public ArrayList<Category> get(long categoryId){
		this.setCursor("WHERE category_parent_id = '" + categoryId + "'");
		return this.sortCursorArrayList();
	}
	
	public ArrayList<Category> getBreadCrumb(long lastCategoryId){
		ArrayList<Category> breadCrumb = new ArrayList<Category>();
		
		if(lastCategoryId > 0){
			Category category = new Category();
			do{
				category = this.getDetails(lastCategoryId);
				breadCrumb.add(category);
				lastCategoryId = category.getParentId();
			} while (lastCategoryId > 0);			
		}
		
		return breadCrumb;
	}
	
	public long getNumberChildren(long catId){
		long num = 0;
		
		Cursor cur = this.db.rawQuery(
				"SELECT count(category_parent_id) as count "
				+ "FROM category " 
				+ "WHERE category_parent_id = '" + catId + "'", null);
		
		if(cur != null){
			cur.moveToFirst();
			if(cur.getCount() > 0){
				num = cur.getInt(cur.getColumnIndex("count"));
			}
		}
		
		return num;
	}
	
	public ArrayList<Category> getForItem(long itemId){
		ArrayList<Category> categories = new ArrayList<Category>();
		Category category = new Category();
		
		Cursor cur = this.db.rawQuery(
				"SELECT cat._id, cat.category_title, cat.category_parent_id "
				+ "FROM category cat "
				+ "JOIN item_rel_category relItem ON relItem.category_id = cat._id "
				+ "WHERE relItem.item_id = '" + itemId + "'"
				+ "ORDER BY cat.category_title ASC", null);
		
		if(cur != null){
			cur.moveToFirst();
			if(cur.getCount() > 0){
				do{
					category = new Category();
					
					category.setId(cur.getLong(cur.getColumnIndex("_id")));
					category.setTitle(cur.getString(cur.getColumnIndex("category_title")));
					category.setParentId(cur.getLong(cur.getColumnIndex("category_parent_id")));
					
					categories.add(category);
				}while(cur.moveToNext());
				
			}
		}
		
		return categories;
	}
	
	public long getNumberItems(long catId){
		long num = 0;
		
		Cursor cur = this.db.rawQuery(
				"SELECT count(item_id) as count "
				+ "FROM item_rel_category " 
				+ "WHERE category_id = '" + catId + "'", null);
		
		if(cur != null){
			cur.moveToFirst();
			if(cur.getCount() > 0){
				num = cur.getInt(cur.getColumnIndex("count"));
			}
		}
		
		return num;
	}
	
	public ArrayList<Category> search(String searchQuery){
		this.setCursor("WHERE category_title LIKE '%" + searchQuery + "%'");
		return this.sortCursorArrayList();
	}
	
	private long[] getChildrenIds(long catId){
		long[] children;
		int count = 0;
		Cursor cur = this.db.rawQuery(
				"SELECT _id "
				+ "FROM category " 
				+ "WHERE category_parent_id = '" + catId + "'", null);
		
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
	
	public long save(Category category) {
		long dbId = 0;
		if(category.getId() == 0){
			ContentValues values = createContentValues(category);
			dbId = db.insert("category", null, values);
			if(dbId > 0){
				category.setId(dbId);
				this.saveNfc(category);
				this.saveBarcode(category);
				Notify.toast(this.context, R.string.toast_category_saved, category.getTitle());
			} else {
				Notify.toast(this.context, R.string.toast_category_saved_error, category.getTitle());
			}
		} else {
			this.update(category);
			dbId = category.getId();
		}
		
		return dbId;
	}
	
	public long saveItemRel(long categoryId, long itemId){
		long dbId = 0;
		ContentValues values = createItemRelContentValues(categoryId, itemId);
		dbId = db.insert("item_rel_category", null, values);
		
		return dbId;
	}
	
	private void saveNfc(Category category){
		if(category.hasNfc()){
			category.getNfc().setRelId(category.getId());
			category.getNfc().setRelTypeId(Constants.CATEGORY);
			this.nfcAdapter.save(category.getNfc());
		}
	}
	
	private void saveBarcode(Category category){
		if(category.hasBarcode()){
			category.getBarcode().setRelId(category.getId());
			category.getBarcode().setRelTypeId(Constants.CATEGORY);
			this.barcodeAdapter.save(category.getBarcode());
		}
	}
	
	/******************************************
	 * UPDATE
	 *****************************************/
	
	public void update(Category category) {
		ContentValues values = createContentValues(category);
		db.update("category", values, " _id = '" + category.getId() + "' ", null);
		this.nfcAdapter.delete(category.getNfc());
		this.barcodeAdapter.delete(category.getBarcode());
		
		this.saveNfc(category);
		this.saveBarcode(category);
		Notify.toast(this.context, R.string.toast_category_updated, category.getTitle());
	}
	
	/******************************************
	 * DELETE
	 *****************************************/
	
	public void delete(Category category){
		this.deleteChildren(category.getId());
		db.delete("category", " _id='" + category.getId() + "' ", null);
		db.delete("item_rel_category", " category_id='" + category.getId() + "' ", null);
		this.deleteChildren(category.getId());
		this.nfcAdapter.delete(category.getNfc());
		this.barcodeAdapter.delete(category.getBarcode());
		if(!category.getTitle().equals("")){
			Notify.toast(this.context, R.string.toast_category_deleted, category.getTitle());
		}
	}
	
	public void deleteChildren(long catId){
		long[] childrenIds = this.getChildrenIds(catId);
		
		if(childrenIds.length > 0){
			for(int i = 0; i < childrenIds.length; i++){
				if(childrenIds[i] > 0){
					Category cat = new Category();
					cat.setId(childrenIds[i]);
					this.delete(cat);
				}
			}
		}
		
	}
	
	public void deleteItemRel(long itemId){
		db.delete("item_rel_category", " item_id='" + itemId + "' ", null);
	}
	
	public void deleteItemRel(long itemId, long categoryId){
		Debug.d("ItemId", itemId, "category id", categoryId);
		db.delete("item_rel_category", " item_id='" + itemId + "' AND category_id='" + categoryId + "' ", null);
	}
	
	/******************************************
	 * GENERAL
	 *****************************************/
	
	private void setCursor(String where){
		Debug.d("WHERE ", where);
		this.cursor = this.db.rawQuery(
				"SELECT _id, category_title, category_parent_id "
				+ "FROM category " 
				+ " " + where + " "
				+ "ORDER BY category_title ASC", null);
	}
	
	private void setColoumns(){
		this.idCol = this.cursor.getColumnIndex("_id");
		this.titleCol = this.cursor.getColumnIndex("category_title");
		this.parentIdCol = this.cursor.getColumnIndex("category_parent_id");
	}
	
	private Category getCategoryData(){
		Category category = new Category();
		
		category.setId(this.cursor.getLong(this.idCol));
		category.setTitle(this.cursor.getString(this.titleCol));
		category.setParentId(this.cursor.getLong(this.parentIdCol));
		category.setNumChildren(this.getNumberChildren(category.getId()));
		category.setNumItems(this.getNumberItems(category.getId()));
		category.setNfc(this.nfcAdapter.getDetailsByRel(category.getId(), Constants.CATEGORY));
		category.setBarcode(this.barcodeAdapter.getDetailsByRel(category.getId(), Constants.CATEGORY));
		
		return category;
	}
	
	private ArrayList<Category> sortCursorArrayList(){
		ArrayList<Category> categories = new ArrayList<Category>();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				do{
					categories.add(this.getCategoryData());
				}while(this.cursor.moveToNext());
			}
		}
		
		return categories;
	}
	
	private Category sortCursor(){
		Category category = new Category();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				category = this.getCategoryData();
			}
		}
		
		return category;
	}
	
	private ContentValues createContentValues(Category category) {
		ContentValues values = new ContentValues();
		
		values.put("category_title", category.getTitle().trim());
		values.put("category_parent_id", category.getParentId());
		
		return values;
	}
	
	private ContentValues createItemRelContentValues(long categoryId, long itemId){
		ContentValues values = new ContentValues();
		
		values.put("category_id", categoryId);
		values.put("item_id", itemId);
		
		return values;
	}
	
	/******************************************
	 * CHECK
	 *****************************************/
	

}
