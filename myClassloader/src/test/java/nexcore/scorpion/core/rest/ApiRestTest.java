package nexcore.scorpion.core.rest;

import static org.junit.Assert.*;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.ObjectHelper;
import org.junit.Before;
import org.junit.Test;

public class ApiRestTest extends CamelTestSupport{
	private CamelContext context;
	
//	@Before
	public void init() throws Exception{
		context = new DefaultCamelContext();
		RouteBuilder builder = new RouteBuilder(){

			@Override
			public void configure() throws Exception {
				restConfiguration().component("restlet").host("localhost").port(28080).bindingMode(RestBindingMode.auto);
				// use the rest DSL to define the rest services
				rest("/")
					.get("/{service}/{id}").consumes("application/json").to("direct:hello")
//					.get("/bye/{id}").consumes("application/json").to("direct:bye")
//					.get("/bye").consumes("application/json").to("direct:bye")
					.post("/bye").to("mock:update");

				from("direct:hello").transform().simple("Hello World ${header.service} ${header.id}");
				from("direct:bye").transform().simple("Bye World ${header.id}");
			}
			
		};
		context.addRoutes(builder);
		context.start();	
		
		Thread.sleep(10000000);
	}
	
//	@Override
//	protected AbstractApplicationContext createApplicationContext() {
//		return new ClassPathXmlApplicationContext("");
//	}

	@Test
	public void test() throws Exception {
		String remaining = ":tset?name";
		System.out.println(ObjectHelper.before(remaining, ":"));
		System.out.println(ObjectHelper.after(remaining, ":"));
		
	}
}
