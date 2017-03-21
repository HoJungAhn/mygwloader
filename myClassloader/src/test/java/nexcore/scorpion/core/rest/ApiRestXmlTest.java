package nexcore.scorpion.core.rest;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApiRestXmlTest extends CamelSpringTestSupport{

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("nexcore/scorpion/core/rest/ApiRestXmlTest.xml");
	}
	@Test
	public void test() throws Exception {
		
	}

}
