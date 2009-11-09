
package com.xpn.xwiki.wiked.confluence.test.ui;

import junit.framework.TestCase;

import org.eclipse.ui.views.properties.IPropertySource;

import com.xpn.xwiki.wiked.confluence.ui.NewXWikiServerConnectionWizard;
import com.xpn.xwiki.wiked.repository.IRepository;

/**
 * Tests first page of new connection dialog
 * @author psenicka_ja
 */
public class NewServerConnectionWizardTest extends TestCase {

    private NewXWikiServerConnectionWizard wizard;
    
	protected void setUp() throws Exception {
		this.wizard = new NewXWikiServerConnectionWizard();
        this.wizard.addPages();
	}
    
	public void testServerDetailsPage() throws Exception {
        assertEquals(1, wizard.getPageCount());
		NewXWikiServerConnectionWizard.ServerDetailsPage page = 
            wizard.getServerDetailsPage();
        wizard.addPages();
        page.setName("name1");
        page.setHost("localhost");
        page.setPort("8080");
        page.setUserName("user1");
        page.setPassword("pwd1");
        IRepository repo = page.createServer();
        assertEquals("name1", repo.getName());
        assertEquals("http://localhost:8080", ((IPropertySource)repo).getPropertyValue("serverUrl"));
        assertEquals("user1", ((IPropertySource)repo).getPropertyValue("userName"));
	}
}
