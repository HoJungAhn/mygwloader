package com.skcc.gw.classloader.context;

import org.springframework.context.ApplicationContext;

import com.skcc.gw.classloader.config.GwClassLoaderConfig;
import com.skcc.gw.classloader.loader.GwReloadClassLoader;
import com.skcc.gw.classloader.loader.store.GwResouceStore;

public interface GwApplicationContext extends ApplicationContext{
	GwReloadClassLoader getReloadClassloader();
	void setReloadClassloader(GwReloadClassLoader reloadClassloader);
	void setReloadContext(GwClassLoaderConfig config, GwResouceStore store);
	
	void start();
	void stop();
	void reload();
}
