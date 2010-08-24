package com.xpn.xwiki.wiked.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class WorkspaceProjectHelper {
    
	public static IProject createProject(String projectName) throws CoreException {
        return createProject(projectName, null);
    }
    
    public static IProject createProject(String projectName, String natureId) 
        throws CoreException {
        IProject project = deleteProject(projectName);
        project.create(null);
        project.open(null);
        setProjectNature(project, natureId);
        return project;
    }

	public static IProject deleteProject(String projectName) throws CoreException {
        IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = wsRoot.getProject(projectName);
        if (project.exists()) {
            project.delete(true, true, null);
        }
        return project;
    }

    public static void setProjectNature(IProject project, String natureId) 
        throws CoreException {
        if (natureId != null) {
            IProjectDescription description = project.getDescription();
            String[] natures = description.getNatureIds();
            String[] newNatures = new String[natures.length + 1];
            System.arraycopy(natures, 0, newNatures, 0, natures.length);
            newNatures[natures.length] = natureId;
            description.setNatureIds(newNatures);
            project.setDescription(description, null);
        }
    }

}
