package com.example.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ppmtool.domain.User;
import com.example.ppmtool.exceptions.UsernameAlreadyExistsException;
import com.example.ppmtool.repositories.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public User saveUser(User newUser) {
		try {
			newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
			
			//Username must be unique
			newUser.setUsername(newUser.getUsername());
			
			//Password and ConfirmPassoword must match
			//Do not persist/show ConfirmPassword
			newUser.setConfirmPassword("");
			
			return userRepository.save(newUser);
		}catch (Exception e) {
			throw new UsernameAlreadyExistsException("Username "+newUser.getUsername()+" already exists");
		}
		
		
	}
}
