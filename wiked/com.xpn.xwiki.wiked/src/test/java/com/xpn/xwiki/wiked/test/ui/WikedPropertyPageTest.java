
package com.xpn.xwiki.wiked.test.ui;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import com.xpn.xwiki.wiked.internal.WikiNature;
import com.xpn.xwiki.wiked.internal.ui.WikedPropertyPage;
import com.xpn.xwiki.wiked.internal.xwt.ObjectRegistry;
import com.xpn.xwiki.wiked.test.WorkspaceProjectHelper;

/**
 * Project property page test
 * @author psenicka_ja
 */
public class WikedPropertyPageTest extends TestCase {

    private IProject project1;

    private WikedPropertyPage page;
	private ObjectRegistry objectRegistry;
    
	protected void setUp() throws Exception {
        this.project1 = WorkspaceProjectHelper.createProject("p1");
		this.page = new WikedPropertyPage();
        this.page.setElement(project1);
        this.objectRegistry = this.page.getBuilder().getObjectRegistry();
	}
    
    protected void tearDown() throws Exception {
        WorkspaceProjectHelper.deleteProject("p1");
    }

    public void testPageCreation() throws Exception {
        Composite parent = new Composite(new Shell(), SWT.NONE);
        this.page.createControl(parent);
        Button natureButton = (Button)this.objectRegistry.getObject("nature");
        assertNotNull(natureButton);
        natureButton.setSelection(true);
        Event event = new Event();
        event.item = natureButton;
        natureButton.notifyListeners(SWT.Selection, event);
        assertTrue(this.page.hasWikiNature());
	}
    
    public void testProjectSetup() throws Exception {
        assertFalse(project1.hasNature(WikiNature.ID));
        Composite parent = new Composite(new Shell(), SWT.NONE);
        this.page.createControl(parent);
        Button natureButton = (Button)this.objectRegistry.getObject("nature");
        assertNotNull(natureButton);
        natureButton.setSelection(true);
        Event event = new Event();
        event.item = natureButton;
        natureButton.notifyListeners(SWT.Selection, event);
        assertTrue(this.page.performOk());
        assertTrue(project1.hasNature(WikiNature.ID));
    }

}
