package com.restusers.api.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.restusers.api.entity.User;

public interface UserService {

	User findByEmail(String email);
	
	User createOrUpdate(User user);
	
	User findById(String id);
	
	void delete(String id);
	
	Page<User> findAll(int page, int count);
	
	Set<User> findByIdIn(List<String> ids);
}
