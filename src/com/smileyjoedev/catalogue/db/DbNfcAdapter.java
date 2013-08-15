package com.smileyjoedev.catalogue.db;

import java.util.ArrayList;

import com.smileyjoedev.catalogue.objects.Nfc;
import com.smileyjoedev.genLibrary.Camera;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.Notify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbNfcAdapter {
	
	/*****************************************
	 * VARIABLES
	 ****************************************/
	
	private Context context;
	private SQLiteDatabase db;
	private DbHelper dbHelper;
	private Cursor cursor;
	private int idCol;
	private int tagIdCol;
	private int relIdCol;
	private int relIdTypeCol;
	
	/*****************************************
	 * CONSTRUCTOR
	 ****************************************/
	
	public DbNfcAdapter(Context context) {
		this.context = context;
		this.dbHelper = new DbHelper(context);
		this.db = dbHelper.getWritableDatabase();
	}
	
	/******************************************
	 * GET
	 *****************************************/
	
	public Nfc getDetails(long nfcId){
		this.setCursor("WHERE _id = '" + nfcId + "' ");
		return this.sortCursor();
	}
	
	public Nfc getDetailsByTagId(String tagId){
		this.setCursor("WHERE nfc_tag_id = '" + tagId + "' ");
		return this.sortCursor();
	}
	
	public Nfc getDetailsByRel(long relId, int relTypeId){
		this.setCursor("WHERE rel_id = '" + relId + "' AND rel_id_type = '" + relTypeId + "' ");
		return this.sortCursor();
	}
	
	public ArrayList<Nfc> get(){
		this.setCursor("");
		return this.sortCursorArrayList();
	}
	
	/******************************************
	 * SAVE
	 *****************************************/
	
	public long save(Nfc nfc) {
		long dbId = 0;
		if(nfc.getId() > 0){
			this.delete(nfc);
		}
		ContentValues values = createContentValues(nfc);
		dbId = db.insert("nfc", null, values);
		
		return dbId;
	}
	
	/******************************************
	 * UPDATE
	 *****************************************/
	
	public void update(Nfc nfc) {
		ContentValues values = createContentValues(nfc);
		db.update("nfc", values, " _id = '" + nfc.getId() + "' ", null);
	}
	
	/******************************************
	 * DELETE
	 *****************************************/
	
	public void delete(Nfc nfc){
		if(nfc.getId() > 0){
			db.delete("nfc", " _id='" + nfc.getId() + "' ", null);
		}
	}
	
	/******************************************
	 * GENERAL
	 *****************************************/
	
	private void setCursor(String where){
		Debug.d("WHERE ", where);
		this.cursor = this.db.rawQuery(
				"SELECT _id, nfc_tag_id, rel_id, rel_id_type "
				+ "FROM nfc " 
				+ " " + where + " "
				+ "ORDER BY _id ASC", null);
	}
	
	private void setColoumns(){
		this.idCol = this.cursor.getColumnIndex("_id");
		this.relIdCol = this.cursor.getColumnIndex("rel_id");
		this.relIdTypeCol = this.cursor.getColumnIndex("rel_id_type");
		this.tagIdCol = this.cursor.getColumnIndex("nfc_tag_id");
	}
	
	private Nfc getNfcData(){
		Nfc nfc = new Nfc();
		
		nfc.setId(this.cursor.getLong(this.idCol));
		nfc.setRelId(this.cursor.getLong(this.relIdCol));
		nfc.setRelTypeId(this.cursor.getInt(this.relIdTypeCol));
		nfc.setTagId(this.cursor.getString(this.tagIdCol));
		
		return nfc;
	}
	
	private ArrayList<Nfc> sortCursorArrayList(){
		ArrayList<Nfc> nfcs = new ArrayList<Nfc>();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				do{
					nfcs.add(this.getNfcData());
				}while(this.cursor.moveToNext());
			}
		}
		
		return nfcs;
	}
	
	private Nfc sortCursor(){
		Nfc nfc = new Nfc();
		
		this.setColoumns();
		
		if(this.cursor != null){
			this.cursor.moveToFirst();
			if(this.cursor.getCount() > 0){
				nfc = this.getNfcData();
			}
		}
		
		return nfc;
	}
	
	private ContentValues createContentValues(Nfc nfc) {
		ContentValues values = new ContentValues();
		
		values.put("rel_id", nfc.getRelId());
		values.put("rel_id_type", nfc.getRelTypeId());
		values.put("nfc_tag_id", nfc.getTagId());
		
		return values;
	}
	
	/******************************************
	 * CHECK
	 *****************************************/
	

}
