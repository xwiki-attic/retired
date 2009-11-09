
package com.xpn.xwiki.wiked.internal.xwt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** 
 * Simple ObjectRegistry implementation based on HashMap.
 */
public class SimpleObjectRegistry implements ObjectRegistry {

    private Map objectMap = new HashMap();

	public String[] getObjectIds() {
		Set idSet = this.objectMap.keySet();
        return (String[])idSet.toArray(new String[idSet.size()]);
	}
    
    public Object getObject(String id) {
		return (Object)this.objectMap.get(id);
	}

	public void setObject(String id, Object object) {
        if (this.objectMap.put(id, object) != null) {
        	throw new IllegalStateException("duplicite id "+id);
        }
	}

	public void setObjects(Map objects) {
        Iterator keys = objects.keySet().iterator();
        while (keys.hasNext()) {
        	Object id = keys.next();
            if (this.objectMap.put(id, objects.get(id)) != null) {
                throw new IllegalStateException("duplicite id "+id);
            }
        }
	}
}