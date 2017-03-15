package com.skcc.context1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.skcc.context1.service.Context1Service1;

@Controller
public class Context1Controller1 {
	@Autowired
	private Context1Service1 service;
	
	public void testController() throws Exception{
		System.out.println(service.getService());
	}
}
