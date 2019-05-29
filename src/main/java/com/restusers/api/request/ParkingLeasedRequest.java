package com.restusers.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingLeasedRequest {

	private String licencePlate;
	private String parkingSpace;
}
