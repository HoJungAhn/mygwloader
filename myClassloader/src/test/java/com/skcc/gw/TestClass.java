package com.skcc.gw;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class TestClass extends CamelTestSupport {

	@Test
	public void test() throws IOException {
		FileInputStream fin = new FileInputStream("/Users/ahnhojung/temp/test1.csv");
		FileOutputStream fout = new FileOutputStream("/Users/ahnhojung/temp/test1_out.csv");
		IOUtils.copy(fin, fout);
//		IOUtils.closeQuietly(fout);
	}

}
