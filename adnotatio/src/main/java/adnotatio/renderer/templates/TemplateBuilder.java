/**
 * 
 */
package adnotatio.renderer.templates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import adnotatio.common.cache.CacheBuilder;
import adnotatio.common.cache.CacheNode;
import adnotatio.common.cache.CompositeCacheNode;
import adnotatio.common.cache.ICacheNodeVisitor;
import adnotatio.common.cache.TextCacheNode;
import adnotatio.common.xml.IXmlElement;
import adnotatio.common.xml.IXmlText;
import adnotatio.common.xml.SerializationUtil;

/**
 * This is a registry of {@link IFieldInfoFactory} instances. It maps individual
 * "tag names" to the corresponding {@link IFieldInfoFactory} objects used to
 * create {@link FieldInfo} for real widgets.
 * 
 * @author kotelnikov
 */
public class TemplateBuilder {

    static class FactoryNode extends CompositeCacheNode {

        private static long fCounter;

        private IXmlElement fElement;

        private IFieldInfoFactory fFactory;

        private String fId;

        public FactoryNode(IFieldInfoFactory factory, IXmlElement e) {
            fFactory = factory;
            fElement = e;
            fId = "__tmpl" + fCounter++;
        }

        public FieldInfo getFieldInfo(FieldPanel widget) {
            FieldInfo info = fFactory.newFieldInfo(widget, fElement);
            return info;
        }

        public String getId() {
            return fId;
        }

    }

    static class PanelGenerator implements ICacheNodeVisitor {

        private StringBuffer fBuf;

        private Map fFieldInfoMap;

        private FieldPanel fPanel;

        private Stack fStack = new Stack();

        public PanelGenerator() {
            super();
        }

        public FieldPanel buildWidget(
            CompositeCacheNode node,
            IFieldI18N messages) {
            fPanel = new FieldPanel(messages);
            buildWidget(fPanel.getPanel(), node);
            return fPanel;
        }

        /**
         * @param node
         * @return
         */
        private void buildWidget(
            FieldPanel.InternalHTMLPanel panel,
            CompositeCacheNode node) {
            fBuf = new StringBuffer();
            fFieldInfoMap = new HashMap();
            int len = node.getChildrenCount();
            for (int i = 0; i < len; i++) {
                CacheNode child = node.getChild(i);
                child.accept(this);
            }
            if (panel != null) {
                panel.setPanelContent(fBuf.toString(), fFieldInfoMap);
            }
            fBuf = null;
            fFieldInfoMap = null;
        }

        public FieldPanel getPanel() {
            return fPanel;
        }

        public void visit(CompositeCacheNode node) {
            FactoryNode n = (FactoryNode) node;
            String id = n.getId();
            String placeholder = "<span id='" + id + "'></span>";
            fBuf.append(placeholder);

            FieldInfo info = n.getFieldInfo(fPanel);
            fFieldInfoMap.put(id, info);

            FieldInfo parent = (FieldInfo) (!fStack.isEmpty()
                ? fStack.peek()
                : null);
            if (parent != null) {
                parent.addChildFieldInfo(info);
            }

            if (n.getChildrenCount() > 0) {
                StringBuffer parentBuf = fBuf;
                Map parentInfoMap = fFieldInfoMap;

                FieldPanel.InternalHTMLPanel widget = (info instanceof IContainerField)
                    ? fPanel.newTemplatePanel()
                    : null;
                fStack.push(info);
                try {
                    buildWidget(widget, n);
                } finally {
                    fStack.pop();
                }
                if (widget != null) {
                    IContainerField f = (IContainerField) info;
                    f.setContentWidget(widget);
                }

                fBuf = parentBuf;
                fFieldInfoMap = parentInfoMap;
            }
        }

        public void visit(TextCacheNode node) {
            fBuf.append(node.getText());
        }

    }

    static class TopNode extends CompositeCacheNode {

    }

