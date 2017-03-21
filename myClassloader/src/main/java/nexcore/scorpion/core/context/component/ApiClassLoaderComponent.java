package nexcore.scorpion.core.context.component;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.bean.BeanInfo;
import org.apache.camel.component.bean.BeanInfoCacheKey;
import org.apache.camel.impl.UriEndpointComponent;
import org.apache.camel.util.LRUSoftCache;
import org.apache.camel.util.ObjectHelper;
import org.restlet.engine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import nexcore.scorpion.core.context.ApiContextFacade;

public class ApiClassLoaderComponent extends UriEndpointComponent{
	
	private Logger logger = LoggerFactory.getLogger(ApiClassLoaderComponent.class);
    // use an internal soft cache for BeanInfo as they are costly to introspect
    // for example the bean language using OGNL expression runs much faster reusing the BeanInfo from this cache
    private final LRUSoftCache<BeanInfoCacheKey, BeanInfo> cache = new LRUSoftCache<BeanInfoCacheKey, BeanInfo>(1000);

    @Autowired
    private ApiContextFacade contextFacade;
    
    public ApiClassLoaderComponent() {
        super(ApiClassEndpoint.class);
    }
    
	public ApiClassLoaderComponent(CamelContext context, Class<? extends Endpoint> endpointClass) {
		super(context, endpointClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		ApiClassEndpoint endpoint = new ApiClassEndpoint(uri, this);
		endpoint.setContextFacade(contextFacade);
		
		// resolved context name
		String contextId = ObjectHelper.before(remaining, ":");
		// resolved bean name
		String beanName = ObjectHelper.after(remaining, ":");
		
		endpoint.setContextId(contextId);
        endpoint.setBeanName(beanName);
        
        if(!StringUtils.isNullOrEmpty(contextId) && contextFacade.getApplicationContext(contextId) == null){
        	throw new Exception("context id : " + contextId + " is not exist");
        }
        
        setProperties(endpoint, parameters);
        // any remaining parameters are parameters for the bean
        endpoint.setParameters(parameters);
        return endpoint;
	}

    BeanInfo getBeanInfoFromCache(BeanInfoCacheKey key) {
        return cache.get(key);
    }

    void addBeanInfoToCache(BeanInfoCacheKey key, BeanInfo beanInfo) {
        cache.put(key, beanInfo);
    }

    @Override
    protected void doShutdown() throws Exception {
        if (logger.isDebugEnabled()) {
        	logger.debug("Clearing BeanInfo cache[size={}, hits={}, misses={}, evicted={}]", new Object[]{cache.size(), cache.getHits(), cache.getMisses(), cache.getEvicted()});
        }
        cache.clear();
    }
}
