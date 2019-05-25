package com.vonage.vnet.services;

import java.util.List;

import com.vonage.vnet.entity.Product;

public interface ProductService {
List<Product> listAll();
Product getById(int id);
Product saveOrUpdate(Product product);
void delete(int id);
}
