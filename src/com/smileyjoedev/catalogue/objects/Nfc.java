package com.smileyjoedev.catalogue.objects;

public class Nfc {

	private long id;
	private String tagId;
	private long relId;
	private int relTypeId;
	
	public Nfc(){
		this.setId(0);
		this.setRelId(0);
		this.setRelTypeId(0);
		this.setTagId("");
	}
	
	public void setId(long id) {
		this.id = id;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public void setRelId(long relId) {
		this.relId = relId;
	}
	public void setRelTypeId(int relTypeId) {
		this.relTypeId = relTypeId;
	}
	public long getId() {
		return id;
	}
	public String getTagId() {
		return tagId;
	}
	public long getRelId() {
		return relId;
	}
	public int getRelTypeId() {
		return relTypeId;
	}
	
	public boolean exists(){
		if(this.getTagId().equals("")){
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String toString() {
		return "Nfc [getId()=" + getId() + ", getTagId()=" + getTagId()
				+ ", getRelId()=" + getRelId() + ", getRelTypeId()="
				+ getRelTypeId() + "]";
	}
	
	
	
}
