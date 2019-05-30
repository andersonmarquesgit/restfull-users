package com.restusers.api.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_log_user")
@Getter
@Setter
public class LogUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	@JsonIgnore
	@Column(name = "last_login")
	private LocalDate lastLogin;
	
	@ManyToOne
    @JoinColumn(name = "fk_tb_user", nullable = false)
	@JsonIgnore
	private User user;
	
	public LogUser(User user, LocalDate lastLogin) {
		this.user = user;
		this.lastLogin = lastLogin;
	}
}
