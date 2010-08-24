
package com.xpn.xwiki.wiked.internal;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Wiki syntax checking builder
 * @author psenicka_ja
 */
public class WikiBuilder extends IncrementalProjectBuilder {

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
        throws CoreException {
		monitor.subTask("Running FindBugs...");
		switch (kind) {

    		case IncrementalProjectBuilder.FULL_BUILD:
    			System.out.println("FULL BUILD");
    			doBuild(args, monitor);
    			break;
    
            case IncrementalProjectBuilder.INCREMENTAL_BUILD:
    			System.out.println("INCREMENTAL BUILD");
    			doBuild(args, monitor);
    			break;
    
            case IncrementalProjectBuilder.AUTO_BUILD:
    			System.out.println("AUTO BUILD");
    			doBuild(args, monitor);
    			break;
    
            default:
                break;
		}

		return null;
	}

	private void doBuild(Map args, IProgressMonitor monitor)
		throws CoreException {
	}
}
