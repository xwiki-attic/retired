
package com.xpn.xwiki.wiked.test.xwt;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.w3c.dom.Document;

import com.xpn.xwiki.wiked.internal.xwt.cf.SWTPropertyReader;

public class SWTPropertyReaderTest extends TestCase {

	private static final DocumentBuilderFactory DBF = 
		DocumentBuilderFactory.newInstance();

	public void testSWTFlags() throws Exception {
		InputStream stream = getClass().getResourceAsStream("SWTPropertyReaderTest-1.xml");
		Document doc = DBF.newDocumentBuilder().parse(stream);
		assertEquals(SWT.NONE|SWT.RESIZE, 
		    SWTPropertyReader.getDefault().parse(doc.getDocumentElement()));
	}
	
	public void testBooleans() throws Exception {
		assertEquals(Boolean.TRUE, 
		    SWTPropertyReader.getDefault().parseKey("true"));
	}
}
