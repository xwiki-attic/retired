
package com.xpn.xwiki.wiked.test.repository;

import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;

import com.xpn.xwiki.wiked.internal.repository.ProjectRepository;
import com.xpn.xwiki.wiked.internal.repository.RepositoryManager;
import com.xpn.xwiki.wiked.repository.IRemoteRepository;
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.IRepositoryListener;
import com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent;
import com.xpn.xwiki.wiked.test.WorkspaceProjectHelper;

/**
 * 
 * @author psenicka_ja
 */
public class RepositoryManagerTest extends TestCase {

    private IProject project1;
    private IProject project2;
	private RepositoryManager manager;

	protected void setUp() throws Exception {
        try {
        	this.project1 = WorkspaceProjectHelper.createProject("p1", 
                "com.xpn.xwiki.wiked.wiki");
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
	}

	protected void tearDown() throws Exception {
		this.manager.shutdown();
        WorkspaceProjectHelper.deleteProject("p1");
        WorkspaceProjectHelper.deleteProject("p2");
        WorkspaceProjectHelper.deleteProject("p11");
	}
    
    /**
     * Reads one project bookmark and compares it's values with expected one
     */
	public void testLocalBookmarkReader() throws Exception {
        this.manager = new RepositoryManager();
        RepositoryManager.BookmarksReader reader = 
            new RepositoryManager.BookmarksReader(manager,
                getClass().getResourceAsStream("RepositoryManagerTest-1.xml")); 
        List bookmarks = reader.readBookmarks();
        assertNotNull(bookmarks);
        assertEquals(1, bookmarks.size());
        IRepository bookmark = (IRepository)bookmarks.get(0);
        assertNotNull(bookmark);
        assertTrue(bookmark instanceof ProjectRepository);
        assertEquals("p1", bookmark.getName());
        assertEquals("project", bookmark.getType());
    }
    
    /**
     * Reads one remote bookmark and compares it's values with expected one
     */
    public void testRemoteBookmarkReader() throws Exception {
        this.manager = new RepositoryManager();
        RepositoryManager.BookmarksReader reader = 
            new RepositoryManager.BookmarksReader(manager, 
                getClass().getResourceAsStream("RepositoryManagerTest-2.xml")); 
        List bookmarks = reader.readBookmarks();
        assertNotNull(bookmarks);
        assertEquals(1, bookmarks.size());
        IRepository bookmark = (IRepository)bookmarks.get(0);
        assertNotNull(bookmark);
        assertTrue(bookmark instanceof IRemoteRepository);
        assertEquals("P1", bookmark.getName());
        assertEquals("xwiki", bookmark.getType());
        assertEquals("xxx", ((IRemoteRepository)bookmark).getUserName());
    }
    
    /**
     * Instantiates a RepositoryManager, which uses readers to iterate over
     * all known bookmarks.
     */
    public void testAllRepositories() throws Exception {
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        IRepository[] repositories = manager.getRepositories();
        assertNotNull(repositories);
        assertEquals(1, repositories.length);
        IRepository repository = (IRepository)repositories[0];
        assertNotNull(repository);
        assertTrue(repository instanceof ProjectRepository);
        assertEquals("p1", repository.getName());
        assertEquals("project", repository.getType());
    }

    /**
     * Instantiates a RepositoryManager, which uses readers to select one from
     * known bookmarks.
     */
    public void testRepositoryLookup() throws Exception {
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        IRepository repository = manager.getRepository("p1");
        assertNotNull(repository);
        assertTrue(repository instanceof ProjectRepository);
        assertEquals("p1", repository.getName());
        assertEquals("project", repository.getType());
    }

    /**
     * Instantiates a RepositoryManager, which uses readers to select one from
     * known bookmarks.
     */
    public void testRepositoryAdd() throws Exception {
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        IRemoteRepository repository = (IRemoteRepository)manager.
            createRepository("xwiki", null);
        repository.setName("P2");
        repository.setURL(new URL("http://www.seznam.cz"));
        repository.setUserName("xxx");
        repository.setPassword("yyy");
        manager.addRepository(repository);
        IRepository bookmark = manager.getRepository("P2");
        assertTrue(bookmark instanceof IRemoteRepository);
        assertEquals("P2", bookmark.getName());
        assertEquals("remote", bookmark.getType());
        assertEquals("xxx", ((IRemoteRepository)bookmark).getUserName());
        assertEquals("yyy", ((IRemoteRepository)bookmark).getPassword());
    }

    /**
     * Make sure the change on repository properties will be propagated to the
     * outside world using IRepositoryListener
     */
    public void testRepositoryModify() throws Exception {
        final ObjectWrapper wrapper = new ObjectWrapper();
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        manager.addChangeListener(new IRepositoryListener() {
            public void repositoryChanged(RepositoryObjectChangeEvent event) {
                wrapper.object = event;
            }   
        });
        IRepository repository = manager.getRepository("p1");
        assertNotNull(repository);
        repository.setName("p11");
        assertEquals(RepositoryObjectChangeEvent.MODIFY, 
            ((RepositoryObjectChangeEvent)wrapper.object).getEventType());
        assertEquals("p11", ((RepositoryObjectChangeEvent)wrapper.object).
            getRepositoryObject().getName());
    }
    
    /**
     * Handle wrong (unknown) bookmark records.
     */
    public void testInvalidProjectRepository() throws Exception {
		this.manager = null;
		try {
	        manager = new RepositoryManager(
	            getClass().getResourceAsStream("RepositoryManagerTest-3.xml"));
		} catch (IllegalStateException ex) {
			// OK
		}
        IRepository[] repositories = manager.getRepositories();
        assertNotNull(repositories);
        assertEquals(1, repositories.length);
        assertNotNull(manager.getRepository("p1"));
        assertNull(manager.getRepository("PX"));
    }
    
    /**
     * Make sure newly created project with proper nature will be loaded by the
     * this.automatically
     */
    public void testProjectLoad() throws Exception {
        this.project2 = WorkspaceProjectHelper.createProject("p2", 
            "com.xpn.xwiki.wiked.wiki");
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        IRepository repository = manager.getRepository("p2");
        assertNotNull(repository);
    }

    /**
     * Make sure newly created project with proper nature will be loaded by the
     * RepositoryManager automatically
     */
    public void testProjectCreate() throws Exception {
        final ObjectWrapper wrapper = new ObjectWrapper();
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        manager.addChangeListener(new IRepositoryListener() {
            public void repositoryChanged(RepositoryObjectChangeEvent event) {
                wrapper.object = event;
            }   
        });
        this.project2 = WorkspaceProjectHelper.createProject("p2", 
            "com.xpn.xwiki.wiked.wiki");
        ProjectRepository p2 = (ProjectRepository)manager.getRepository("p2");
        assertNotNull(p2);
        assertEquals(this.project2.getFullPath().toString(), 
            p2.getProject().getFullPath().toString());
    }

    /**
     * Make sure newly created project with proper nature will be loaded by the
     * RepositoryManager automatically
     */
    public void testRefresh() throws Exception {
        this.manager = new RepositoryManager();
        ProjectRepository p2 = (ProjectRepository)manager.getRepository("p2");
        assertNull(p2);
        this.project2 = WorkspaceProjectHelper.createProject("p2", 
            "com.xpn.xwiki.wiked.wiki");
        this.manager.refresh();
        p2 = (ProjectRepository)manager.getRepository("p2");
        assertNotNull(p2);
        assertEquals(this.project2.getFullPath().toString(), 
            p2.getProject().getFullPath().toString());
    }
    
    /**
     * Make sure newly created project with proper nature will be loaded by the
     * RepositoryManager automatically
     */
    public void testProjectAddNature() throws Exception {
        this.project2 = WorkspaceProjectHelper.createProject("p2");
        this.manager = new RepositoryManager();
        ProjectRepository p2 = (ProjectRepository)manager.getRepository("p2");
        assertNull(p2);
        WorkspaceProjectHelper.setProjectNature(this.project2, "com.xpn.xwiki.wiked.wiki");
        p2 = (ProjectRepository)manager.getRepository("p2");
        assertNotNull(p2);
        assertEquals(this.project2.getFullPath().toString(), 
            p2.getProject().getFullPath().toString());
    }

    /**
     * Make sure newly created project with proper nature will be loaded by the
     * RepositoryManager automatically and relevant change event will be fired.
     */
    public void testProjectCreateAndNotify() throws Exception {
        final ObjectWrapper wrapper = new ObjectWrapper();
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        manager.addChangeListener(new IRepositoryListener() {
            public void repositoryChanged(RepositoryObjectChangeEvent event) {
                if (RepositoryObjectChangeEvent.CREATE == event.getEventType()) {
                	wrapper.object = event;
                }
            }   
        });
        this.project2 = WorkspaceProjectHelper.createProject("p2", 
            "com.xpn.xwiki.wiked.wiki");
        assertEquals(RepositoryObjectChangeEvent.CREATE, 
            ((RepositoryObjectChangeEvent)wrapper.object).getEventType());
        assertEquals("p2", ((RepositoryObjectChangeEvent)wrapper.object).
            getRepositoryObject().getName());
    }

    /**
     * Make sure the change on project properties will be propagated to the
     * repository
     */
    public void testProjectModify() throws Exception {
        final ObjectWrapper wrapper = new ObjectWrapper();
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        manager.addChangeListener(new IRepositoryListener() {
            public void repositoryChanged(RepositoryObjectChangeEvent event) {
                wrapper.object = event;
            }   
        });
        IProjectDescription p1d = project1.getDescription();
        p1d.setName("p11");
        project1.move(p1d, true, null);
        assertNotNull(manager.getRepository("p11"));
    }

    /**
     * Make sure the change on project properties will be propagated to the
     * repository
     */
    public void testProjectModifyAndNotify() throws Exception {
        final ObjectWrapper wrapper1 = new ObjectWrapper();
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        manager.addChangeListener(new IRepositoryListener() {
            public void repositoryChanged(RepositoryObjectChangeEvent event) {
                wrapper1.object = event;
            }   
        });
        IProjectDescription p1d = project1.getDescription();
        p1d.setName("p11");
        project1.move(p1d, true, null);
        assertEquals(RepositoryObjectChangeEvent.MODIFY, 
            ((RepositoryObjectChangeEvent)wrapper1.object).getEventType());
        assertEquals("p1", ((RepositoryObjectChangeEvent)wrapper1.object).
            getRepositoryObject().getName());
    }

    /**
     * Make sure that removed project will diappear from the RepositoryManager 
     */
    public void testProjectDelete() throws Exception {
        final ObjectWrapper wrapper = new ObjectWrapper();
        this.manager = new RepositoryManager(
            getClass().getResourceAsStream("RepositoryManagerTest-1.xml"));
        manager.addChangeListener(new IRepositoryListener() {
            public void repositoryChanged(RepositoryObjectChangeEvent event) {
                if (RepositoryObjectChangeEvent.REMOVE == event.getEventType()) {
                	wrapper.object = event;
                }
            }   
        });
        assertEquals(1, manager.getRepositories().length);
        IRepository repository = manager.getRepository("p1");
        assertNotNull(repository);
        project1.delete(true, true, null);
        assertEquals(RepositoryObjectChangeEvent.REMOVE, 
            ((RepositoryObjectChangeEvent)wrapper.object).getEventType());
        assertEquals("p1", ((RepositoryObjectChangeEvent)wrapper.object).
            getRepositoryObject().getName());
    }

    private static class ObjectWrapper {
    	public Object object;
        public ObjectWrapper() {
        }
        public ObjectWrapper(Object object) {
            this.object = object;
        }
    }
}