package com.vonage.vnet.controller;

import java.security.Principal;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vonage.vnet.converter.OrderFormToOrder;
import com.vonage.vnet.entity.Order;
import com.vonage.vnet.entity.User;
import com.vonage.vnet.param.ShopForm;
import com.vonage.vnet.services.ProductService;
import com.vonage.vnet.services.ShopService;
import com.vonage.vnet.services.UserService;

@Controller
public class ShopController {
	private OrderFormToOrder orderFormToOrder;
	private ShopService shopService;
	private UserService userService;
	@Autowired
	public void setUserService(UserService userService) {
		this.userService=userService;
	}
	@Autowired
	public void setOrderFormToOrder(OrderFormToOrder orderFormToOrder) {
		this.orderFormToOrder=orderFormToOrder;
	}
	@Autowired
	public void setShopService(ShopService shopService) {
		this.shopService = shopService;
	}

	private ProductService productService;

	@Autowired
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	@RequestMapping("/shop/productList")
	public String viewProducts(Model model) {
		ShopForm shopForm=new ShopForm();
		model.addAttribute("shopForm",shopForm);
		model.addAttribute("products", productService.listAll());
		return "shop/productList";
	}

	@RequestMapping(value = "/shop/shoppingcart", method = RequestMethod.POST)
	public String SaveShoppingCart(@Valid ShopForm shopForm, BindingResult bindingResult,Model model,Principal principal) {
		 if(bindingResult.hasErrors()){
	            return "shop/productList";
	        }
		 String username="Guest";
		 try{
				username=principal.getName();
			}catch(Exception e) {
				e.printStackTrace();
			}
		 User user = userService.findUserByName(username).get();
		 Order order=orderFormToOrder.convert(shopForm);
		 order.setUser(user);
		 model.addAttribute("order",order);
		 model.addAttribute("products",order.getProducts());
		 shopService.saveOrder(order);
		return "shop/shoppingcart";

	}
	@RequestMapping("shop/orderSummary")
	public String viewOrderSummary(Principal principal,Model model) {
		String username=principal.getName();
		User user=userService.findUserByName(username).get();
		Set<Order> orders = user.getOrders();
		model.addAttribute("orders",orders);
		return "shop/orderSummary";
		
	}
}
