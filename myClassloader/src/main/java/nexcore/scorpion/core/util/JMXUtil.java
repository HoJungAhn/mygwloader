package nexcore.scorpion.core.util;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.management.DefaultManagementNamingStrategy;
import org.apache.camel.spi.ManagementStrategy;

public class JMXUtil implements CamelContextAware {
	private static final org.slf4j.Logger logger=org.slf4j.LoggerFactory.getLogger(JMXUtil.class); 

    // TODO spring에서 로딩 되는 순서에 따라 오류가 발생 할 수 있음으로 수정 요망 이석
    /**
     * Spring 등록시 아래와 같이 등록하면 strategy 가 초기화 된다.
     * <bean id="jmxutil" class="nexcore.gemini.commons.util.JMXUtil" />
     */

    private static CamelContext context;

    /** jmx domain name */
    private static String domainName;

    private static ManagementStrategy strategy;

    /**
     * Camel ManagementAgent를 이용하여 JMX모듈을 등록한다.
     * 
     * @param obj
     *            JMX로 관리될 객체
     * @param mbeanType
     * @param mbeanName
     */
    public static void registerMbean(Object obj, String mbeanType, String mbeanName) {
        if (domainName == null) {
            strategy = context.getManagementStrategy();
            if (strategy.getManagementNamingStrategy() instanceof DefaultManagementNamingStrategy) {
                domainName = ((DefaultManagementNamingStrategy) strategy
                            .getManagementNamingStrategy()).getDomainName();
            }
        }

        try {
            strategy.getManagementAgent().register(obj, getObjectName(domainName, mbeanType, mbeanName));
        } catch (Exception e) {
        	logger.error(e.toString(), e);
        }
    }

    public static void registerMbean(ManagementStrategy _strategy, Object obj, String mbeanType, String mbeanName) {
        String defaultDomain = "nexcore.gemini";
        try {
            _strategy.getManagementAgent().register(obj, getObjectName(defaultDomain, mbeanType, mbeanName));
        } catch (Exception e) {
        	logger.error(e.toString(), e);
        }

    }

    /**
     * MBean서버 객체를 반환한다.
     */
    public static MBeanServer getMBeanServer() {
        if (domainName == null) {
            strategy = context.getManagementStrategy();
            if (strategy.getManagementNamingStrategy() instanceof DefaultManagementNamingStrategy) {
                domainName = ((DefaultManagementNamingStrategy) strategy
                        .getManagementNamingStrategy()).getDomainName();
            }
        }
        return strategy.getManagementAgent().getMBeanServer();
    }

    private static ObjectName getObjectName(String domainName, String type,
                                            String id) throws MalformedObjectNameException,
            NullPointerException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(domainName).append(":");
        buffer.append("type=" + type + ",");
        buffer.append("name=").append(id);
        return new ObjectName(buffer.toString());
    }
    
    public static boolean isInstanced() {
        return context != null;
    }

    public void setCamelContext(CamelContext camelContext) {
        context = camelContext;
        strategy = context.getManagementStrategy();
    }

    public CamelContext getCamelContext() {
        return context;
    }

}