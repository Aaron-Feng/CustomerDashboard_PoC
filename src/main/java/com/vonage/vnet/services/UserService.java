package com.vonage.vnet.services;

import java.util.Optional;

import com.vonage.vnet.entity.User;

public interface UserService {
	Optional<User> findUserByName(String name);

	void saveUser(User user);

	void saveAdminUser(User user);
}
