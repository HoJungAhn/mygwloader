package com.skcc.gw.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.OrderComparator;

 
/**
* 업무 그룹명 : nexcore.gemini.commons.lazyinit
* 서브 업무명 : ContextStartedAwareSupport.java
* 작성자 : alcava00
* 작성일 : 2012. 6. 21.
* 설 명 : IContextStartedAware 를 구현한 Class에서 ContextStarted event를 Notify 하기 위한 Class 
*         그런
*       Springbean으로 등록하여 사용함  
*       <bean id="contextStartedAwareSupport"  class="nexcore.gemini.commons.lazyinit.ContextStartedAwareSupport"/>
* @see nexcore.gemini.commons.lazyinit.IContextStartedAware 
*/
public class ContextStartedAwareSupport implements Service, CamelContextAware, ApplicationListener<ContextRefreshedEvent> {
//    public class ContextStartedAwareSupport implements StartupListener, Service, CamelContextAware {
    private static final transient Logger LOG = LoggerFactory.getLogger(ContextStartedAwareSupport.class);

    /**
    * camelContext
    */
    private CamelContext camelContext;

    /**
    * @param context
    * @param alreadyStarted
    * @throws Exception
    */
    public void onCamelContextStarted(CamelContext context, boolean alreadyStarted) throws Exception {
    	LOG.info("Context is Started *******************************");
    	Map<String, IContextStartedAware> lazyInits = camelContext.getRegistry().lookupByType(IContextStartedAware.class);

    	List<IContextStartedAware> lazyInitList = toSortedList(lazyInits);
    	for (IContextStartedAware lazyInit : lazyInitList) {
    		LOG.info("Lazy INIT START >> " + lazyInit.getClass().getName());
    		lazyInit.contextStarted();
    	}
    }
    
    private List<IContextStartedAware> toSortedList(Map<String, IContextStartedAware> map) {
    	Iterator<String> iter = map.keySet().iterator();
    	List<IContextStartedAware> list = new ArrayList<IContextStartedAware>();
    	while (iter.hasNext()) {
    		list.add(map.get(iter.next()));
    	}
    	
    	OrderComparator.sort(list);
    	
    	return list;
    }

    public void start() throws Exception {
    	LOG.debug("ContextStartedAwareSupport SERVICE START");
    }

    public void stop() throws Exception {
    	LOG.debug("ContextStartedAwareSupport SERVICE STOP");
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
//        try {
//            camelContext.addService(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
      Map<String, IContextStartedAware> lazyInits = camelContext.getRegistry().lookupByType(IContextStartedAware.class);
 
      List<IContextStartedAware> lazyInitList = toSortedList(lazyInits);
      for (IContextStartedAware lazyInit : lazyInitList) {
          LOG.info("Lazy INIT START >> " + lazyInit.getClass().getName());
          lazyInit.contextStarted();
      }
    }

    public CamelContext getCamelContext() {
        return this.camelContext;
    }
}
