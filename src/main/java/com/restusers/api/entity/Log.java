package com.restusers.api.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_log")
@Getter
@Setter
public class Log {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	@Column(name = "last_login", columnDefinition = "TIMESTAMP")
	private LocalDateTime lastLogin;
	
	@Column(name = "user_id")
	private String userId;
}
