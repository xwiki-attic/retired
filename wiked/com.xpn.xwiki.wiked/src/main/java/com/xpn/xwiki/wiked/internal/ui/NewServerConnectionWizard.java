
package com.xpn.xwiki.wiked.internal.ui;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilder;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;
import com.xpn.xwiki.wiked.repository.IRemoteRepository;
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;
import com.xpn.xwiki.wiked.repository.RepositoryException;

/**
 * Wizard providing new remote connection
 */
public abstract class NewServerConnectionWizard extends Wizard implements INewWizard {
    
	private ServerDetailsPage serverDetailsPage;
    private String type;
    private IRepository repository;

	/**
	 * Constructor for SampleNewWizard.
	 */
	public NewServerConnectionWizard(IRepositoryManager manager, String type) {
        this.type = type;
        this.serverDetailsPage = new ServerDetailsPage(manager, "Server Details");
        setNeedsProgressMonitor(true);
	}

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("New Repository"); 
        setDefaultPageImageDescriptor(WikedPlugin.getInstance().getImageRegistry().getDescriptor(
            WikedImageRegistry.NEW_REPOSITORY));
    }

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		addPage(serverDetailsPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
                monitor.setTaskName("Connecting to " + serverDetailsPage.getName() + "...");
                monitor.worked(1);
                IRepositoryManager rm = WikedPlugin.getInstance().getRepositoryManager();
                try {
                    rm.addRepository(serverDetailsPage.createServer());
                } catch (Exception ex) {
                    final String msg = "Connection failed:\n" + ex.getMessage();
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            MessageDialog.openError(getShell(), "Connection Error", msg);
                        }
                    });
                    WikedPlugin.logError("connection failure: " + serverDetailsPage, ex);
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

    public IRepository getRepository() {
    	return this.repository;
    }
    
    public ServerDetailsPage getServerDetailsPage() {
    	return this.serverDetailsPage;
    }
    
    protected abstract Object createURL(String host, int port, String usr, String pwd);

    public class ServerDetailsPage extends WizardPage {
    
        private String name;
        private String host;
        private String port;
        private String username;
        private String password;
        
        private IRepositoryManager manager;

        public ServerDetailsPage(IRepositoryManager manager, String pageName) {
            super(pageName);
            this.manager = manager;
            setDescription("Specifiy Details of a Remote Repository Server");
            setTitle("New Remote Server Details");
        }
    
        public void createControl(Composite parent) {
            try {
                XWTBuilder builder = new XWTBuilder();
                InputStream stream = getClass().getResourceAsStream("NewServerConnectionWizard.xwt");
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
        
		public void setHost(String host) {
			this.host = host;
		}
        
        public void setPort(String port) {
            this.port = port;
        }

        public void setUserName(String username) {
			this.username = username;
 		}
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public IRepository createServer() 
            throws MalformedURLException, RepositoryException {
            HashMap map = new HashMap();
            map.put(IRemoteRepository.ATTR_NAME, (this.name != null && this.name.length() > 0) ?
                this.name : this.host);
            map.put(IRemoteRepository.ATTR_URL, createURL(this.host, Integer.parseInt(this.port), 
                this.username, this.password));
            map.put(IRemoteRepository.ATTR_USERNAME, this.username);
            map.put(IRemoteRepository.ATTR_PASSWORD, this.password);
            return this.manager.createRepository(type, map);
        }
        
		public String toString() {
        	return name+"@"+host+" as "+username;
        }
    }

}
