/**
 * 
 */
package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import adnotatio.common.xml.IXmlElement;

/**
 * @author kotelnikov
 */
public class TabPanelInfo extends FieldInfo {

    /**
     * @param panel
     * @param e
     */
    public TabPanelInfo(FieldPanel panel, IXmlElement e) {
        super(panel, e);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#addChildFieldInfo(adnotatio.renderer.templates.FieldInfo)
     */
    protected void addChildFieldInfo(FieldInfo child) {
        if (child instanceof TabInfo) {
            TabPanel panel = (TabPanel) getWidget();
            TabInfo i = (TabInfo) child;
            String label = i.getLabelFromAttributes();
            Widget w = i.getWidget();
            panel.add(w, label);
            boolean selected = i.getAttributeAsBoolean("selected", false);
            if (selected) {
                int count = panel.getTabBar().getTabCount();
                panel.selectTab(count - 1);
            }
        }
        super.addChildFieldInfo(child);
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#clear()
     */
    public void clear() {
        // 
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#getValue()
     */
    public Object getValue() {
        return null;
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#newWidget()
     */
    protected Widget newWidget() {
        TabPanel panel = new TabPanel();
        return panel;
    }

    /**
     * @see adnotatio.renderer.templates.FieldInfo#setValue(java.lang.Object)
     */
    protected boolean setValue(Object value) {
        return false;
    }

}
