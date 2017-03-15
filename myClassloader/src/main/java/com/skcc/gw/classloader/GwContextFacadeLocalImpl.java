package com.skcc.gw.classloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.skcc.gw.classloader.config.GwClassLoaderConfig;
import com.skcc.gw.classloader.context.GwApplicationContext;
import com.skcc.gw.classloader.context.GwReloadApplicationContext;
import com.skcc.gw.classloader.loader.store.GwFileResourceStore;
import com.skcc.gw.util.IContextStartedAware;
import com.skcc.gw.util.JMXUtil;

@ManagedResource(description = "GwContextFacade")
public class GwContextFacadeLocalImpl implements GwContextFacade, ApplicationContextAware, CamelContextAware, IContextStartedAware{
	private Logger logger = LoggerFactory.getLogger(GwContextFacadeLocalImpl.class);
	
	private ApplicationContext applicationContext;
	private Map <String, GwClassLoaderConfig> configMap = new ConcurrentHashMap<String, GwClassLoaderConfig>();

	private Map <String, GwApplicationContext> contextMap = new ConcurrentHashMap<String, GwApplicationContext>();
	
	
	private void initializeContext(){
		if(configMap == null || configMap.size() == 0){
			logger.info("Biz Config is empty.");
			return;
		}
		
		for(String contextId: configMap.keySet()){
			GwApplicationContext context = new GwReloadApplicationContext(applicationContext);
			GwClassLoaderConfig config = configMap.get(contextId);
			if(logger.isDebugEnabled()){
				logger.debug("Gw Context Loading. contextID: " + config.getContextId());
				logger.debug("Gw Context Loading. class loc: " + config.getContextClassLocation());
				logger.debug("Gw Context Loading. xml file : " + config.getContextConfigXmlLocation());
				logger.debug("Gw Context Loading. accept pt: " + config.getAcceptClasses());
			}
			
			// context 초기화. file store를 이용
			context.setReloadContext(configMap.get(contextId), new GwFileResourceStore(configMap.get(contextId).getContextClassLocation()));
			contextMap.put(contextId, context);
			context.reload();
		}
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		initializeContext();

	}
	
	public Map<String, GwClassLoaderConfig> getConfigMap() {
		return configMap;
	}

	public void setConfigMap(Map<String, GwClassLoaderConfig> configMap) {
		this.configMap = configMap;
	}
	
	@ManagedOperation(description = "reload")
	public void refresh() {
		for(String contextId : contextMap.keySet()){
			contextMap.get(contextId).reload();
		}
	}
	
	public void setCamelContext(CamelContext camelContext) {
		
	}
	
    public ApplicationContext getApplicationContext(String contextId) {
        return contextMap.get(contextId);
    }

	public CamelContext getCamelContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

    private static final String mBeanType = "GwContextFacade";
    private static final String mBeanName = "GwContextFacadeLocalImpl";

	public void contextStarted() {
	    if (JMXUtil.isInstanced()) {
//	        JMXUtil.registerMbean(this, mBeanType, mBeanName);
	    }
	}

	
}
