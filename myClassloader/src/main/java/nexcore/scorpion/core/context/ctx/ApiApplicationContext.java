package nexcore.scorpion.core.context.ctx;

import org.springframework.context.ApplicationContext;

import nexcore.scorpion.core.context.config.ApiClassLoaderConfig;
import nexcore.scorpion.core.context.loader.ApiReloadClassLoader;
import nexcore.scorpion.core.context.loader.store.ApiResouceStore;

public interface ApiApplicationContext extends ApplicationContext{
	ApiReloadClassLoader getReloadClassloader();
	void setReloadClassloader(ApiReloadClassLoader reloadClassloader);
	void setReloadContext(ApiClassLoaderConfig config, ApiResouceStore store);
	
	void start();
	void stop();
	void reload();
}
