package com.restusers.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.restusers.api.entity.LogUser;
import com.restusers.api.entity.User;

public interface LogUserRepository extends JpaRepository<LogUser, String> {

	@Query("select l from LogUser l " + 
			"where l.lastLogin in " + 
			"    ( select max(lg.lastLogin) " + 
			"      from LogUser lg where lg.user = :user )")
	LogUser findLastLogin(@Param(value = "user") User user);
}
