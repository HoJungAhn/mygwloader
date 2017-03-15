package com.skcc.gw.classloader;

import java.lang.reflect.Method;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GwContextFacadeLocalImplTest extends CamelSpringTestSupport {
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("com/skcc/gw/classloader/GwContextFacadeLocalImplTest.xml");
	}
	
	@Test
	public void test() throws Exception {
		
		while(true){
			try {
				Thread.sleep(4000);
				GwContextFacade contextFacade = context.getRegistry().lookupByNameAndType("contextFacade", GwContextFacade.class);
				ApplicationContext context = contextFacade.getApplicationContext("context1");
				
				for(String name : context.getBeanDefinitionNames()){
					System.out.println("bean : " + name);
				}
				
				Object bean = context.getBean("context1Controller1");
				
				Method method = findMethod(bean, "testController", null);

				method.invoke(bean, null);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	   private Method findMethod(Object obj, String methodName, Class<?>[] paramTypes) throws Exception {
	        
	        if (paramTypes == null || paramTypes.length == 0) {
	            return obj.getClass().getMethod(methodName, paramTypes);
	        }
	        
	        Method[] methods = obj.getClass().getMethods();
	        
	        Class[] methodParamTypes = null;
	        boolean fullMatch = true;
	        boolean isMatch = false;
	        Class superClass = null;
	        
	        for (Method method : methods) {
	            if (method.getName().equals(methodName)) {
	                methodParamTypes = method.getParameterTypes();
	                
	                if (methodParamTypes.length != paramTypes.length) continue;
	                
	                fullMatch = true;
	                for (int i = 0 ; i < methodParamTypes.length ; i++) {
	                    if (methodParamTypes[i] == paramTypes[i]) {
	                        continue;
	                    }
	                    
	                    isMatch = false;

	                    if (methodParamTypes[i].isInterface()) {
	                        // 인터페이스 확인
	                        for (Class intf : paramTypes[i].getInterfaces()) {
	                            if (methodParamTypes[i] == intf) {
	                                isMatch = true;
	                                break;
	                            }
	                        }
	                        
	                    } else {
	                        // 수퍼클래스 확인
	                        superClass = paramTypes[i].getSuperclass();
	                        while (superClass != null && superClass != Object.class) {
//	                      paramTypes[i].get
	                            if (methodParamTypes[i] == superClass) {
	                                isMatch = true;
	                                break;
	                            }
	                            
	                            superClass = paramTypes[i].getSuperclass();
	                        }
	                    }
	                    
	                    if (isMatch) {
	                        continue;
	                    }
	                    
	                    fullMatch = false;
	                    break;
	                }
	                    
	                if (fullMatch) {
	                    return method;
	                }
	            }
	        }
	        
	        return obj.getClass().getMethod(methodName, paramTypes);
	    }



}
