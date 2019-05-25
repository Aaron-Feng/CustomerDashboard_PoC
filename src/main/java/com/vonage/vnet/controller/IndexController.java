package com.vonage.vnet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class for redirecting to dashboard page.
 * 
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/index.html")
    public String redirectIndex() {
        return "redirect:/";
    }

    /**
     * This method is responsible for redirecting to Package Inquiry page.
     * 
     * @return String
     */
    @RequestMapping(value = "/")
    public String home() {
        return "redirect:/packageInquiry";
    }

}