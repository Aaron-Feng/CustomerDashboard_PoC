package com.vonage.vnet.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="Product")
public class Product {
	@Id
	@Column(name="P_ID")
	private int id;
	
	@Column(name="P_NAME")
	private String name;
	
	@Column(name="P_PRICE")
	private BigDecimal price;
	
	@Column(name="P_DESCRIPTION")
	private String description;

	@ManyToMany(mappedBy = "products")
    private List<Order> orders = new ArrayList<>();
	
	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	public Product() {
		
	}

	public Product(String name, BigDecimal price, String description) {
		super();
		this.name = name;
		this.price = price;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}