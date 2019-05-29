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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty("area_code")
	private int areaCode;
	
	@Column(name = "country_code")
	@JsonProperty("country_code")
	private String countryCode;
	
	@ManyToOne
    @JoinColumn(name = "fk_tb_user", nullable = false)
	@JsonIgnore
    private User user;
}
