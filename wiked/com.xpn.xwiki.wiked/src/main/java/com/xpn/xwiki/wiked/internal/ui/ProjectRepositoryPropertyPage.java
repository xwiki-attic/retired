package com.xpn.xwiki.wiked.internal.ui;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.repository.ProjectRepository;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilder;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;
import com.xpn.xwiki.wiked.repository.IRemoteRepository;
import com.xpn.xwiki.wiked.repository.IRepository;

public class ProjectRepositoryPropertyPage extends PropertyPage {

	private XWTBuilder builder;
	private Map remoteServers;
	private String previewServerName;

    private static final String XWT = "ProjectRepositoryPropertyPage.xwt";
    
	public ProjectRepositoryPropertyPage() throws XWTException {
		this.remoteServers = new HashMap();
		IRepository[] repositories = WikedPlugin.getInstance().
			getRepositoryManager().getRepositories();
		for (int i = 0; i < repositories.length; i++) {
			IRepository repository = repositories[i];
			if (repository instanceof IRemoteRepository) {
				this.remoteServers.put(repository.getName(), repository);
			}
		}
		this.builder = new XWTBuilder(
            ProjectRepositoryPropertyPage.class.getClassLoader());
	}

	public void setElement(IAdaptable element) {
		super.setElement(element);
		ProjectRepository repository = (ProjectRepository)element;
		try {
			this.previewServerName = repository.getPreviewServerName();
		} catch (Exception ex) {
			WikedPlugin.handleError("cannot configure " + repository, ex);
		}
	}

	public XWTBuilder getBuilder() {
		return this.builder;
	}

	public String[] getAvailableRemoteServerNames() {
		String[] arr = new String[this.remoteServers.size()];
		String[] names = (String[])this.remoteServers.keySet().toArray(arr);
		Arrays.sort(names);
		return names;
	}

	public String getServerDetails(String serverName) {
		IRepository repo = (IRepository)this.remoteServers.get(serverName);
		if (repo != null) {
			StringBuffer buff = new StringBuffer();
			buff.append("Type ").append(repo.getType()).append(", ");
			return buff.toString();
		}
		return "";
	}
	
	public String getPreviewServerName() {
		return this.previewServerName;
	}

	public void setPreviewServerName(String previewServerName) {
		this.previewServerName = previewServerName;
	}

	public int getPreviewServerIndex() {
		if (this.previewServerName == null || this.previewServerName.length() == 0) {
			return -1;
		}
		String[] names = getAvailableRemoteServerNames();
		for (int i = 0; i < names.length; i++) {
			if (this.previewServerName.equals(names[i])) {
				return i;
			}
		}
		return -1;
	}
	
	protected Control createContents(Composite parent) {
        try {
			InputStream stream = getClass().getResourceAsStream(XWT);
			return this.builder.create(parent, stream, this);
		} catch (Exception ex) {
			IllegalStateException isex = new IllegalStateException();
			isex.initCause(ex);
			throw isex;
		}
	}

	public boolean performOk() {
		ProjectRepository repository = (ProjectRepository)getElement();
		try {
            repository.setPreviewServerName(getPreviewServerName());
		} catch (Exception ex) {
			WikedPlugin.handleError("cannot configure " + repository, ex);
			return false;
		}

		return true;
	}

}