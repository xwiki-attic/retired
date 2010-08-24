
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
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.ISpace;
import com.xpn.xwiki.wiked.repository.RepositoryException;

/**
 * Wizard providing new remote connection
 */
public class NewSpaceWizard extends Wizard {
    
	private SpaceDetailsPage page;
    private IRepository repository;
    private ISpace space;

	/**
	 * Constructor for SampleNewWizard.
	 */
	public NewSpaceWizard(IRepository repository) {
        this.repository = repository;
        this.page = new SpaceDetailsPage("Space Details");
        setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
                monitor.setTaskName("Connecting to " + page.getName() + "...");
                monitor.worked(1);
                try {
                    space = page.createSpace(repository);
                } catch (Exception ex) {
                    WikedPlugin.handleError("Cannot create space "+page.getName(), ex);
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

    public ISpace getSpace() {
    	return this.space;
    }
    
    public static class SpaceDetailsPage extends WizardPage {
    
        private String name;

        public SpaceDetailsPage(String pageName) {
            super(pageName);
            setDescription("Specifiy Details of a Space");
            setTitle("New Space Details");
        }
    
        public void createControl(Composite parent) {
            try {
                XWTBuilder builder = new XWTBuilder();
                InputStream stream = getClass().getResourceAsStream("NewSpaceWizard.xwt");
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
        
        public ISpace createSpace(IRepository repository) throws RepositoryException {
        	return repository.createSpace(getName());
        }
        
    }

}
