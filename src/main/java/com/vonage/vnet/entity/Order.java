package com.vonage.vnet.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
@Entity
@Table(name="Order_list")
public class Order {
@Id
@Column(name="o_id")
@GeneratedValue(strategy=GenerationType.AUTO)
private int id;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name="u_id")
private User user;

@Column(name="total_PRICE")
private BigDecimal price;
@ManyToMany(fetch = FetchType.LAZY,
cascade = {
    CascadeType.PERSIST,
    CascadeType.MERGE
})
@JoinTable(name = "order_detail",
joinColumns = { @JoinColumn(name = "o_id") },
inverseJoinColumns = { @JoinColumn(name = "p_id") })
private List<Product> products = new ArrayList<>();
public List<Product> getProducts() {
	return products;
}

public void setProducts(List<Product> products) {
	this.products = products;
}

@CreationTimestamp
@Temporal(TemporalType.TIMESTAMP)
@Column(name="o_time")
private Date datetime;

public Order() {
}

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}

public BigDecimal getPrice() {
	return price;
}

public void setPrice(BigDecimal price) {
	this.price = price;
}

public Date getDatetime() {
	return datetime;
}

public void setDatetime(Date datetime) {
	this.datetime = datetime;
}
}
