package com.parking.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeUserController {

	@RequestMapping("/")
	public String index() {
		return "index";
	}
}
