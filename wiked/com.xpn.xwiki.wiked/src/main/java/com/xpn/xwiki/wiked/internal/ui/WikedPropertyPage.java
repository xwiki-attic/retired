package com.xpn.xwiki.wiked.internal.ui;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.WikiNature;
import com.xpn.xwiki.wiked.internal.xwt.XWTBuilder;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;

// Project property setup
//
//    ((IResource) getElement()).setPersistentProperty(
//        new QualifiedName("", OWNER_PROPERTY),
//        ownerText.getText());

public class WikedPropertyPage extends PropertyPage {

	private XWTBuilder builder;
	private boolean previousWikiNature;
	private boolean hasWikiNature;

	public WikedPropertyPage() throws XWTException {
		this.builder = new XWTBuilder();
	}

	public void setElement(IAdaptable element) {
		super.setElement(element);
		IProject project = (IProject) element;
		try {
			this.previousWikiNature = project.hasNature(WikiNature.ID);
		} catch (CoreException ex) {
			WikedPlugin.logError("cannot read natures of " + project, ex);
			this.previousWikiNature = false;
		} finally {
			this.hasWikiNature = this.previousWikiNature;
		}
	}

	public XWTBuilder getBuilder() {
		return this.builder;
	}

	public boolean hasWikiNature() {
		return hasWikiNature;
	}

	public void setWikiNature(boolean flag) {
		this.hasWikiNature = flag;
	}

	protected Control createContents(Composite parent) {
		try {
			InputStream stream = getClass().getResourceAsStream("WikedPropertyPage.xwt");
			return this.builder.create(parent, stream, this);
		} catch (XWTException ex) {
			IllegalStateException isex = new IllegalStateException();
			isex.initCause(ex);
			throw isex;
		}
	}

	public boolean performOk() {
		if (this.previousWikiNature != this.hasWikiNature) {
			IProject project = (IProject) getElement();
			try {
				IProjectDescription description = project.getDescription();
				String[] natures = description.getNatureIds();
				if (hasWikiNature) {
					String[] newNatures = new String[natures.length + 1];
					System.arraycopy(natures, 0, newNatures, 0, natures.length);
					newNatures[natures.length] = WikiNature.ID;
					description.setNatureIds(newNatures);
				} else {
					String[] newNatures = new String[natures.length - 1];
					Iterator niter = Arrays.asList(natures).iterator();
					for (int i = 0; niter.hasNext();) {
						String natureId = (String) niter.next();
						if (WikiNature.ID.equals(natureId) == false) {
							newNatures[i++] = natureId;
						}
					}
					description.setNatureIds(newNatures);
				}
				project.setDescription(description, null);
			} catch (CoreException ex) {
				WikedPlugin.logError("cannot (un)set nature of " + project, ex);
				return false;
			}
		}

		return true;
	}

}