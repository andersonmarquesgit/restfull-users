package com.restusers.api.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restusers.api.entity.Log;
import com.restusers.api.repository.LogUserRepository;
import com.restusers.api.service.LogUserService;

@Service
public class LogUserServiceImpl implements LogUserService {

	@Autowired
	private LogUserRepository logUserRepository;
	
	@Override
	public Log registerLogUser(Log logUser) {
		return logUserRepository.save(logUser);
	}

	@Override
	public LocalDateTime findLastLogin(String userId) {
		Log logUser = logUserRepository.findLastLogin(userId);
		if(logUser == null) {
			return LocalDateTime.now();
		}
		return logUser.getLastLogin();
	}

}
