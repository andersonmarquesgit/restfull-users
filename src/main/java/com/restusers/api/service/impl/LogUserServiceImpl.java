package com.restusers.api.service.impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restusers.api.entity.LogUser;
import com.restusers.api.entity.User;
import com.restusers.api.repository.LogUserRepository;
import com.restusers.api.service.LogUserService;

@Service
public class LogUserServiceImpl implements LogUserService {

	@Autowired
	private LogUserRepository logUserRepository;
	
	@Override
	public LogUser registerLogUser(LogUser logUser) {
		return logUserRepository.save(logUser);
	}

	@Override
	public LocalDate findLastLogin(User user) {
		LogUser logUser = logUserRepository.findLastLogin(user);
		if(logUser == null) {
			return LocalDate.now();
		}
		return logUser.getLastLogin();
	}

}
