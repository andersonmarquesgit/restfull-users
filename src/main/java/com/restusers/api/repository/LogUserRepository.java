package com.restusers.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.restusers.api.entity.Log;

public interface LogUserRepository extends JpaRepository<Log, String> {

	@Query("select l from Log l " + 
			"where l.lastLogin in " + 
			"    ( select max(lg.lastLogin) " + 
			"      from Log lg where lg.userId = :userId )")
	Log findLastLogin(@Param(value = "userId") String userId);
}
