package com.vonage.vnet.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vonage.vnet.entity.Product;
@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

}
