package com.skcc.gw.classloader.loader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GwResourcesClassLoaderDelegator extends ClassLoader{
	private Logger logger = LoggerFactory.getLogger(GwResourcesClassLoaderDelegator.class);
	private ResourceStore [] stores;
	
	public GwResourcesClassLoaderDelegator(ClassLoader parent, ResourceStore[] stores){
		super(parent);
		this.stores = stores;
	}

    private Class fastFindClass(final String name) {
        
        if (stores != null ) {
            for (int i = 0; i < stores.length; i++) {
                final ResourceStore store = stores[i];
                final byte[] clazzBytes = store.read(ConversionUtils.convertClassToResourcePath(name));
                if (clazzBytes != null) {
                	logger.debug(this.getClass().getClassLoader() + " found class: " + name  + " (" + clazzBytes.length + " bytes)");
                    return defineClass(name, clazzBytes, 0, clazzBytes.length);
                }            
            }
        }
 
        return null;            
    }
    
    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class clazz = findLoadedClass(name);

        if (clazz == null) {
            final ClassLoader parent = getParent();
            if (parent != null) {
                try {
                    clazz = parent.loadClass(name);
                } catch (ClassNotFoundException e) {
                    logger.debug(name + " does not exists in main classloader.");
                    clazz = null;
                }
            }
        }
            
        if (clazz == null) {
            clazz = fastFindClass(name);
            
            if (clazz == null) {
                throw new ClassNotFoundException(name);
            } else {
                logger.debug(this.getClass().getClassLoader() + " loaded from store: " + name);
            }
        }

        if (resolve) {
            resolveClass(clazz);
        }

        return clazz;
    }
 
    protected Class findClass( final String name ) throws ClassNotFoundException {
        final Class clazz = fastFindClass(name);
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
    	return super.getResources(name);
    }
    
    @Override
    public URL getResource(String name) {
    	URL url = findResource(name);
    	
    	if (url == null) {
    		url = super.getResource(name);
    	}
    	
    	return url; 
    }
    
    @Override
	protected Enumeration<URL> findResources(String name) throws IOException {
    	URL findResource = findResource(name);
    	
    	URL[] urls = null;
    	if (findResource != null) {
    		urls = new URL[]{ findResource };
    	} else {
    		urls = new URL[0];
    	}
    	
		return new Enum(urls);
	}
    
    class Enum implements Enumeration<URL> {

    	private URL[] urls;
    	private int index = 0;
    	public Enum(URL[] urls) {
    		this.urls = urls;
		}
    	
    	public boolean hasMoreElements() {
			if (urls.length > index) {
				return true;
			}
			return false;
		}

		public URL nextElement() {
			
			return urls[index++];
		}
    }
}
