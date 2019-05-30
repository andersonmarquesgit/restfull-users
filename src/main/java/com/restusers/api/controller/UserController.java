package com.restusers.api.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restusers.api.entity.User;
import com.restusers.api.response.Response;
import com.restusers.api.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*") // Permitindo o acesso de qualquer IP, porta, etc.
@Api(value = "User")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN')") // Autorização com base no perfil. Nesse caso apenas ADMIN podem criar usuários.
	@ApiOperation(value = "Criação de usuários", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(@ApiResponse(code = 201, message = "Novo usuário criado", response = User.class, 
		responseHeaders = @ResponseHeader(name = "User", description = "Usuário criado", response = User.class)))
	public ResponseEntity<Response<User>> create(HttpServletRequest request, 
			@ApiParam(
				    value="User", 
				    name="user", 
				    required=true)
			@RequestBody User user,
			BindingResult result) {
		Response<User> response = new Response<User>();

		try {
			validateCreateUser(user, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			user.setPassword(this.passwordEncoder.encode(user.getPassword()));
			user.setCreatedAt(LocalDateTime.now());
			User userPersisted = this.userService.createOrUpdate(user);
			response.setData(userPersisted);
		} catch (DuplicateKeyException dE) {
			response.getErrors().add("Email already registered!");
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	private void validateCreateUser(User user, BindingResult result) {
		if (user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email no information"));
		}
		
		if (user.getFirstName() == null) {
			result.addError(new ObjectError("User", "First name no information"));
		}
		
		if (user.getLastName() == null) {
			result.addError(new ObjectError("User", "Last name no information"));
		}
	}

	@PutMapping
	@PreAuthorize("hasAnyRole('ADMIN')") // Autorização com base no perfil. Nesse caso apenas ADMIN podem atualizar usuários.
	@ApiOperation(value = "Atualização de usuários", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(@ApiResponse(code = 200, message = "Usuário atualizado", response = User.class, 
		responseHeaders = @ResponseHeader(name = "Location", description = "uri do usuário atualizado", response = String.class)))
	public ResponseEntity<Response<User>> update(HttpServletRequest request, 
			@ApiParam(
				    value="User", 
				    name="user", 
				    required=true)
			@RequestBody User user,
			BindingResult result) {
		Response<User> response = new Response<User>();
		try {
			validateUpdateUser(user, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			user.setPassword(this.passwordEncoder.encode(user.getPassword()));
			User userUpdated = this.userService.createOrUpdate(user);
			response.setData(userUpdated);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	private void validateUpdateUser(User user, BindingResult result) {
		if (user.getId() == null) {
			result.addError(new ObjectError("User", "Id no information"));
		}
		if (user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email no information"));
		}
	}

	@GetMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('ADMIN')") // Autorização com base no perfil. Nesse caso apenas ADMIN podem consultar usuários.
	@ApiOperation(value = "Consultar Usuário pelo ID")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "Id do usuário", required = false, dataType = "string", paramType = "query", defaultValue = "1") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = User.class),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure") })
	public ResponseEntity<Response<User>> findById(@PathVariable String id) {
		Response<User> response = new Response<User>();
		User user = this.userService.findById(id);

		if (user == null) {
			response.getErrors().add("Register not found! ID: " + id);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(user);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('ADMIN')") // Autorização com base no perfil. Nesse caso apenas ADMIN podem excluir usuários.
	@ApiOperation(value = "Remover usuário", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = User.class),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure") })
	public ResponseEntity<Response<String>> delete(@PathVariable String id) {
		Response<String> response = new Response<String>();
		User user = this.userService.findById(id);

		if (user == null) {
			response.getErrors().add("Register not found! ID: " + id);
			return ResponseEntity.badRequest().body(response);
		}

		this.userService.delete(id);

		return ResponseEntity.ok(new Response<String>());
	}

	@GetMapping(value = "{page}/{count}")
	@PreAuthorize("hasAnyRole('ADMIN')") // Autorização com base no perfil. Nesse caso apenas ADMIN podem listar usuários.
	@ApiOperation(value = "Listar todos os usuários")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "Page", required = false, dataType = "string", paramType = "query", defaultValue = "0"),
			@ApiImplicitParam(name = "count", value = "Count", required = false, dataType = "string", paramType = "query", defaultValue = "10")})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", responseContainer = "Page"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure") })
	public ResponseEntity<Response<Page<User>>> findAll(@PathVariable int page, @PathVariable int count) {
		Response<Page<User>> response = new Response<Page<User>>();
		Page<User> users = this.userService.findAll(page, count);
		response.setData(users);

		return ResponseEntity.ok(response);
	}
}
