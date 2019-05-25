package com.vonage.vnet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vonage.vnet.entity.Product;
import com.vonage.vnet.services.ProductService;

@Controller
public class ProductController {
	private ProductService productService;
	
	@Autowired
	public void setProductService(ProductService productService) {
		this.productService=productService;
	}
	
	
	@RequestMapping("/product/productList")
	public String listProducts(Model model){
		model.addAttribute("products",productService.listAll());
		return "product/productList";
	}
	
	@RequestMapping("/product/productShow/{id}")
	public String getProduct(@PathVariable String id, Model model) {
		model.addAttribute("product",productService.getById(Integer.valueOf(id)));
		return "product/productShow";
	}
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/product/new")
	public String newProduct(Model model) {
		model.addAttribute("product",new Product());
		return "product/productForm";
	}
	@RequestMapping(value = "/product/newProduct", method=RequestMethod.POST)
	public String saveProduct(@ModelAttribute Product product) {
		System.out.println(product.getId());
		Product savedProduct = productService.saveOrUpdate(product);
		return "redirect:/product/productList";
	}
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/product/editProduct/{id}")
	public String editProduct(@PathVariable String id, Model model) {
		Product product = productService.getById(Integer.valueOf(id));
		product.setId(Integer.valueOf(id));
		model.addAttribute("product",product);
		return "product/productForm";
	}
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/product/deleteProduct/{id}")
	public String deleteProduct(@PathVariable String id) {
		productService.delete(Integer.valueOf(id));
		return "redirect:/product/productList";
	}
}
