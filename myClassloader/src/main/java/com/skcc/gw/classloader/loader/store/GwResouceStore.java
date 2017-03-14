package com.skcc.gw.classloader.loader.store;

import org.apache.commons.jci.stores.ResourceStore;

public interface GwResouceStore extends ResourceStore {
	boolean exist();
	String getRoot();
}
