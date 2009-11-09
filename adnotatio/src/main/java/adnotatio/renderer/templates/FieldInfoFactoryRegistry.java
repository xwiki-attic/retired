/**
 * 
 */
package adnotatio.renderer.templates;

import java.util.HashMap;
import java.util.Map;

import adnotatio.common.xml.IXmlElement;

/**
 * This is a registry of {@link IFieldInfoFactory} instances. It maps individual
 * "tag names" to the corresponding {@link IFieldInfoFactory} objects used to
 * create {@link FieldInfo} for real widgets.
 * 
 * @author kotelnikov
 */
public class FieldInfoFactoryRegistry {

    /**
     * This map contains {@link IFieldInfoFactory} objects. The keys are
     * "name#type" string where "name" is the name of the template tag and the
     * "type" is the value of the "type" attribute in the tag
     */
    private Map fFactories = new HashMap();

    /**
     * 
     */
    public FieldInfoFactoryRegistry() {
        super();
        initFielInfoFactories();
    }

    /**
     * Adds the given {@link IFieldInfoFactory} object and associates it with
     * the specified template tag and tag type.
     * 
     * @param name the tag name
     * @param type the type of the tag (the value of the "type" attribute in the
     *        tag)
     * @param factory the factory to add
     */
    public void addFieldInfoFactory(
        String name,
        String type,
        IFieldInfoFactory factory) {
        Map map = (Map) fFactories.get(name);
        if (map == null) {
            map = new HashMap();
            fFactories.put(name, map);
        }
        if (type == null) {
            type = "";
        }
        map.put(type, factory);
    }

    /**
     * Returns a field factory corresponding to the specified tag name and tag
     * type
     * 
     * @param name the name of the template tag
     * @param type the type of the template tag
     * @return a field factory corresponding to the specified tag name and tag
     *         type
     */
    public IFieldInfoFactory getFieldInfoFactory(String name, String type) {
        IFieldInfoFactory factory = null;
        Map map = (Map) fFactories.get(name);
        if (map != null) {
            if (type == null) {
                type = "";
            }
            factory = (IFieldInfoFactory) map.get(type);
            if (factory == null) {
                factory = (IFieldInfoFactory) map.get("");
            }
        }
        return factory;
    }

    /**
     * Adds {@link IFieldInfoFactory} objects to this factory. Fills the given
     * map with the {@link IFieldInfoFactory} objects. The keys are "name#type"
     * string where "name" is the name of the template tag and the "type" is the
     * value of the "type" attribute in the tag
     */
    protected void initFielInfoFactories() {
        // HTML panel
        addFieldInfoFactory("html", null, new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new HTMLPanelInfo(panel, e);
            }
        });

        // Inline HTML panel
        addFieldInfoFactory("html", "inline", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new InlineHTMLPanelInfo(panel, e);
            }
        });

        // A simple text input
        addFieldInfoFactory("input", null, new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new TextBoxInfo(panel, e);
            }
        });

        // Checkbox
        addFieldInfoFactory("input", "checkbox", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new CheckboxFieldInfo(panel, e);
            }
        });

        // Radiobutton
        addFieldInfoFactory("input", "radio", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new RadioFieldInfo(panel, e);
            }
        });

        // Hidden field
        addFieldInfoFactory("input", "hidden", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new HiddenFieldInfo(panel, e);
            }
        });

        // "Readonly" field - just an HTML block
        addFieldInfoFactory("input", "readonly", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new ReadonlyFieldInfo(panel, e);
            }
        });

        // Password field
        addFieldInfoFactory("input", "password", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new PasswordFieldInfo(panel, e);
            }
        });

        // Submit buttons
        addFieldInfoFactory("input", "submit", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new SubmitButtonInfo(panel, e);
            }
        });

        // Cancel buttons
        addFieldInfoFactory("input", "cancel", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new CancelButtonInfo(panel, e);
            }
        });

        // Clear buttons
        addFieldInfoFactory("input", "clear", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new ClearButtonInfo(panel, e);
            }
        });

        // Textarea
        addFieldInfoFactory("textarea", null, new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new TextAreaInfo(panel, e);
            }
        });

        // Disclosure panel
        addFieldInfoFactory("panel", "disclosure", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new DisclosurePanelInfo(panel, e);
            }
        });

        // Tab widgets
        addFieldInfoFactory("panel", "tabs", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new TabPanelInfo(panel, e);
            }
        });
        addFieldInfoFactory("panel", "tab", new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new TabInfo(panel, e);
            }
        });

        // Label
        addFieldInfoFactory("label", null, new IFieldInfoFactory() {
            public FieldInfo newFieldInfo(FieldPanel panel, IXmlElement e) {
                return new LabelInfo(panel, e);
            }
        });

    }

}
