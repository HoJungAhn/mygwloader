package com.skcc.gw.classloader;

import org.springframework.context.ApplicationContext;

public interface GwContextFacade {
	 ApplicationContext getApplicationContext(String contextId);
}
