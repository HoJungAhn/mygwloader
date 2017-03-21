package nexcore.scorpion.core.context.component;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.component.bean.BeanProcessor;
import org.apache.camel.impl.DefaultAsyncProducer;

public class ApiClassLoaderProducer extends DefaultAsyncProducer{
	
    private final BeanProcessor processor;

    public ApiClassLoaderProducer(ApiClassEndpoint endpoint, BeanProcessor processor) {
        super(endpoint);
        this.processor = processor;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            processor.process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }
        callback.done(true);
        return true;
    }
}
