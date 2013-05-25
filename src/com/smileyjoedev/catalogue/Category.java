package com.smileyjoedev.catalogue;

public class Category {
	
	private String title;
	private long id;
	private long parentId;
	private long numChildren;
	private long numItems;

	/**********************************************
	 * Constructor
	 *********************************************/
	
	public Category(){
		this.setTitle("");
		this.setId(0);
		this.setParentId(0);
		this.setNumChildren(0);
		this.setNumItems(0);
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
	
	@Override
	public String toString() {
		return "Category [getTitle()=" + getTitle() + ", getId()=" + getId() + ", getParentId()=" + getParentId() + ", getNumChildren()=" + getNumChildren() + ", getNumItems()=" + getNumItems() + "]";
	}
	
}
