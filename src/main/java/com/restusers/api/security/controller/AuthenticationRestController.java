package com.restusers.api.security.controller;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.restusers.api.entity.User;
import com.restusers.api.response.Response;
import com.restusers.api.security.jwt.JwtAuthenticationRequest;
import com.restusers.api.security.jwt.JwtTokenUtil;
import com.restusers.api.security.model.CurrentUser;
import com.restusers.api.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
@CrossOrigin(origins = "*")//Permitindo o acesso de qualquer IP, porta, etc.
@Api(value = "Authentication")
public class AuthenticationRestController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping(value = "/signin")
	@ApiOperation(value = "Autenticação de usuário", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(@ApiResponse(code = 200, message = "Success", response = CurrentUser.class, 
		responseHeaders = @ResponseHeader(name = "Location", description = "token", response = String.class)))
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {
		
		try {
			
			if(this.validateAuthUser(authenticationRequest)) {
				return new ResponseEntity<>("Missing fields", HttpStatus.BAD_REQUEST);
			}
			
			final Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							authenticationRequest.getEmail(),
							authenticationRequest.getPassword()
					)
			);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
			final String token = jwtTokenUtil.generateToken(userDetails);
			final User user = userService.findByEmail(authenticationRequest.getEmail());
			user.setPassword(null);
			return ResponseEntity.ok(new CurrentUser(token, user));
		} catch (Exception e) {
			return new ResponseEntity<>("Invalid e-mail or password", HttpStatus.BAD_REQUEST);
		}
	}
	
	
	private boolean validateAuthUser(JwtAuthenticationRequest authenticationRequest) {
		if(authenticationRequest.getEmail() == null || authenticationRequest.getEmail() == ""
				|| authenticationRequest.getPassword() == null || authenticationRequest.getPassword() == "") {
			return true;
		}
		return false;
	}


	@PostMapping(value = "/signup")
	@ApiOperation(value = "Signup de usuário", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(@ApiResponse(code = 200, message = "Success", response = CurrentUser.class, 
		responseHeaders = @ResponseHeader(name = "Location", description = "token", response = String.class)))
	public ResponseEntity<Response<CurrentUser>> createAuthenticationToken(@RequestBody User userRequest, BindingResult result) throws AuthenticationException {
		
		Response<CurrentUser> response = new Response<CurrentUser>();
		
		validateCreateUser(userRequest, result);
		if (result.hasErrors()) {
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		userRequest.setPassword(this.passwordEncoder.encode(userRequest.getPassword()));
		User userPersisted = this.userService.createOrUpdate(userRequest);

		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						userPersisted.getEmail(),
						userPersisted.getPassword()
				)
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final UserDetails userDetails = userDetailsService.loadUserByUsername(userPersisted.getEmail());
		final String token = jwtTokenUtil.generateToken(userDetails);
		final User user = userService.findByEmail(userPersisted.getEmail());
		user.setPassword(null);
		
		response.setData(new CurrentUser(token, userPersisted));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}
	
	private void validateCreateUser(User user, BindingResult result) {
		if (user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email no information"));
		}else if(userService.findByEmail(user.getEmail()) != null) {
			result.addError(new ObjectError("User", "Email already exists"));
		}
		
		if (user.getFirstName() == null) {
			result.addError(new ObjectError("User", "First name no information"));
		}
		
		if (user.getLastName() == null) {
			result.addError(new ObjectError("User", "Last name no information"));
		}
	}
}
