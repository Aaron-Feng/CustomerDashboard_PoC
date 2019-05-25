package com.vonage.vnet.servicesImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vonage.vnet.entity.User;
import com.vonage.vnet.repo.UserRepository;
import com.vonage.vnet.services.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public void saveUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRole("USER");
		userRepository.save(user);
	}

	@Override
	public void saveAdminUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRole("ADMIN");
		userRepository.save(user);
		
	}

	@Override
	public Optional<User> findUserByName(String name) {
		return userRepository.findByName(name);
	}

}
