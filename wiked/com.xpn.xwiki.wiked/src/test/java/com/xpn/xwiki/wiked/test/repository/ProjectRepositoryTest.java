
package com.xpn.xwiki.wiked.test.repository;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import com.xpn.xwiki.wiked.internal.repository.FilePage;
import com.xpn.xwiki.wiked.internal.repository.FolderSpace;
import com.xpn.xwiki.wiked.internal.repository.ProjectRepository;
import com.xpn.xwiki.wiked.internal.repository.RepositoryManager;
import com.xpn.xwiki.wiked.repository.IPage;
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.IRepositoryListener;
import com.xpn.xwiki.wiked.repository.ISpace;
import com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent;
import com.xpn.xwiki.wiked.test.WorkspaceProjectHelper;

public class ProjectRepositoryTest extends TestCase {

	private IProject project1;
	private RepositoryManager manager;

	protected void setUp() throws Exception {
		try {
			this.project1 = WorkspaceProjectHelper.createProject("p1",
					"com.xpn.xwiki.wiked.wiki");
			this.manager = new RepositoryManager(
			    getClass().getResourceAsStream("ProjectRepositoryTest-1.xml"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void tearDown() throws Exception {
		this.manager.shutdown();
		WorkspaceProjectHelper.deleteProject("p1");
	}

	/**
	 * Reads one project bookmark and compares it's values with expected one
	 */
	public void testCreateSpace() throws Exception {
		IRepository[] bookmarks = manager.getRepositories();
		assertNotNull(bookmarks);
		assertEquals(1, bookmarks.length);
		IRepository repository = bookmarks[0];
		assertNotNull(repository);
		assertTrue(repository instanceof ProjectRepository);
		assertEquals("p1", repository.getName());
		assertEquals("project", repository.getType());
		ISpace space = repository.createSpace("space1");
		assertNotNull(space);
		assertEquals("space1", space.getName());
		assertEquals("space1", repository.getSpaces(null)[0].getName());
		IFolder folder = project1.getFolder("space1");
		assertNotNull(folder);
		assertTrue(folder.exists());
	}

	public void testChangeEventAfterCreateSpace() throws Exception {
		IRepository[] bookmarks = manager.getRepositories();
		assertNotNull(bookmarks);
		assertEquals(1, bookmarks.length);
		IRepository repository = bookmarks[0];
		assertNotNull(repository);
		assertTrue(repository instanceof ProjectRepository);
		assertEquals("p1", repository.getName());
		assertEquals("project", repository.getType());
		final List events = new ArrayList();
		manager.addChangeListener(new IRepositoryListener() {
			public void repositoryChanged(RepositoryObjectChangeEvent event) {
				events.add(event);
			}
		});
		ISpace space = repository.createSpace("space1");
		assertNotNull(space);
		assertEquals("space1", space.getName());
		assertEquals("space1", repository.getSpaces(null)[0].getName());
		IFolder folder = project1.getFolder("space1");
		assertNotNull(folder);
		assertTrue(folder.exists());
		assertEquals(1, events.size());
		RepositoryObjectChangeEvent event = 
			(RepositoryObjectChangeEvent)events.get(0);
		assertNotNull(event);
		assertEquals(FolderSpace.class.getName(), 
			event.getRepositoryObject().getClass().getName());
		assertEquals("space1", event.getRepositoryObject().getName());
		assertEquals(RepositoryObjectChangeEvent.CREATE, event.getEventType());
	}
	
	public void testChangeEventAfterDeleteSpace() throws Exception {
		IRepository[] bookmarks = manager.getRepositories();
		assertNotNull(bookmarks);
		assertEquals(1, bookmarks.length);
		IRepository repository = bookmarks[0];
		assertNotNull(repository);
		assertTrue(repository instanceof ProjectRepository);
		assertEquals("p1", repository.getName());
		assertEquals("project", repository.getType());
		final List events = new ArrayList();
		ISpace space = repository.createSpace("space1");
		assertNotNull(space);
		manager.addChangeListener(new IRepositoryListener() {
			public void repositoryChanged(RepositoryObjectChangeEvent event) {
				events.add(event);
			}
		});
		space = repository.removeSpace("space1");
		assertNotNull(space);
		IFolder folder = project1.getFolder("space1");
		assertNotNull(folder);
		assertFalse(folder.exists());
		assertEquals(1, events.size());
		RepositoryObjectChangeEvent event = 
			(RepositoryObjectChangeEvent)events.get(0);
		assertNotNull(event);
		assertEquals(FolderSpace.class.getName(), 
			event.getRepositoryObject().getClass().getName());
		assertEquals("space1", event.getRepositoryObject().getName());
		assertEquals(RepositoryObjectChangeEvent.REMOVE, event.getEventType());
	}
	
	/**
	 * Reads one project bookmark and compares it's values with expected one
	 */
	public void testCreatePage() throws Exception {
		IRepository[] bookmarks = manager.getRepositories();
		assertNotNull(bookmarks);
		assertEquals(1, bookmarks.length);
		IRepository repository = bookmarks[0];
		assertNotNull(repository);
		assertTrue(repository instanceof ProjectRepository);
		assertEquals("p1", repository.getName());
		assertEquals("project", repository.getType());
		ISpace space = repository.createSpace("space1");
		assertNotNull(space);
		assertEquals("space1", space.getName());
		assertEquals("space1", repository.getSpaces(null)[0].getName());
		IFolder folder = project1.getFolder("space1");
		assertNotNull(folder);
		assertTrue(folder.exists());
		IPage page = space.createPage("page1");
		assertNotNull(page);
		assertEquals("page1.wiki", page.getName());
		assertEquals("page1", page.getTitle());
		assertEquals("page1.wiki", repository.getSpaces(null)[0].
		     getPages(null)[0].getName());
		IFile file = folder.getFile("page1.wiki");
		assertNotNull(file);
		assertTrue(file.exists());
	}

	public void testChangeEventAfterCeratePage() throws Exception {
		IRepository[] bookmarks = manager.getRepositories();
		assertNotNull(bookmarks);
		assertEquals(1, bookmarks.length);
		IRepository repository = bookmarks[0];
		assertNotNull(repository);
		assertTrue(repository instanceof ProjectRepository);
		assertEquals("p1", repository.getName());
		assertEquals("project", repository.getType());
		ISpace space = repository.createSpace("space1");
		assertNotNull(space);
		assertEquals("space1", space.getName());
		assertEquals("space1", repository.getSpaces(null)[0].getName());
		IFolder folder = project1.getFolder("space1");
		assertNotNull(folder);
		assertTrue(folder.exists());
		final List events = new ArrayList();
		manager.addChangeListener(new IRepositoryListener() {
			public void repositoryChanged(RepositoryObjectChangeEvent event) {
				events.add(event);
			}
		});
		IPage page = space.createPage("page1");
		assertNotNull(page);
		assertEquals("page1.wiki", page.getName());
		assertEquals("page1", page.getTitle());
		assertEquals("page1.wiki", repository.getSpaces(null)[0].
		     getPages(null)[0].getName());
		IFile file = folder.getFile("page1.wiki");
		assertNotNull(file);
		assertTrue(file.exists());
		assertEquals(1, events.size());
		RepositoryObjectChangeEvent event = 
			(RepositoryObjectChangeEvent)events.get(0);
		assertNotNull(event);
		assertEquals(FilePage.class.getName(), 
			event.getRepositoryObject().getClass().getName());
		assertEquals("page1.wiki", event.getRepositoryObject().getName());
		assertEquals(RepositoryObjectChangeEvent.CREATE, event.getEventType());
	}

	public void testChangeEventAfterDeletePage() throws Exception {
		IRepository[] bookmarks = manager.getRepositories();
		assertNotNull(bookmarks);
		assertEquals(1, bookmarks.length);
		IRepository repository = bookmarks[0];
		assertNotNull(repository);
		assertTrue(repository instanceof ProjectRepository);
		assertEquals("p1", repository.getName());
		assertEquals("project", repository.getType());
		ISpace space = repository.createSpace("space1");
		assertNotNull(space);
		assertEquals("space1", space.getName());
		assertEquals("space1", repository.getSpaces(null)[0].getName());
		IFolder folder = project1.getFolder("space1");
		assertNotNull(folder);
		assertTrue(folder.exists());
		final List events = new ArrayList();
		IPage page = space.createPage("page1");
		assertNotNull(page);
		manager.addChangeListener(new IRepositoryListener() {
			public void repositoryChanged(RepositoryObjectChangeEvent event) {
				events.add(event);
			}
		});
		page = space.removePage("page1");
		assertNotNull(page);
		IFile file = folder.getFile("page1.wiki");
		assertNotNull(file);
		assertFalse(file.exists());
		assertEquals(1, events.size());
		RepositoryObjectChangeEvent event = 
			(RepositoryObjectChangeEvent)events.get(0);
		assertNotNull(event);
		assertEquals(FilePage.class.getName(), 
			event.getRepositoryObject().getClass().getName());
		assertEquals("page1.wiki", event.getRepositoryObject().getName());
		assertEquals(RepositoryObjectChangeEvent.REMOVE, event.getEventType());
	}
}
