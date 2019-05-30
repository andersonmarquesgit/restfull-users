package com.restusers.api.security.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.restusers.api.entity.Log;
import com.restusers.api.entity.User;
import com.restusers.api.enums.ProfileEnum;
import com.restusers.api.response.Response;
import com.restusers.api.security.jwt.JwtAuthenticationRequest;
import com.restusers.api.security.jwt.JwtTokenUtil;
import com.restusers.api.security.model.CurrentUser;
import com.restusers.api.service.LogUserService;
import com.restusers.api.service.UserService;
import com.restusers.api.to.UserTO;

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
	private LogUserService logUserService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping(value = "/signin")
	@ApiOperation(value = "Signin de usuário", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
			
			Log logUser = new Log();
			logUser.setUserId(user.getId());
			logUser.setLastLogin(LocalDateTime.now());
			this.logUserService.registerLogUser(logUser);
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
	public ResponseEntity<?> createAuthenticationToken(@RequestBody User userRequest) throws AuthenticationException {
		
		try {
			
			if(this.validateCreateUser(userRequest)) {
				return new ResponseEntity<>("Missing fields", HttpStatus.BAD_REQUEST);
			}
			
			if(userService.findByEmail(userRequest.getEmail()) != null) {
				return new ResponseEntity<>("E-mail already exists", HttpStatus.BAD_REQUEST);
			}
			 
			String passwordRequest = userRequest.getPassword();
			userRequest.setPassword(this.passwordEncoder.encode(passwordRequest));
			userRequest.setProfile(ProfileEnum.ROLE_USER);
			userRequest.getPhones().forEach(p -> p.setUser(userRequest));
			userRequest.setCreatedAt(LocalDateTime.now());
			User userPersisted = this.userService.createOrUpdate(userRequest);
	
			final Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							userRequest.getEmail(),
							passwordRequest
					)
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			final UserDetails userDetails = userDetailsService.loadUserByUsername(userPersisted.getEmail());
			final String token = jwtTokenUtil.generateToken(userDetails);
			final User user = userService.findByEmail(userPersisted.getEmail());
			user.setPassword(null);
			return ResponseEntity.ok(new CurrentUser(token, user));
			
		} catch (Exception e) {
			return new ResponseEntity<>("Invalid e-mail or password", HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(value = "/me")
	@PreAuthorize("hasAnyRole('USER')") // Autorização com base no perfil. Nesse caso apenas USER pode consultar suas informações.
	@ApiOperation(value = "Consulta de informações do usuário pelo token")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = UserTO.class),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure") })
	public ResponseEntity<Response<UserTO>> update(HttpServletRequest request){
		Response<UserTO> response = new Response<UserTO>();
		
		String authToken = request.getHeader("Authorization");
		String username = jwtTokenUtil.getUserNameFromToken(authToken);
		User user = userService.findByEmail(username);
		
		UserTO userTO = new UserTO();
		userTO.setFirstName(user.getFirstName());
		userTO.setLastName(user.getLastName());
		userTO.setEmail(user.getEmail());
		userTO.setPhones(user.getPhones());
		userTO.setCreatedAt(user.getCreatedAt());
		userTO.setLastLogin(this.logUserService.findLastLogin(user.getId()));
		
		response.setData(userTO);
		
		return ResponseEntity.ok(response);
		
	}
	
	private boolean validateCreateUser(User user) {
		if (user.getEmail() == null) {
			return true;
		}
		
		if (user.getFirstName() == null) {
			return true;
		}
		
		if (user.getLastName() == null) {
			return true;
		}
		
		return false;
	}
}
