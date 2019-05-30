package com.restusers.api.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restusers.api.enums.ProfileEnum;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_user")
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	@NotBlank(message = "First name required")
	@Column(name = "firstName")
	private String firstName;
	
	@NotBlank(message = "Last name required")
	@Column(name = "lastName")
	private String lastName;
	
	@NotBlank(message = "Email required")
	@Email(message = "Email invalid")
	@Column(name = "email", unique = true)
	private String email;
	
	@NotBlank(message = "Password required")
	@Size(min = 6)
	@Column(name = "password", unique = true)
	private String password;
	
	@JsonIgnore
	private ProfileEnum profile;
	
	@OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST})
	private List<Phone> phones;

	@JsonIgnore
	@Column(name = "created_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime createdAt;
	
}
