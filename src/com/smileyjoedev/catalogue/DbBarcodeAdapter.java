package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import com.smileyjoedev.genLibrary.Camera;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.Notify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbBarcodeAdapter {
	
	/*****************************************
	 * VARIABLES
	 ****************************************/
	
	private Context context;
	private SQLiteDatabase db;
	private DbHelper dbHelper;
	private Cursor cursor;
	private int idCol;
	private int barcodeIdCol;
	private int relIdCol;
	private int relIdTypeCol;
	
	/*****************************************
	 * CONSTRUCTOR
	 ****************************************/
	
	public DbBarcodeAdapter(Context context) {
		this.context = context;
		this.dbHelper = new DbHelper(context);
		this.db = dbHelper.getWritableDatabase();
	}
	
	/******************************************
	 * GET
	 *****************************************/
	
	public Barcode getDetails(long id){
		this.setCursor("WHERE _id = '" + id + "' ");
		return this.sortCursor();
	}
	
	public Barcode getDetailsByBarcodeId(String barcodeId){
		this.setCursor("WHERE barcode_id = '" + barcodeId + "' ");
		return this.sortCursor();
	}
	
	public Barcode getDetailsByRel(long relId, int relTypeId){
		this.setCursor("WHERE rel_id = '" + relId + "' AND rel_id_type = '" + relTypeId + "' ");
		return this.sortCursor();
	}
	
	public ArrayList<Barcode> get(){
		this.setCursor("");
		return this.sortCursorArrayList();
	}
	
	/******************************************
	 * SAVE
	 *****************************************/
	
	public long save(Barcode barcode) {
		long dbId = 0;
		if(barcode.getId() > 0){
			this.delete(barcode);
		}
		ContentValues values = createContentValues(barcode);
		dbId = db.insert("barcode", null, values);
		
		return dbId;
	}
	
	/******************************************
	 * UPDATE
	 *****************************************/
	
	public void update(Barcode barcode) {
		ContentValues values = createContentValues(barcode);
		db.update("barcode", values, " _id = '" + barcode.getId() + "' ", null);
	}
	
	/******************************************
	 * DELETE
	 *****************************************/
	
	public void delete(Barcode barcode){
		if(barcode.getId() > 0){
			db.delete("barcode", " _id='" + barcode.getId() + "' ", null);
		}
	}
	
	/******************************************
	 * GENERAL
	 *****************************************/
	
	private void setCursor(String where){
		Debug.d("WHERE ", where);
		this.cursor = this.db.rawQuery(
				"SELECT _id, barcode_id, rel_id, rel_id_type "
				+ "FROM barcode " 
				+ " " + where + " "
				+ "ORDER BY _id ASC", null);
	}
	
	private void setColoumns(){
		this.idCol = this.cursor.getColumnIndex("_id");
		this.relIdCol = this.cursor.getColumnIndex("rel_id");
		this.relIdTypeCol = this.cursor.getColumnIndex("rel_id_type");
		this.barcodeIdCol = this.cursor.getColumnIndex("barcode_id");
	}
	
	private Barcode getBarcodeData(){
		Barcode barcode = new Barcode();
		
		barcode.setId(this.cursor.getLong(this.idCol));
		barcode.setRelId(this.cursor.getLong(this.relIdCol));
		barcode.setRelTypeId(this.cursor.getInt(this.relIdTypeCol));
		barcode.setBarcodeId(this.cursor.getString(this.barcodeIdCol));
		
		return barcode;
	}
	
	private ArrayList<Barcode> sortCursorArrayList(){
		ArrayList<Barcode> barcodes = new ArrayList<Barcode>();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				do{
					barcodes.add(this.getBarcodeData());
				}while(this.cursor.moveToNext());
			}
		}
		
		return barcodes;
	}
	
	private Barcode sortCursor(){
		Barcode barcode = new Barcode();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				barcode = this.getBarcodeData();
			}
		}
		
		return barcode;
	}
	
	private ContentValues createContentValues(Barcode barcode) {
		ContentValues values = new ContentValues();
		
		values.put("rel_id", barcode.getRelId());
		values.put("rel_id_type", barcode.getRelTypeId());
		values.put("barcode_id", barcode.getBarcodeId());
		
		return values;
	}
	
	/******************************************
	 * CHECK
	 *****************************************/
	

}
