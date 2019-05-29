package com.restusers;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.restusers.api.entity.User;
import com.restusers.api.enums.ProfileEnum;
import com.restusers.api.repository.UserRepository;

@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	// Criando usuário ao iniciar a aplicação
	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			initUsers(userRepository, passwordEncoder);
		};
	}

	private void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		User admin = new User();
		admin.setFirstName("Administrador");
		admin.setLastName("Pitang");
		admin.setEmail("admin@pitang.com");
		admin.setPassword(passwordEncoder.encode("123456"));
		admin.setProfile(ProfileEnum.ROLE_ADMIN);
		
		User user = new User();
		user.setFirstName("User");
		user.setLastName("Pitang");
		user.setEmail("user@pitang.com");
		user.setPassword(passwordEncoder.encode("123456"));
		user.setProfile(ProfileEnum.ROLE_USER);
		

		User find = userRepository.findByEmail("admin@pitang.com");
		User find1 = userRepository.findByEmail("user@pitang.com");
		if (find == null && find1 == null) {
			userRepository.save(admin);
			userRepository.save(user);
		}
	}
	
}
