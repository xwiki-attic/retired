/**
 * 
 */
package adnotatio.renderer.templates;

import com.google.gwt.user.client.ui.Widget;

/**
 * Container field can have its own children.
 * 
 * @author kotelnikov
 */
public interface IContainerField {

    /**
     * Adds child widget to this container
     * 
     * @param widget the widget to add
     */
    void setContentWidget(Widget widget);

}
