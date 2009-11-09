
package com.xpn.xwiki.wiked.test.xwt;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.ObjectRegistry;
import com.xpn.xwiki.wiked.internal.xwt.SimpleObjectRegistry;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilderContext;
import com.xpn.xwiki.wiked.internal.xwt.XWTFactoryRegistry;
import com.xpn.xwiki.wiked.internal.xwt.cf.CompositeFactory;
import com.xpn.xwiki.wiked.internal.xwt.cf.LayoutFactory;
import com.xpn.xwiki.wiked.internal.xwt.cf.SWTPropertyReader;

public class CompositeFactoryTest extends TestCase {

	private CompositeFactory factory;
	private Composite composite;
	
	private static final DocumentBuilderFactory DBF = 
		DocumentBuilderFactory.newInstance();
	
	protected void setUp() throws Exception {
		this.factory = new CompositeFactory();
		this.factory.setBuilderContext(new TestBuilderContext());
	}

	public void testSWTFlagParser() throws Exception {
		InputStream stream = getClass().getResourceAsStream("CompositeFactoryTest-1.xml");
		Document doc = DBF.newDocumentBuilder().parse(stream);
		assertEquals(SWT.NONE|SWT.RESIZE, 
		    SWTPropertyReader.getDefault().parse(doc.getDocumentElement()));
	}

	public void testFactory() throws Exception {
		InputStream stream = getClass().getResourceAsStream("CompositeFactoryTest-1.xml");
		Document doc = DBF.newDocumentBuilder().parse(stream);
		Composite parent = new Shell();
		this.composite = (Composite)this.factory.create(parent, doc.getDocumentElement());
		assertNotNull(composite);
	}
	
	public void testLayoutFactory() throws Exception {
		InputStream stream = getClass().getResourceAsStream("CompositeFactoryTest-2.xml");
		Document doc = DBF.newDocumentBuilder().parse(stream);
		Composite parent = new Shell();
		this.composite = (Composite)this.factory.create(parent, doc.getDocumentElement());
		assertNotNull(composite);
		RowLayout layout = (RowLayout)composite.getLayout();
		assertNotNull(layout);
		assertEquals(SWT.HORIZONTAL, layout.type);
		assertEquals(true, layout.wrap);
		assertEquals(true, layout.pack);
		assertEquals(true, layout.justify);
	}

    public void testGridLayoutFactory() throws Exception {
        InputStream stream = getClass().getResourceAsStream("CompositeFactoryTest-3.xml");
        Document doc = DBF.newDocumentBuilder().parse(stream);
        Composite parent = new Shell();
        this.composite = (Composite)this.factory.create(parent, doc.getDocumentElement());
        assertNotNull(composite);
        GridLayout layout = (GridLayout)composite.getLayout();
        assertNotNull(layout);
        assertEquals(2, layout.numColumns); 
        assertEquals(9, layout.verticalSpacing); 
    }

    private static class TestBuilderContext implements XWTBuilderContext {
        private ObjectRegistry objects;
        private Map factories;
        public TestBuilderContext() {
            this.objects = new SimpleObjectRegistry();
            this.factories = new HashMap();
            LayoutFactory layoutFactory = new LayoutFactory();
            layoutFactory.setBuilderContext(this);
            this.factories.put("layout", layoutFactory);
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
