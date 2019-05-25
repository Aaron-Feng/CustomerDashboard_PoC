package com.vonage.vnet.repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vonage.vnet.entity.User;
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

	Optional<User> findByName(String name);

}
