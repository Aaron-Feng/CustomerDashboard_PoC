package com.vonage.vnet.domain;

public enum UserRoleType {
	
	DEALER_EXECUTIVE("DEALER_EXECUTIVE"),
	REGION_OFFICE("REGION_OFFICE"),
	ZONE_OFFICE("ZONE_OFFICE"),
	CORPORATE_PERSONNEL("CORPORATE_PERSONNEL"),
	NATIONAL("NATIONAL"),
	SNET_SUPERUSER("SNET_SUPERUSER");
	
	private final String userType;
	
	UserRoleType(String userType) {
		this.userType = userType;
	}
	
	public String getUserType() {
		return userType;
	}

}
