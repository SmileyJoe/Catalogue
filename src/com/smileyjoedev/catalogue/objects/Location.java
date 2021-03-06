package com.smileyjoedev.catalogue.objects;

import java.util.ArrayList;

import com.smileyjoedev.catalogue.db.DbLocationAdapter;

import android.content.Context;

public class Location {
	
	private String title;
	private long id;
	private long parentId;
	private long numChildren;
	private long numItems;
	private String breadCrumb;
	private Nfc nfc;
	private Barcode barcode;
	
	/**********************************************
	 * Constructor
	 *********************************************/
	
	public Location(){
		this.setTitle("");
		this.setId(0);
		this.setParentId(0);
		this.setNumChildren(0);
		this.setNumItems(0);
		this.setBreadCrumb("");
		this.setNfc(new Nfc());
		this.setBarcode(new Barcode());
	}

	/**********************************************
	 * Setters
	 *********************************************/
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setParentId(long id){
		this.parentId = id;
	}
	
	public void setNumChildren(long num){
		this.numChildren = num;
	}
	
	public void setNumItems(long num){
		this.numItems = num;
	}
	
	public void setBreadCrumb(String breadCrumb){
		this.breadCrumb = breadCrumb;
	}
	
	public void setNfc(Nfc nfc){
		this.nfc = nfc;
	}

	public void setBarcode(Barcode barcode){
		this.barcode = barcode;
	}
	
	/**********************************************
	 * Getters
	 *********************************************/
	
	public String getTitle() {
		return title;
	}
	
	public long getId() {
		return id;
	}

	public long getParentId(){
		return this.parentId;
	}
	
	public long getNumChildren(){
		return this.numChildren;
	}
	
	public long getNumItems(){
		return this.numItems;
	}
	
	public Nfc getNfc(){
		return this.nfc;
	}

	public Barcode getBarcode(){
		return this.barcode;
	}
	
	public String getBreadCrumb(Context context){
		if(this.breadCrumb.equals("")){
			DbLocationAdapter adapter = new DbLocationAdapter(context);
			ArrayList<Location> locations = adapter.getBreadCrumb(this.getId());
			
			for(int i = locations.size()-1; i >= 0; i--){
				if(i < (locations.size()-1)){
					this.breadCrumb += "/";
				}
				this.breadCrumb += locations.get(i).getTitle();
			}
		}
		return this.breadCrumb;
	}
	
	public boolean hasNfc(){
		if(this.getNfc().exists()){
			return true;
		} else {
			return false;
		}
	}

	public boolean hasBarcode(){
		if(this.getBarcode().exists()){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Location [getTitle()=" + getTitle() + ", getId()=" + getId() + ", getParentId()=" + getParentId() + ", getNumChildren()=" + getNumChildren() + ", getNumItems()=" + getNumItems() + ", getNfc()=" + getNfc() + ", getBarcode()=" + getBarcode() + ", hasNfc()=" + hasNfc() + ", hasBarcode()=" + hasBarcode() + "]";
	}
	
}
