
package com.xpn.xwiki.wiked.confluence.repository.remote;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * 
 * @author psenicka_ja
 */
public class PropertyDescriptorFactory {

	private PropertyDescriptorFactory() {
	}
    
    public static IPropertyDescriptor[] create(Set keys) {
        return create(keys, new Visitor() {
            public PropertyDescriptor create(String key) {
            	return new PropertyDescriptor(key, key);
            }
        });
    }

    /**
	 * @param map
	 * @param visitor
	 * @return
	 */
	public static IPropertyDescriptor[] create(Set keys, Visitor visitor) {
        List properties = new ArrayList();
        Iterator keyi = keys.iterator();
        while (keyi.hasNext()) {
        	String key = (String)keyi.next();
            PropertyDescriptor desc = visitor.create(key);
            if (desc != null) {
                properties.add(desc);
            }
        }
		return (IPropertyDescriptor[])properties.toArray(
            new IPropertyDescriptor[properties.size()]);
	}

    interface Visitor {
        PropertyDescriptor create(String key);
    }
}
