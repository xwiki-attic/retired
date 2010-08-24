
package com.xpn.xwiki.wiked.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * The IProject Wiki nature. 
 * The nature identifies standard Eclipse project as a member
 * of Wiki system and takes them as local Wiki repository.
 * 
 * @author psenicka_ja
 */
public class WikiNature implements IProjectNature {

	private IProject project;

    public static final String ID = "com.xpn.xwiki.wiked.wiki";
    
	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject value) {
		project = value;
	}
}

