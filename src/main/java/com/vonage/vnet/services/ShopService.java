package com.vonage.vnet.services;

import java.util.Set;

import com.vonage.vnet.entity.Order;
public interface ShopService {
Set<Order> getOrdersByUser(int userId);
Order saveOrder(Order order);
}
