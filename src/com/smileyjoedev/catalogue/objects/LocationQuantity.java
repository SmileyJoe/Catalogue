package com.smileyjoedev.catalogue.objects;


public class LocationQuantity {
	
	private Location location;
	private long quantity;
	
	/**********************************************
	 * Constructor
	 *********************************************/
	
	public LocationQuantity(){
		this.setLocation(new Location());
		this.setQuantity(0);
	}
	
	/**********************************************
	 * Setters
	 *********************************************/
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	/**********************************************
	 * Getters
	 *********************************************/
	
	public Location getLocation() {
		return location;
	}

	public long getQuantity() {
		return quantity;
	}

	/**********************************************
	 * General
	 *********************************************/
	
	@Override
	public String toString() {
		return "LocationQuantity [getLocation()=" + getLocation().toString() + ", getQuantity()=" + getQuantity() + "]";
	}
	
	
}
