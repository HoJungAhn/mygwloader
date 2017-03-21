package nexcore.scorpion.core.context.component;

import org.apache.camel.Exchange;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApiClassLoaderComponentTest  extends CamelSpringTestSupport{
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("nexcore/scorpion/core/context/component/ApiClassLoaderComponentTest.xml");
	}

	@Test
	public void test() throws Exception {
		Exchange exchange = context.getEndpoint("direct:start").createExchange();
		template.send("direct:start", exchange);
	}

}
