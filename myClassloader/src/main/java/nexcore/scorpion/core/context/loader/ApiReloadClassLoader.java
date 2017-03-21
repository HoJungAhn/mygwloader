package nexcore.scorpion.core.context.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.jci.stores.ResourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nexcore.scorpion.core.context.loader.store.ApiResouceStore;

public class ApiReloadClassLoader extends ClassLoader{
	private Logger logger = LoggerFactory.getLogger(ApiReloadClassLoader.class);
	
	private Set<Pattern> acceptClasses = Collections.emptySet();
	private ApiResouceStore[] stores = new ApiResouceStore[0];
	
	private ApiResourcesClassLoaderDelegator delegator;
	
	// class loader reload 시 최초 parent class loader를 사용하기 위해 저
	private ClassLoader parent;
	
	public ApiReloadClassLoader(ClassLoader parent){
		super(parent);
		this.parent = parent;
		this.delegator = new ApiResourcesClassLoaderDelegator(parent, this.stores, acceptClasses);
	}
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return delegator.getResources(name);
	}

	@Override
	public URL getResource(String name) {
		return delegator.getResource(name);
	}
	
	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		return delegator.findResources(name);
	}
	
	@Override
	protected URL findResource(String name) {
		return delegator.findResource(name);
	}

	public void setAcceptClasses(Set<Pattern> acceptPatterns) {
		this.acceptClasses = acceptPatterns;	
	}
	
	public boolean addResourceStore(ApiResouceStore store){
		try {
			final int n = stores.length;
			final ApiResouceStore[] newStores = new ApiResouceStore[n + 1];
			System.arraycopy(stores, 0, newStores, 1, n);
			newStores[0] = store;
			stores = newStores;
			delegator = new ApiResourcesClassLoaderDelegator(parent, stores, acceptClasses);
			return true;
		} catch (final Exception e) {
			logger.error("could not add resource store " + store);
		}
		return false;
	}

	public boolean removeResourceStore(final ResourceStore pStore) {
		final int n = stores.length;
		int point = 0;

		// FIXME: this should be improved with a Map
		// find the pStore and index position with var i
		while ((point < n) && (stores[point] != pStore)) {
			point++;
		}

		// pStore was not found
		if (point == n) {
			return false;
		}

		// if stores length > 1 then array copy old values, else create new
		// empty store
		final ApiResouceStore[] newStores = new ApiResouceStore[n - 1];
		if (point > 0) {
			System.arraycopy(stores, 0, newStores, 0, point);
		}
		if (point < n - 1) {
			System.arraycopy(stores, point + 1, newStores, point, (n - point - 1));
		}

		stores = newStores;
		delegator = new ApiResourcesClassLoaderDelegator(parent, stores, acceptClasses);
		return true;
	}

	public void reload() {
		logger.debug("reloading : " + stores.length);
		delegator = new ApiResourcesClassLoaderDelegator(parent, stores, acceptClasses);
	}
	
	@Override
	public void clearAssertionStatus() {
		delegator.clearAssertionStatus();
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		return delegator.getResourceAsStream(name);
	}

	@Override
	public Class loadClass(String name) throws ClassNotFoundException {
		return delegator.isAccepted(name) ? delegator.loadClass(name) : parent.loadClass(name);
	}

	@Override
	public void setClassAssertionStatus(String className, boolean enabled) {
		delegator.setClassAssertionStatus(className, enabled);
	}

	@Override
	public void setDefaultAssertionStatus(boolean enabled) {
		delegator.setDefaultAssertionStatus(enabled);
	}
	
	@Override
	public void setPackageAssertionStatus(String packageName, boolean enabled) {
		delegator.setPackageAssertionStatus(packageName, enabled);
	}


}
