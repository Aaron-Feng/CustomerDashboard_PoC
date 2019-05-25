package com.vonage.vnet.servicesImpl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vonage.vnet.entity.Order;
import com.vonage.vnet.entity.User;
import com.vonage.vnet.repo.OrderRepository;
import com.vonage.vnet.repo.UserRepository;
import com.vonage.vnet.services.ShopService;

@Service
public class ShopServiceImpl implements ShopService {
	private OrderRepository orderRepository;
	private UserRepository userRepository;

	@Autowired
	public ShopServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Set<Order> getOrdersByUser(int userId) {
		Set<Order> orders = new HashSet<>();
		User user = userRepository.findById(userId).get();
		orders = user.getOrders();
		return orders;
	}

	@Override
	public Order saveOrder(Order order) {
		Order savedOrder = orderRepository.save(order);
		return savedOrder;
	}

}
