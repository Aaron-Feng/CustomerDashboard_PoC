package com.vonage.vnet.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vonage.vnet.entity.Order;
@Repository
public interface OrderRepository extends CrudRepository<Order, Integer>{

}
