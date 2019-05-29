package com.restusers.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_phone")
@Getter
@Setter
public class Phone {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	@Column(name = "number")
	private Long number;
	
	@Column(name = "area_code")
	private int areaCode;
	
	@Size(min = 6)
	@Column(name = "country_code")
	private String countryCode;
	
	@ManyToOne
    @JoinColumn(name = "fk_tb_user", nullable = false)
    private User user;
}
