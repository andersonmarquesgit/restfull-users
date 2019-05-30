package com.restusers.api.service;

import java.time.LocalDateTime;

import com.restusers.api.entity.Log;

public interface LogUserService {

	Log registerLogUser(Log logUser);
	
	LocalDateTime findLastLogin(String userId);
}
