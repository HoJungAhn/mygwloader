package nexcore.scorpion.core.context;

import org.springframework.context.ApplicationContext;

public interface ApiContextFacade {
	 ApplicationContext getApplicationContext(String contextId);
}
