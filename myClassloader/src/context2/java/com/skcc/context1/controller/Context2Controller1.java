package com.skcc.context1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.skcc.context1.service.Context2Service1;

@Controller
public class Context2Controller1 {
	@Autowired
	private Context2Service1 service;
	
	public void testController() throws Exception{
		System.out.println(service.getService());
	}
}
