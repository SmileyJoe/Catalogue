package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.content.Context;

public class Category {
	
	private String title;
	private long id;
	private long parentId;
	private long numChildren;
	private long numItems;
	private String breadCrumb;
	private Nfc nfc;

	/**********************************************
	 * Constructor
	 *********************************************/
	
	public Category(){
		this.setTitle("");
		this.setId(0);
		this.setParentId(0);
		this.setNumChildren(0);
		this.setNumItems(0);
		this.setBreadCrumb("");
		this.setNfc(new Nfc());
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
	
	public String getBreadCrumb(Context context){
		if(this.breadCrumb.equals("")){
			DbCategoryAdapter adapter = new DbCategoryAdapter(context);
			ArrayList<Category> categories = adapter.getBreadCrumb(this.getId());
			
			for(int i = categories.size()-1; i >= 0; i--){
				if(i < (categories.size()-1)){
					this.breadCrumb += "/";
				}
				this.breadCrumb += categories.get(i).getTitle();
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
	
	@Override
	public String toString() {
		return "Category [getTitle()=" + getTitle() + ", getId()=" + getId() + ", getParentId()=" + getParentId() + ", getNumChildren()=" + getNumChildren() + ", getNumItems()=" + getNumItems() + "]";
	}
	
}
