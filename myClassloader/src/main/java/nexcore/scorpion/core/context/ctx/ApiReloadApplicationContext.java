package nexcore.scorpion.core.context.ctx;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import nexcore.scorpion.core.context.config.ApiClassLoaderConfig;
import nexcore.scorpion.core.context.loader.ApiClassReloadBeanFactory;
import nexcore.scorpion.core.context.loader.ApiClassReloadInstantiationStrategy;
import nexcore.scorpion.core.context.loader.ApiReloadClassLoader;
import nexcore.scorpion.core.context.loader.store.ApiResouceStore;

public class ApiReloadApplicationContext extends ClassPathXmlApplicationContext implements ApiApplicationContext{
	private Logger logger = LoggerFactory.getLogger(ApiReloadApplicationContext.class);
	
	private ApiReloadClassLoader reloadClassloader;
	private DefaultListableBeanFactory beanFactory;
	
	private ApiClassLoaderConfig config;

	public ApiReloadApplicationContext(ApplicationContext pContext){
		super(pContext);
		setReloadClassloader(new ApiReloadClassLoader(pContext.getClassLoader()));
	}
	
	public ApiReloadClassLoader getReloadClassloader() {
		return reloadClassloader;
	}

	public void setReloadClassloader(ApiReloadClassLoader reloadClassloader) {
		super.setClassLoader(reloadClassloader);
		this.reloadClassloader = reloadClassloader;
	}
	
	public void setReloadContext(ApiClassLoaderConfig config, ApiResouceStore store){
		setConfigLocation(config.getContextConfigXmlLocation());
		setId(config.getContextId());
		reloadClassloader.setAcceptClasses(config.getAcceptPatterns());
		reloadClassloader.addResourceStore(store);
		
		setReloadClassloader(reloadClassloader);
		
		String contextClassLocation = config.getContextClassLocation();
		Set<Pattern> acceptPatterns = config.getAcceptPatterns();
		// TODO :  setReloadClassloader 호출 하는거 확인.
//		setReloadClassloader(reloadClassloader);
	}
	
	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		AbstractRefreshableApplicationContext parent = (AbstractRefreshableApplicationContext) getParent();
		beanFactory = new ApiClassReloadBeanFactory(parent.getBeanFactory());
		beanFactory.setInstantiationStrategy(new ApiClassReloadInstantiationStrategy());
		beanFactory.setBeanClassLoader(reloadClassloader);
		// TODO : context aware 추가내용 삭제 
//		beanFactory.addBeanPostProcessor(new BeanPostProcessor(){
//
//			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//				return bean;
//			}
//
//			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//				if (bean instanceof BizContextBeanHandlerAware && beanHandler != null) {
//					((BizContextBeanHandlerAware) bean).setBeanHandler(beanHandler);
//				}
//				return bean;
//			}
//
//		});
		return beanFactory;
		
	}

	public synchronized void reload() {
		if(reloadClassloader != null){
			reloadClassloader.reload();
		}
		
		long t1 = System.currentTimeMillis();
		super.refresh();

		long t2 = System.currentTimeMillis();
		if(logger.isInfoEnabled()){
			logger.info("*************************************************************************") ;
			logger.info("ApplicationContext is STARTED >>> ID : {}", getId()) ;
			logger.info("Reloading Time : {}", t2 - t1);
			logger.info("Created Controlleres : ");
			
			Map<String,Object> tempMap = getBeansWithAnnotation(Controller.class);
			Iterator<String> it = tempMap.keySet().iterator();
			while(it.hasNext()) {
				String beanId = it.next();
				logger.info("BEAN ID {} ,Class {}",beanId, getBean(beanId).toString() );
			}
			logger.info("*************************************************************************") ;
			logger.info("Created Services : ");
			
			tempMap = getBeansWithAnnotation(Service.class);
            it = tempMap.keySet().iterator();
            while(it.hasNext()) {
                String beanId = it.next();
                logger.info("BEAN ID {} ,Class {}",beanId, getBean(beanId).toString() );
            }
            logger.info("*************************************************************************") ;
		}
	}
	
	
	
}
