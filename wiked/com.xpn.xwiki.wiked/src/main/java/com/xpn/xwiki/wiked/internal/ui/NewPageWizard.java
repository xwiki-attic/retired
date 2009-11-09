
package com.xpn.xwiki.wiked.internal.ui;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilder;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;
import com.xpn.xwiki.wiked.repository.IPage;
import com.xpn.xwiki.wiked.repository.ISpace;
import com.xpn.xwiki.wiked.repository.RepositoryException;

/**
 * Wizard providing new remote connection
 */
public class NewPageWizard extends Wizard {
    
	private PageDetailsPage pageDetails;
    private ISpace space;
    private IPage page;

	/**
	 * Constructor for SampleNewWizard.
	 */
	public NewPageWizard(ISpace space) {
        this.space = space;
        this.pageDetails = new PageDetailsPage("Page Details");
        setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		addPage(pageDetails);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
                monitor.setTaskName("Connecting to " + pageDetails.getName() + "...");
                monitor.worked(1);
                try {
                    pageDetails.createPage(space);
                } catch (Exception ex) {
                    WikedPlugin.handleError("Cannot create page "+pageDetails.getName(), ex);
                    throw new InvocationTargetException(ex);
                }
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			return false;
		}
		return true;
	}

    public IPage getPage() {
    	return this.page;
    }
    
    public static class PageDetailsPage extends WizardPage {
    
        private String name;

        public PageDetailsPage(String pageName) {
            super(pageName);
            setDescription("Specifiy Details of a Page");
            setTitle("New Page Details");
        }
    
        public void createControl(Composite parent) {
            try {
                XWTBuilder builder = new XWTBuilder();
                InputStream stream = getClass().getResourceAsStream("NewPageWizard.xwt");
                setControl(builder.create(parent, stream, this));
            } catch (XWTException ex) {
            	IllegalStateException isex = new IllegalStateException();
                isex.initCause(ex);
                throw isex;
            }
        }
    
        public String getName() {
        	return name;
        }
        
        public void setName(String name) {
        	this.name = name;
        }
        
        public IPage createPage(ISpace space) throws RepositoryException {
        	return space.createPage(getName());
        }
        
    }

}
