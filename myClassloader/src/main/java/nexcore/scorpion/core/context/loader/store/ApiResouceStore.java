package nexcore.scorpion.core.context.loader.store;

import org.apache.commons.jci.stores.ResourceStore;

public interface ApiResouceStore extends ResourceStore {
	boolean exist();
	String getRoot();
}
