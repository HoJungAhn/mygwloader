package com.skcc.gw.classloader.loader;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleInstantiationStrategy;

public class GwClassReloadInstantiationStrategy extends SimpleInstantiationStrategy {
	public Object instantiate(RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) {
		if (beanDefinition.getMethodOverrides().isEmpty()) {
			Class clazz = beanDefinition.getBeanClass();
			if (clazz.isInterface())
				throw new BeanInstantiationException(clazz, "Specified class is an interface");
			try {
				java.lang.reflect.Constructor constructor = clazz.getDeclaredConstructor((Class[]) null);
				return BeanUtils.instantiateClass(constructor, null);
			} catch (Exception ex) {
				throw new BeanInstantiationException(clazz, "No default constructor found", ex);
			}
		} else {
			return instantiateWithMethodInjection(beanDefinition, beanName, owner);
		}
	}
}
