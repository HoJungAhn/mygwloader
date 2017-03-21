package nexcore.scorpion.core.context.component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.NoSuchBeanException;
import org.apache.camel.spi.Registry;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class ApiClassRegistry implements Registry{
	private ApplicationContext applicationContext;
	private String contextId;
	
	public ApiClassRegistry(String contextId, ApplicationContext applicationContext) {
		this.contextId = contextId;
		this.applicationContext = applicationContext;
	}
	
    @Override
    public <T> T lookupByNameAndType(String name, Class<T> type) {
        Object answer;
        try {
            answer = applicationContext.getBean(name, type);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        } catch (BeanNotOfRequiredTypeException e) {
            return null;
        }

        // just to be safe
        if (answer == null) {
            return null;
        }

        try {
            return type.cast(answer);
        } catch (Throwable e) {
            String msg = "Found bean: " + name + " in ApplicationContext: " + applicationContext
                    + " of type: " + answer.getClass().getName() + " expected type was: " + type;
            throw new NoSuchBeanException(name, msg, e);
        }
    }

    @Override
    public Object lookupByName(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    @Override
    public <T> Set<T> findByType(Class<T> type) {
        Map<String, T> map = findByTypeWithName(type);
        return new HashSet<T>(map.values());
    }

    @Override
    public <T> Map<String, T> findByTypeWithName(Class<T> type) {
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, type);
    }

    @Override
    public Object lookup(String name) {
        return lookupByName(name);
    }

    @Override
    public <T> T lookup(String name, Class<T> type) {
        return lookupByNameAndType(name, type);
    }

    @Override
    public <T> Map<String, T> lookupByType(Class<T> type) {
        return findByTypeWithName(type);
    }


	public ApplicationContext getContext() {
		return applicationContext;
	}

	public String getContextId() {
		return contextId;
	}

	
}