    CacheBuilder fBuilder = new CacheBuilder() {

        /**
         * @see adnotatio.common.cache.CacheBuilder#getChildren(java.lang.Object,
         *      CompositeCacheNode)
         */
        protected Iterator getChildren(
            Object node,
            CompositeCacheNode compositeNode) {
            if (!(node instanceof IXmlElement))
                return null;
            final IXmlElement e = (IXmlElement) node;
            return new Iterator() {

                int fPos = 0;

                public boolean hasNext() {
                    return fPos < e.getChildNumber();
                }

                public Object next() {
                    if (!hasNext())
                        return null;
                    return e.getChild(fPos++);
                }

                public void remove() {
                    throw new RuntimeException();
                }

            };
        }

        /**
         * @see adnotatio.common.cache.CacheBuilder#getCompositeNode(java.lang.Object)
         */
        protected CompositeCacheNode getCompositeNode(Object object) {
            if (!(object instanceof IXmlElement))
                return null;
            IXmlElement e = (IXmlElement) object;
            String tagName = e.getNodeName();
            String type = e.getAttribute("type");
            IFieldInfoFactory factory = fFieldInfoFactoryRegistry
                .getFieldInfoFactory(tagName, type);

            CompositeCacheNode result = null;
            if (factory != null) {
                result = new FactoryNode(factory, e);
            }
            return result;
        }

        /**
         * @see adnotatio.common.cache.CacheBuilder#getTextPrefix(java.lang.Object,
         *      CompositeCacheNode)
         */
        protected String getTextPrefix(
            Object node,
            CompositeCacheNode compositeNode) {
            if (compositeNode != null)
                return null;
            SerializationUtil util = SerializationUtil.getInstance();
            if (node instanceof IXmlElement) {
                StringBuffer buf = new StringBuffer();
                util.serializeOpenTag((IXmlElement) node, buf);
                return buf.toString();
            } else if (node instanceof IXmlText) {
                StringBuffer buf = new StringBuffer();
                util.serializeText((IXmlText) node, buf);
                return buf.toString();
            }
            return null;
        }

        /**
         * @see adnotatio.common.cache.CacheBuilder#getTextSuffix(java.lang.Object,
         *      CompositeCacheNode)
         */
        protected String getTextSuffix(
            Object node,
            CompositeCacheNode compositeNode) {
            if (compositeNode != null)
                return null;
            SerializationUtil util = SerializationUtil.getInstance();
            if (node instanceof IXmlElement) {
                StringBuffer buf = new StringBuffer();
                util.serializeCloseTag((IXmlElement) node, buf);
                return buf.toString();
            }
            return null;
        }

        protected CompositeCacheNode newTopNode(Object object) {
            return new TopNode();
        }

    };

    private IFieldI18N fConstants;

    FieldInfoFactoryRegistry fFieldInfoFactoryRegistry;

    private Map fTemplates = new HashMap();

    /**
     * @param registry
     */
    public TemplateBuilder(FieldInfoFactoryRegistry registry) {
        this(registry, IFieldI18N.NULL);
    }

    /**
     * @param registry
     */
    public TemplateBuilder(
        FieldInfoFactoryRegistry registry,
        IFieldI18N constants) {
        super();
        fFieldInfoFactoryRegistry = registry;
        fConstants = constants;
    }

    public void addTemplate(String templateName, IXmlElement templateNode) {
        CompositeCacheNode template = fBuilder.build(templateNode);
        fTemplates.put(templateName, template);
    }

    public FieldPanel buildPanel(String templateName) {
        return buildPanel(templateName, fConstants);
    }

    public FieldPanel buildPanel(String templateName, IFieldI18N constants) {
        CompositeCacheNode template = (CompositeCacheNode) fTemplates
            .get(templateName);
        if (template == null)
            return null;
        PanelGenerator generator = new PanelGenerator();
        return generator.buildWidget(template, constants);
    }

    public IFieldI18N getConstants() {
        return fConstants;
    }

    public void removeTemplate(String templateName) {
        fTemplates.remove(templateName);
    }

    public void setConstants(IFieldI18N constants) {
        fConstants = constants;
    }

}
