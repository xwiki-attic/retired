
package com.xpn.xwiki.wiked.test.xwt;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.xpn.xwiki.wiked.internal.xwt.XWTBuilder;

public class ModifyListenerTest extends TestCase {

	private XWTBuilder builder;
	
	private static final DocumentBuilderFactory DBF = 
		DocumentBuilderFactory.newInstance();
	
	protected void setUp() throws Exception {
		this.builder = new XWTBuilder(getClass().getClassLoader(),
			getClass().getResourceAsStream("ModifyListenerTest.map"));
	}

	public void testTextFactory() throws Exception {
		InputStream stream = getClass().getResourceAsStream("ModifyListenerTest-1.xml");
		Composite parent = new Composite(new Shell(), SWT.NONE);
		Composite c = this.builder.create(parent, stream);
		assertNotNull(c);
        Label label = (Label)builder.getObjectRegistry().getObject("label1");
        assertNotNull(label);
        assertEquals("xxx", label.getText());
	}

}
