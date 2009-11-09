package com.xpn.xwiki.wiked.internal.ui;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilder;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;
import com.xpn.xwiki.wiked.repository.IRemoteRepository;

public class RemoteRepositoryPropertyPage extends PropertyPage {

	private XWTBuilder builder;
    
	private String name;
	private String url;
	private String userName;
	private String password;

    private static final String XWT = "RemoteRepositoryPropertyPage.xwt";
    
	public RemoteRepositoryPropertyPage() throws XWTException {
		this.builder = new XWTBuilder(
            RemoteRepositoryPropertyPage.class.getClassLoader());
	}

	public void setElement(IAdaptable element) {
		super.setElement(element);
		IRemoteRepository repository = (IRemoteRepository) element;
		this.name = repository.getName();
		this.userName = repository.getUserName();
		this.password = repository.getPassword();
		this.url = repository.getURL().toExternalForm();
	}

	public XWTBuilder getBuilder() {
		return this.builder;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String username) {
		this.userName = username;
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
		IRemoteRepository repository = (IRemoteRepository)getElement();
		try {
            repository.setName(getName());
            repository.setUserName(getUserName());
            repository.setPassword(getPassword());
            repository.setURL(new URL(getUrl()));
		} catch (Exception ex) {
			WikedPlugin.handleError("cannot configure " + repository, ex);
			return false;
		}

		return true;
	}

}