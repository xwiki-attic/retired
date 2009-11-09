
package com.xpn.xwiki.wiked.test.xwt;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.ObjectRegistry;
import com.xpn.xwiki.wiked.internal.xwt.SimpleObjectRegistry;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilderContext;
import com.xpn.xwiki.wiked.internal.xwt.XWTFactoryRegistry;
import com.xpn.xwiki.wiked.internal.xwt.cf.LayoutDataFactory;
import com.xpn.xwiki.wiked.internal.xwt.cf.TextFactory;

public class TextFactoryTest extends TestCase {

	private TextFactory factory;
	
	private static final DocumentBuilderFactory DBF = 
		DocumentBuilderFactory.newInstance();
	
	protected void setUp() throws Exception {
		this.factory = new TextFactory();
		this.factory.setBuilderContext(new TestBuilderContext());
	}

	public void testTextFactory() throws Exception {
		InputStream stream = getClass().getResourceAsStream("TextFactoryTest-1.xml");
		Document doc = DBF.newDocumentBuilder().parse(stream);
		Composite parent = new Composite(new Shell(), SWT.NONE);
		Text text = (Text)this.factory.create(parent, doc.getDocumentElement());
		assertNotNull(text);
		RowData data = (RowData)text.getLayoutData();
		assertEquals(11, data.width);
		assertEquals(22, data.height);
		assertEquals("This is a text", text.getText());
		assertEquals(10, text.getTextLimit());
	}

    public void testGridFactory() throws Exception {
        InputStream stream = getClass().getResourceAsStream("TextFactoryTest-2.xml");
        Document doc = DBF.newDocumentBuilder().parse(stream);
        Composite parent = new Composite(new Shell(), SWT.NONE);
        Text text = (Text)this.factory.create(parent, doc.getDocumentElement());
        assertNotNull(text);
        GridData layoutData = (GridData)text.getLayoutData();
        assertNotNull(layoutData);
    }    
    
    private class TestBuilderContext implements XWTBuilderContext {
        private ObjectRegistry objects;
        private Map factories;
        public TestBuilderContext() {
            this.objects = new SimpleObjectRegistry();
            this.factories = new HashMap();
            this.factories.put("text", TextFactoryTest.this.factory);
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
