package com.vonage.vnet.controller;

import java.security.Principal;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vonage.vnet.entity.User;
import com.vonage.vnet.services.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	 @RequestMapping(value="/login",method = RequestMethod.GET)
	    public String login(){
	        return "login";
	    }
	 @RequestMapping(value="/registration", method = RequestMethod.GET)
	    public String registration(Model model){
	        User user = new User();
	        model.addAttribute("user", user);
	        return "registration";
	    }
	 @RequestMapping(value = "/registration", method = RequestMethod.POST)
	    public String createNewUser(@Valid User user, BindingResult bindingResult,Model model) {

	        User userExists=null;
	        if(userService.findUserByName(user.getName()).isPresent())
	        	userExists=userService.findUserByName(user.getName()).get();
	        if (userExists != null) {
	            bindingResult
	                    .rejectValue("name", "error.user",
	                            "There is already a user registered with the name provided");
	        }
	        if (bindingResult.hasErrors()) {
	            return "registration";
	        } else {
	            userService.saveUser(user);
	            model.addAttribute("successMessage", "User has been registered successfully");
	            model.addAttribute("user", new User());
	        }
	        return "registration";
	    }
	@RequestMapping("/welcome")
	public String welcome(Principal principal,Model model) {
		try{
			model.addAttribute("username",principal.getName());
		}catch(Exception e) {
			model.addAttribute("username","Guest");
		}
		return "home";
		
	}
}
