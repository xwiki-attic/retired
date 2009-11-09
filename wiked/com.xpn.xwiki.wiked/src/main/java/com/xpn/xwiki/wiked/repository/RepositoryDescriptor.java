
package com.xpn.xwiki.wiked.repository;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.wizard.IWizard;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;

/**
 * A repository descriptor contains data read from the "repository"
 * extension point. 
 * Presence of attributes is ensured by exsd file associated to 
 * the extension point.
 */
public class RepositoryDescriptor implements IRepositoryFactory {

	/** The plugin configuration element */
    private IConfigurationElement element;
    
	public RepositoryDescriptor(IConfigurationElement element) {
        this.element = element;
    }
    
	/** @return the plugin configuration element */
	public IConfigurationElement getConfigurationElement() {
		return element;
	}
	
	/** 
	 * @return the plugin configuration element name read from the
	 * <code>name</code> attribute. 
	 */
    public String getName() {
    	return element.getAttribute("name");
    }

	/** 
	 * @return the icon read from the <code>icon</code> attribute. The
	 * icon is located in correcponding plugin structure. 
	 */
    public ImageDescriptor getIcon() {
        String path = element.getAttribute("icon");
        if (path != null && path.length() > 0) {
            try {
                IPluginDescriptor desc = this.element.getDeclaringExtension().
                    getDeclaringPluginDescriptor();
                URL url = desc.getPlugin().find(new Path(path));
                return ImageDescriptor.createFromURL(url);
            } catch (CoreException ex) {
                WikedPlugin.logError("cannot lookup icon "+path, ex);
            }
        }
        
        ImageRegistry registry = WikedPlugin.getInstance().getImageRegistry();
        return registry.getDescriptor(WikedImageRegistry.ADD_REPOSITORY);
    }

	/** 
	 * @return the plugin configuration element description read from the
	 * <code>description</code> attribute. 
	 */
    public String getDescription() {
        return element.getAttribute("description");
    }
    
	/** 
	 * @return the plugin configuration element type read from the
	 * <code>type</code> attribute. 
	 */
    public String getType() {
        return element.getAttribute("type");
    }
     
    /**
     * Creates a repository by delegating the call to a factory referred
     * as <code>factory</code> attribute. 
     * @see com.xpn.xwiki.wiked.repository.IRepositoryFactory#createRepository(java.lang.String, java.lang.Object)
     * @throws RepositoryException transalted the <code>CoreException</code>
     * catched during repository creation.
     */
    public IRepository createRepository(String type, Object dataObject) 
        throws RepositoryException {
    	try {
            IRepositoryFactory factory = (IRepositoryFactory)this.element.
                createExecutableExtension("factory");
            return factory.createRepository(getType(), dataObject);
        } catch (CoreException ex) {
            throw new RepositoryException(
                "cannot create repository factory", ex);
        }
    }

    /**
     * Returns a connection wizard.
     * The method reads the <code>connectionWizard</code> attribute and 
     * locates the wizard by it's id in <code>org.eclipse.ui.newWizards</code>
     * registry.
     * @return the wizard or <code>null</code> if no wizard is defined
     * @throws RepositoryException if there is no such wizard defined or
     * a <code>CoreException</code> was thrown.
     */
	public IWizard getConnectionWizard() 
        throws RepositoryException {
        IConfigurationElement[] wizards = this.element.getChildren("connectionWizard");
        if (wizards != null && wizards.length == 1) {
            String wizardId = wizards[0].getAttribute("id");
            if (wizardId == null || wizardId.length() == 0) {
                WikedPlugin.log("no wizard id defined");
            	return null;
            }
            try {
            	IConfigurationElement[] cfgs = Platform.getPluginRegistry().
                    getConfigurationElementsFor("org.eclipse.ui.newWizards");
                for (int i = 0; i < cfgs.length; i++) {
					if ("wizard".equals(cfgs[i].getName()) && 
                        wizardId.equals(cfgs[i].getAttribute("id"))) {
                        return (IWizard)cfgs[i].createExecutableExtension("class");
                    }
				}
            } catch (CoreException ex) {
                throw new RepositoryException(
                    "cannot create repository wizard", ex);
            }
        }
        
        return null;
    }
}
