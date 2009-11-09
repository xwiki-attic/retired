package org.xwiki.rest;

import java.util.HashMap;
import java.util.Map;

import org.restlet.resource.Resource;

/**
 * A registry for keeping track of what resource class is attached to which URI pattern.
 * 
 * @version $Id$
 */
public class XWikiResourceClassRegistry
{
    private Map<Class< ? extends Resource>, String> resourceClassToUriPatternMap;

    public XWikiResourceClassRegistry()
    {
        resourceClassToUriPatternMap = new HashMap<Class< ? extends Resource>, String>();
    }

    public void registerResourceClass(Class< ? extends Resource> resource, String uriPattern)
    {
        resourceClassToUriPatternMap.put(resource, uriPattern);
    }

    public void removeResourceClass(Class resource)
    {
        resourceClassToUriPatternMap.remove(resource);
    }

    public String getUriPatternForResourceClass(Class< ? extends Resource> resourceClass)
    {
        return resourceClassToUriPatternMap.get(resourceClass);
    }

}
