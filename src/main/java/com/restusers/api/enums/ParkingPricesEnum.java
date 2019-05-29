package com.restusers.api.enums;

public enum ParkingPricesEnum {
	
	UP_TO_3_HOURS("1"),
	EXTRA_HOUR("2");
	
	public String id;
	
	ParkingPricesEnum(String id){
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
}
