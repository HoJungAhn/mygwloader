package com.skcc.gw.classloader.config;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class GwClassLoaderConfig {
	private String contextId;
	private String contextClassLocation;
	private String contextConfigXmlLocation;
	private String acceptClasses = ".*";

	public String getContextClassLocation() {
		return contextClassLocation;
	}
	public void setContextClassLocation(String contextClassLocation) {
		this.contextClassLocation = contextClassLocation;
	}
	public String getAcceptClasses() {
		return acceptClasses;
	}
	public void setAcceptClasses(String acceptClasses) {
		this.acceptClasses = acceptClasses;
	} 
	
	public Set<Pattern> getAcceptPatterns(){
		String[] splitted = acceptClasses.split(",");
		Set<Pattern> patterns = new HashSet<Pattern>(splitted.length);
		for (String pattern : splitted)
			patterns.add(Pattern.compile(pattern));	
		return patterns;
	}
	
	public String getContextId() {
		return contextId;
	}
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}
	
	public String getContextConfigXmlLocation() {
		return contextConfigXmlLocation;
	}
	public void setContextConfigXmlLocation(String contextConfigXmlLocation) {
		this.contextConfigXmlLocation = contextConfigXmlLocation;
	}
}
