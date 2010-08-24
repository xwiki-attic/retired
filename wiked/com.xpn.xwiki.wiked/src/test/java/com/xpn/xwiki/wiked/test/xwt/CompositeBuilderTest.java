
package com.xpn.xwiki.wiked.test.xwt;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilder;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilderContext;

public class CompositeBuilderTest extends TestCase {

	private XWTBuilder builder;
	
	private static final DocumentBuilderFactory DBF = 
		DocumentBuilderFactory.newInstance();
	
	protected void setUp() throws Exception {
		this.builder = new XWTBuilder();
		this.builder.registerFactory("test1", new Test1Factory());
	}

	public void testSimpleFactory() throws Exception {
		InputStream stream = getClass().getResourceAsStream("CompositeBuilderTest-1.xml");
		Document doc = DBF.newDocumentBuilder().parse(stream);
		Composite c = this.builder.create(null, doc.getDocumentElement());
		assertNull(c);
	}

	public static class Test1Factory extends AbstractSWTFactory {
        public void setBuilderContext(XWTBuilderContext ctx) {
        }
		public Object createObject(Widget parent, Element element) {
			return null;
		}
	}
	
}
