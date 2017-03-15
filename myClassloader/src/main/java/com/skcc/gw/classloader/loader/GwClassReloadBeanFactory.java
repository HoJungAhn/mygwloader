package com.skcc.gw.classloader.loader;

import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * 
 * @author ahnhojung
 *
 */
public class GwClassReloadBeanFactory extends DefaultListableBeanFactory{
	private Logger logger = LoggerFactory.getLogger(GwClassReloadBeanFactory.class);

	/*
	 * TODO : 사용할지 않할지 확인 필요.
	 */
	private XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);
	
	public GwClassReloadBeanFactory(){
		super();
	}
	public GwClassReloadBeanFactory(BeanFactory parentBeanFactory){
		super(parentBeanFactory);
	}
	

	/**
	 * Create a new XmlBeanFactory with the given resource, which must be
	 * parsable using DOM.
	 * 
	 * @param resource
	 *            XML resource to load bean definitions from
	 * @throws BeansException
	 *             in case of loading or parsing errors
	 */
	public GwClassReloadBeanFactory(Resource resource) throws BeansException {
		this(resource, null);
		logger.debug("ClassReloadingBeanFactory(Resource resource)");
	}
	
	/**
	 * Create a new XmlBeanFactory with the given input stream, which must be
	 * parsable using DOM.
	 * 
	 * @param resource
	 *            XML resource to load bean definitions from
	 * @param parentBeanFactory
	 *            parent bean factory
	 * @throws BeansException
	 *             in case of loading or parsing errors
	 */
	public GwClassReloadBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
		super(parentBeanFactory);
		this.reader.loadBeanDefinitions(resource);
	}
	
	@Override
	protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object args[]) {
		Class beanClass = resolveBeanClass(mbd, beanName);
		if (mbd.getFactoryMethodName() != null){
			return instantiateUsingFactoryMethod(beanName, mbd, args);
		}
		
		java.lang.reflect.Constructor ctors[] = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
		
		if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR
				|| mbd.hasConstructorArgumentValues()
				|| !ObjectUtils.isEmpty(args))
			return autowireConstructor(beanName, mbd, ctors, args);
		else
			return instantiateBean(beanName, mbd);
	}
	
	protected Class resolveBeanClass(RootBeanDefinition mbd, String beanName) {
		return resolveBeanClass(mbd, beanName, null);
	}
	
	@Override
	protected Class resolveBeanClass(RootBeanDefinition mbd, String beanName, Class typesToMatch[]) {
		try {
			if (typesToMatch != null) {
				ClassLoader tempClassLoader = getTempClassLoader();
				if (tempClassLoader != null) {
					if (tempClassLoader instanceof DecoratingClassLoader) {
						DecoratingClassLoader dcl = (DecoratingClassLoader) tempClassLoader;
						for (int i = 0; i < typesToMatch.length; i++)
							dcl.excludeClass(typesToMatch[i].getName());

					}
					String className = mbd.getBeanClassName();
					return className == null ? null : ClassUtils.forName(className, tempClassLoader);
				}
			}
		} catch (ClassNotFoundException ex) {
			throw new CannotLoadBeanClassException(
					mbd.getResourceDescription(), beanName,
					mbd.getBeanClassName(), ex);
		} catch (LinkageError err) {
			throw new CannotLoadBeanClassException(
					mbd.getResourceDescription(), beanName,
					mbd.getBeanClassName(), err);
		}
		try {
			return mbd.resolveBeanClass(getBeanClassLoader());
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException occured.", e);
			return null;
		}
	}
	
	@Override
	public void preInstantiateSingletons() throws BeansException{
	    List<Throwable> throwableList = new ArrayList<Throwable>();
	    
        for (String beanName : getBeanDefinitionNames()) {
            RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
            if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                try {
                    if (isFactoryBean(beanName)) {
                        final FactoryBean factory = (FactoryBean) getBean(FACTORY_BEAN_PREFIX + beanName);
                        boolean isEagerInit;
                        if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                            isEagerInit = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                                public Boolean run() {
                                    return ((SmartFactoryBean) factory).isEagerInit();
                                }
                            }, getAccessControlContext());
                        }
                        else {
                            isEagerInit = factory instanceof SmartFactoryBean && ((SmartFactoryBean) factory).isEagerInit(); 
                        }
                        if (isEagerInit) {
                            getBean(beanName);
                        }
                    }
                    else {
                        getBean(beanName);
                    }
                } catch (Exception e) {
                    throwableList.add(e);
                } catch (Error e) {
                    throwableList.add(e);
                }
            }
        }
        
        for (Throwable t : throwableList) {
        	logger.error(t.toString());
        }
	}
	
	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
	    Set<String> beanNames = new LinkedHashSet<String>(getBeanDefinitionCount());
        beanNames.addAll(Arrays.asList(getBeanDefinitionNames()));
        beanNames.addAll(Arrays.asList(getSingletonNames()));
        Map<String, Object> results = new LinkedHashMap<String, Object>();
        for (String beanName : beanNames) {
            if (findAnnotationOnBean(beanName, annotationType) != null) {
                try {
                    results.put(beanName, getBean(beanName));
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return results;
	}
	
	@Override
	protected <T> T doGetBean(String name, Class<T> requiredType, Object[] args, boolean typeCheckOnly) throws BeansException {
	    T bean = null;
	    try {
	        bean = super.doGetBean(name, requiredType, args, typeCheckOnly);
	    } catch (NoClassDefFoundError e) {
//	        e.printStackTrace();
//	        throw new NoClassDefFoundException(e.getMessage(), e);
	    	logger.error(e.getMessage(), e);
	    	throw e;
	    }
	    return bean;
	}
}
