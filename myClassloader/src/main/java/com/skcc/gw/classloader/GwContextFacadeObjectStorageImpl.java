package com.skcc.gw.classloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class GwContextFacadeObjectStorageImpl implements GwContextFacade, ApplicationContextAware {
	private ApplicationContext applicationContext;
	private Map <String, Object> contextMap = new ConcurrentHashMap<String, Object>();
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		
	}

	public ApplicationContext getApplicationContext(String contextId) {
		// TODO Auto-generated method stub
		return null;
	}
}
