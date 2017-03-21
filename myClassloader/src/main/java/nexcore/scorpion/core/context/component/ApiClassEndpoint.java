package nexcore.scorpion.core.context.component;

import java.util.Map;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.bean.BeanHolder;
import org.apache.camel.component.bean.BeanProcessor;
import org.apache.camel.component.bean.RegistryBean;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.springframework.util.StringUtils;

import nexcore.scorpion.core.context.ApiContextFacade;

/**
 * api context component
 * 
 * @author ahnhojung
 *
 */

@UriEndpoint(scheme = "apictx", title = "Api CtxBean", syntax = "apictx:ctx:beanName", producerOnly = true, label = "core,java")
public class ApiClassEndpoint extends DefaultEndpoint{
    private transient BeanHolder beanHolder;
    private transient BeanProcessor processor;
    
    @UriPath(description = "Sets the name of the context to invoke")
    private String contextId;

	@UriPath(description = "Sets the name of the bean to invoke") @Metadata(required = "true")
    private String beanName;
    
    @UriParam(description = "Sets the name of the method to invoke on the bean")
    private String method;
    
    @UriParam(description = "If enabled, Camel will cache the result of the first Registry look-up. Cache can be enabled if the bean in the Registry is defined as a singleton scope.")
    private boolean cache;
    
    private ApiContextFacade contextFacade;

	@UriParam(label = "advanced", description = "Used for configuring additional properties on the bean")
    private Map<String, Object> parameters;

    public ApiClassEndpoint() {
        setExchangePattern(ExchangePattern.InOut);
    }

    public ApiClassEndpoint(String endpointUri, Component component, BeanProcessor processor) {
        super(endpointUri, component);
        this.processor = processor;
        setExchangePattern(ExchangePattern.InOut);
    }

    public ApiClassEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
        setExchangePattern(ExchangePattern.InOut);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ApiClassLoaderProducer(this, processor);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("You cannot consume from a bean endpoint");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public BeanProcessor getProcessor() {
        return processor;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (processor == null) {
            BeanHolder holder = getBeanHolder();
            if (holder == null) {
                RegistryBean registryBean;
                if(!StringUtils.isEmpty(getContextId())){
                	registryBean = new RegistryBean(new ApiClassRegistry(getContextId(), contextFacade.getApplicationContext(getContextId())), getCamelContext(), beanName);
                }else{
                	registryBean = new RegistryBean(getCamelContext(), beanName);
                }
                if (cache) {
                    holder = registryBean.createCacheHolder();
                } else {
                    holder = registryBean;
                }
            }
            processor = new BeanProcessor(holder);
            if (method != null) {
                processor.setMethod(method);
            }
            processor.setMultiParameterArray(false);
            if (parameters != null) {
                setProperties(processor, parameters);
            }
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        // noop
    }

    // Properties
    //-------------------------------------------------------------------------

    public ApiContextFacade getContextFacade() {
		return contextFacade;
	}

	public void setContextFacade(ApiContextFacade contextFacade) {
		this.contextFacade = contextFacade;
	}
    
    public String getContextId() {
		return contextId;
	}
    
    public void setContextId(String contextId){
    	this.contextId = contextId;
    }
    
    public String getBeanName() {
        return beanName;
    }

    /**
     * Sets the name of the bean to invoke
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public boolean isCache() {
        return cache;
    }

    /**
     * If enabled, Camel will cache the result of the first Registry look-up.
     * Cache can be enabled if the bean in the Registry is defined as a singleton scope.
     */
    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public String getMethod() {
        return method;
    }

    /**
     * Sets the name of the method to invoke on the bean
     */
    public void setMethod(String method) {
        this.method = method;
    }

    public BeanHolder getBeanHolder() {
        return beanHolder;
    }

    public void setBeanHolder(BeanHolder beanHolder) {
        this.beanHolder = beanHolder;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Used for configuring additional properties on the bean
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    @Override
    protected String createEndpointUri() {
        return "apictx:" + getContextId() + ":" + getBeanName() + (method != null ? "?method=" + method : "");
    }

}
