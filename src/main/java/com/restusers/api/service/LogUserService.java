package com.restusers.api.service;

import java.time.LocalDate;

import com.restusers.api.entity.LogUser;
import com.restusers.api.entity.User;

public interface LogUserService {

	LogUser registerLogUser(LogUser logUser);
	
	LocalDate findLastLogin(User user);
}
