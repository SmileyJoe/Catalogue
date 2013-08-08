package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import com.smileyjoedev.genLibrary.TimeStamp;

public class Item {
	
	private long id;
	private String title;
	private String desc;
	private long pdtCreate;
	private long pdtUpdate;
	private ArrayList<Category> categories;
	private ArrayList<LocationQuantity> locations;
	
	/**********************************************
	 * Constructor
	 *********************************************/
	
	public Item(){
		this.setId(0);
		this.setTitle("");
		this.setDesc("");
		this.setPdtCreate(0);
		this.setPdtUpdate(0);
		
		Category cat = new Category();
		ArrayList<Category> cats = new ArrayList<Category>();
		cats.add(cat);
		this.setCategories(cats);
		
		LocationQuantity locQuan = new LocationQuantity();
		ArrayList<LocationQuantity> locQuans = new ArrayList<LocationQuantity>();
		locQuans.add(locQuan);
		this.setLocations(locQuans);
	}
	
	/**********************************************
	 * Setters
	 *********************************************/
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public void setPdtCreate(long pdtCreate) {
		this.pdtCreate = pdtCreate;
	}
	
	public void setPdtUpdate(long pdtUpdate) {
		this.pdtUpdate = pdtUpdate;
	}
	
	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}
	
	public void setLocations(ArrayList<LocationQuantity> locations) {
		this.locations = locations;
	}

	/**********************************************
	 * Setters
	 *********************************************/
	
	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDesc() {
		return desc;
	}

	public long getPdtCreate() {
		return pdtCreate;
	}
	
	public String getCreateFormatted(){
		return TimeStamp.convertUt(this.getPdtCreate(), Constants.DATE_FORMAT_LONG);
	}

	public long getPdtUpdate() {
		return pdtUpdate;
	}
	
	public String getUpdateFormatted(){
		return TimeStamp.convertUt(this.getPdtUpdate(), Constants.DATE_FORMAT_LONG);
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public ArrayList<LocationQuantity> getLocations() {
		return locations;
	}

	public long getTotalQuantity(){
		long total = 0;
		
		for(int i = 0; i < this.getLocations().size(); i++){
			total += this.getLocations().get(i).getQuantity();
		}
		
		return total;
	}
	
	public ArrayList<Location> getLocationTitles(){
		ArrayList<Location> locations = new ArrayList<Location>();
		
		for(int i = 0; i < this.getLocations().size(); i++){
			locations.add(this.getLocations().get(i).getLocation());
		}
		
		return locations;
	}
	
	/**********************************************
	 * Add
	 *********************************************/
	
	public void addLocation(Location location, long quantity){
		LocationQuantity locQuan = new LocationQuantity();
		
		locQuan.setLocation(location);
		locQuan.setQuantity(quantity);
		
		this.locations.add(locQuan);
	}
	
	public void addCategory(Category category){
		this.categories.add(category);
	}
	
	/**********************************************
	 * General
	 *********************************************/
	
	@Override
	public String toString() {
		return "Item [getId()=" + getId() + ", getTitle()=" + getTitle() + ", getDesc()=" + getDesc() + ", getPdtCreate()=" + getPdtCreate() + ", getPdtUpdate()=" + getPdtUpdate() + ", getCategories()=" + getCategories().toString() + ", getLocations()=" + getLocations().toString() + "]";
	}
	
	
	
}
