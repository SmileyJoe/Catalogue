package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.content.Context;

public class Location {
	
	private String title;
	private long id;
	private long parentId;
	private long numChildren;
	private long numItems;
	private String breadCrumb;
	
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
	
	@Override
	public String toString() {
		return "Location [getTitle()=" + getTitle() + ", getId()=" + getId() + ", getParentId()=" + getParentId() + ", getNumChildren()=" + getNumChildren() + ", getNumItems()=" + getNumItems() + "]";
	}
	
}
