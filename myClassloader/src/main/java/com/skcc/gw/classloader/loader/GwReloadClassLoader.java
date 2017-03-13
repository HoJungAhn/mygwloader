package com.skcc.gw.classloader.loader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.jci.stores.ResourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GwReloadClassLoader extends ClassLoader{
	private Logger logger = LoggerFactory.getLogger(GwReloadClassLoader.class);
	
	private Map<String, GwResourcesClassLoaderDelegator> delegateLoaderMap = new ConcurrentHashMap<String, GwResourcesClassLoaderDelegator>();
	public GwReloadClassLoader(ClassLoader parent){
		super(parent);
	}
	
	public void addResourceStore(ResourceStore store){
		
	}
}
