
package com.xpn.xwiki.wiked.test.xwt;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.ObjectRegistry;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilderContext;
import com.xpn.xwiki.wiked.internal.xwt.XWTFactoryRegistry;
import com.xpn.xwiki.wiked.internal.xwt.cf.LabelFactory;
import com.xpn.xwiki.wiked.internal.xwt.cf.LayoutDataFactory;
import com.xpn.xwiki.wiked.internal.xwt.cf.TextFactory;

public class LabelFactoryTest extends TestCase {

	private LabelFactory factory;
	
	private static final DocumentBuilderFactory DBF = 
		DocumentBuilderFactory.newInstance();
	
	protected void setUp() throws Exception {
		this.factory = new LabelFactory();
		this.factory.setBuilderContext(new TestBuilderContext());
	}

	public void testLabelFactory() throws Exception {
		InputStream stream = getClass().getResourceAsStream("LabelFactoryTest-1.xml");
		Document doc = DBF.newDocumentBuilder().parse(stream);
		Composite parent = new Composite(new Shell(), SWT.NONE);
		Label label = (Label)this.factory.create(parent, doc.getDocumentElement());
		assertNotNull(label);
		RowData data = (RowData)label.getLayoutData();
		assertEquals(11, data.width);
		assertEquals(22, data.height);
		assertEquals("This is a label", label.getText());
	}

    private static class TestBuilderContext implements XWTBuilderContext {
        private ObjectRegistry objects;
        private Map factories;
        public TestBuilderContext() {
            this.factories = new HashMap();
            LabelFactory labelFactory = new LabelFactory();
            labelFactory.setBuilderContext(this);
            this.factories.put("label", labelFactory);
            TextFactory textFactory = new TextFactory();
            textFactory.setBuilderContext(this);
            this.factories.put("text", textFactory);
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
