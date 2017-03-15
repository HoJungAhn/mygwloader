package com.skcc.gw.classloader.loader.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GwFileResourceStore implements GwResouceStore{
	private Logger logger = LoggerFactory.getLogger(GwFileResourceStore.class);
	private File rootPath;
	
	public GwFileResourceStore(String rootPath){
		this.rootPath = new File(rootPath);
	}
	
	public GwFileResourceStore(File rootPath){
		this.rootPath = rootPath;
	}
	
	public byte[] read(String pResourceName) {
        InputStream is = null;
        try {
            is = new FileInputStream(getFile(pResourceName));
            final byte[] data = IOUtils.toByteArray(is);
            return data;
        } catch (Exception e) {
//        	logger.error("can not read file :" + pResourceName);
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }
	}

	public void remove(String pResourceName) {
		getFile(pResourceName).delete();
	}

	public void write(String pResourceName, byte[] pResourceData) {
		FileOutputStream fout = null;
		
		try{
			File file = getFile(pResourceName);
			if(file.getParentFile().exists() == false){
				file.getParentFile().mkdirs();
			}
			fout = new FileOutputStream(file);
			IOUtils.write(pResourceData, fout);
		}catch(IOException e){
//			logger.error("can not write file :" + pResourceName);
		}finally{
			IOUtils.closeQuietly(fout);
		}
		
	}
	
    private File getFile( String pResourceName ) {
        final String fileName = pResourceName.replace('/', File.separatorChar);
        return new File(rootPath, fileName);
    }
    
    public boolean exist(){
    	return rootPath.exists();
    }

	public String getRoot() {
		return rootPath.getPath();
	}

}
