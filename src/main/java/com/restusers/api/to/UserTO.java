package com.restusers.api.to;

import java.time.LocalDateTime;
import java.util.List;

import com.restusers.api.entity.Phone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTO {

	private String firstName;
	private String lastName;
	private String email;
	private List<Phone> phones;
	private LocalDateTime createdAt;
	private LocalDateTime lastLogin;
}
