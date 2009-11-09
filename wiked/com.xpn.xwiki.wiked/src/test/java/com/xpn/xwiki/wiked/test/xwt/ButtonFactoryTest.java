
package com.xpn.xwiki.wiked.test.xwt;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.ObjectRegistry;
import com.xpn.xwiki.wiked.internal.xwt.SimpleObjectRegistry;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilderContext;
import com.xpn.xwiki.wiked.internal.xwt.XWTFactoryRegistry;
import com.xpn.xwiki.wiked.internal.xwt.cf.ButtonFactory;
import com.xpn.xwiki.wiked.internal.xwt.cf.LayoutDataFactory;

public class ButtonFactoryTest extends TestCase {

	private ButtonFactory factory;
	
	private static final DocumentBuilderFactory DBF = 
		DocumentBuilderFactory.newInstance();
	
	protected void setUp() throws Exception {
		this.factory = new ButtonFactory();
		this.factory.setBuilderContext(new TestBuilderContext());
	}

	public void testButtonFactory() throws Exception {
		InputStream stream = getClass().getResourceAsStream("ButtonFactoryTest-1.xml");
		Document doc = DBF.newDocumentBuilder().parse(stream);
		Composite parent = new Composite(new Shell(), SWT.NONE);
		Button btn = (Button)this.factory.create(parent, doc.getDocumentElement());
		assertNotNull(btn);
		RowData data = (RowData)btn.getLayoutData();
		assertEquals(11, data.width);
		assertEquals(22, data.height);
		assertEquals("This is a button", btn.getText());
    }
    
    private class TestBuilderContext implements XWTBuilderContext {
        private ObjectRegistry objects;
        private Map factories;
        public TestBuilderContext() {
            this.objects = new SimpleObjectRegistry();
            this.factories = new HashMap();
            this.factories.put("button", ButtonFactoryTest.this.factory);
            LayoutDataFactory ldFactory = new LayoutDataFactory();
            ldFactory.setBuilderContext(this);
            this.factories.put("layoutData", ldFactory);
        }
		public XWTFactoryRegistry getFactoryRegistry() {
			return new XWTFactoryRegistry() {
                public AbstractSWTFactory getFactory(Element element) {
                    return (AbstractSWTFactory)factories.get(element.getNodeName());
                }
            };
		}
		public ObjectRegistry getObjectRegistry() {
			return objects;
		}
		public ClassLoader getClassLoader() {
			return getClass().getClassLoader();
		}
	}
}
