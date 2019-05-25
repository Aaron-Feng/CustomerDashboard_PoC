package com.vonage.vnet.servicesImpl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vonage.vnet.entity.Product;
import com.vonage.vnet.repo.ProductRepository;
import com.vonage.vnet.services.*;
@Service
public class ProductServiceImpl implements ProductService {
	private ProductRepository productRepository;
	
	@Autowired
	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	@Override
	public List<Product> listAll() {
		List<Product> products = new ArrayList<>();
		productRepository.findAll().forEach(products::add);
		return products;
	}

	@Override
	public Product getById(int id) {
		return productRepository.findById(id).get();
	}

	@Override
	public Product saveOrUpdate(Product product) {
		Product savedProduct = productRepository.save(product);
		return savedProduct;
		
	}

	@Override
	public void delete(int id) {
		productRepository.deleteById(id);
	}
	

}
