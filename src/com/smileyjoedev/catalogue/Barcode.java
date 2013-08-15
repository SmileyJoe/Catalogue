package com.smileyjoedev.catalogue;

public class Barcode {

	private long id;
	private String barcodeId;
	private long relId;
	private int relTypeId;
	
	public Barcode(){
		this.setId(0);
		this.setRelId(0);
		this.setRelTypeId(0);
		this.setBarcodeId("");
	}
	
	public void setId(long id) {
		this.id = id;
	}
	public void setBarcodeId(String barcodeId) {
		this.barcodeId = barcodeId;
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
	public String getBarcodeId() {
		return barcodeId;
	}
	public long getRelId() {
		return relId;
	}
	public int getRelTypeId() {
		return relTypeId;
	}
	
	public boolean exists(){
		if(this.getBarcodeId().equals("")){
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String toString() {
		return "Barcode [getId()=" + getId() + ", getBarcodeId()=" + getBarcodeId() + ", getRelId()=" + getRelId() + ", getRelTypeId()=" + getRelTypeId() + ", exists()=" + exists() + "]";
	}
	
}
