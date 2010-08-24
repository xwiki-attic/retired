
package com.xpn.xwiki.wiked.test.xwt;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.ObjectRegistry;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilderContext;
import com.xpn.xwiki.wiked.internal.xwt.XWTFactoryRegistry;
import com.xpn.xwiki.wiked.internal.xwt.cf.ComboFactory;
import com.xpn.xwiki.wiked.internal.xwt.cf.LayoutDataFactory;
import com.xpn.xwiki.wiked.internal.xwt.cf.TextFactory;

public class ComboFactoryTest extends TestCase {

	private ComboFactory factory;
	
	private static final DocumentBuilderFactory DBF = 
		DocumentBuilderFactory.newInstance();
	
	protected void setUp() throws Exception {
		this.factory = new ComboFactory();
		this.factory.setBuilderContext(new TestBuilderContext());
	}

	public void testLabelFactory() throws Exception {
		InputStream stream = getClass().getResourceAsStream("ComboFactoryTest-1.xml");
		Document doc = DBF.newDocumentBuilder().parse(stream);
		Composite parent = new Composite(new Shell(), SWT.NONE);
		CCombo combo = (CCombo)this.factory.create(parent, doc.getDocumentElement());
		assertNotNull(combo);
		RowData data = (RowData)combo.getLayoutData();
		assertEquals(11, data.width);
		assertEquals(22, data.height);
		assertEquals("This is a label", combo.getText());
	}

    private static class TestBuilderContext implements XWTBuilderContext {
        private ObjectRegistry objects;
        private Map factories;
        public TestBuilderContext() {
            this.factories = new HashMap();
            ComboFactory comboFactory = new ComboFactory();
            comboFactory.setBuilderContext(this);
            this.factories.put("combo", comboFactory);
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
