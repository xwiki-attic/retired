
package com.xpn.xwiki.wiked.internal.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.WikiNature;
import com.xpn.xwiki.wiked.repository.IBookmarkSaveParticipant;
import com.xpn.xwiki.wiked.repository.IRepository;
import com.xpn.xwiki.wiked.repository.IRepositoryFactory;
import com.xpn.xwiki.wiked.repository.IRepositoryListener;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;
import com.xpn.xwiki.wiked.repository.RepositoryDescriptor;
import com.xpn.xwiki.wiked.repository.RepositoryException;
import com.xpn.xwiki.wiked.repository.RepositoryObjectChangeEvent;

public class RepositoryManager implements IRepositoryManager, 
    IResourceChangeListener {

	private final static String BOOKMARK_FILENAME = "bookmarks.xml";
    private final static String ELEMENT_BOOKMARKS = "bookmarks";
    private final static String ELEMENT_BOOKMARK = "bookmark";
    private final static String ATTR_TYPE = "type";
    private final static String ATTR_NAME = "name";

    private Map repositoryFactories;
	private Map repositories;
	private List listeners;
    
    static IPath getConfigPath() {
        return WikedPlugin.getInstance().getStateLocation().append(BOOKMARK_FILENAME);
    }
    
	public RepositoryManager() {
        this.repositoryFactories = new HashMap();
        this.repositories = new HashMap();
        this.listeners = new ArrayList(2);
        try {
	        loadRepositoryFactories();
	        File bookmarksFile = getConfigPath().toFile();
	        InputStream bookmarksStream = null;
	        if (bookmarksFile.exists()) {
	        	try {
	        		bookmarksStream = new FileInputStream(bookmarksFile);
	        	} catch (FileNotFoundException ex) {
	        		bookmarksStream = null;
	        		WikedPlugin.log("bookmarks.xml not found, using default");
	        	}
	        }
	        if (bookmarksStream == null) {
        		bookmarksStream = getClass().getResourceAsStream("/wiked.bookmarks");
	        }
	        loadRepositories(bookmarksStream);
        } catch (Exception ex) {
            WikedPlugin.logError("Error while reading the configuration", ex);
        }
	}

	public RepositoryManager(InputStream bookmarksStream) {
        this.repositoryFactories = new HashMap();
        this.repositories = new HashMap();
        this.listeners = new ArrayList(2);
        try {
            loadRepositoryFactories();
            loadRepositories(bookmarksStream);
        } catch (Exception ex) {
            WikedPlugin.logError("Error while reading the configuration", ex);
        }
    }

    public String[] getRepositoryTypes() {
        String[] arr = new String[this.repositoryFactories.size()];
        return (String[])this.repositoryFactories.keySet().toArray(arr);
    }
    
    public RepositoryDescriptor getRepositoryDescriptor(String name) {
    	if (name == null || name.length() == 0) {
    		throw new IllegalArgumentException("no name");
    	}
        return (RepositoryDescriptor)this.repositoryFactories.get(name);
    }

    public IRepository[] getRepositories() {
		return (IRepository[])this.repositories.values().toArray(
            new IRepository[repositories.size()]);
	}

	public IRepository getRepository(String name) {
    	if (name == null || name.length() == 0) {
    		throw new IllegalArgumentException("no name");
    	}
        return (IRepository)this.repositories.get(name);
	}

	public IRepository createRepository(String type, Object dataObject) 
        throws RepositoryException {
		if (ProjectRepository.TYPE.equals(type)) {
            if (dataObject instanceof IProject) {
            	try {
					return new ProjectRepository((IProject)dataObject);
				} catch (MalformedURLException ex) {
					IllegalArgumentException iaex = 
                        new IllegalArgumentException("illegal data "+dataObject);				
                    iaex.initCause(ex);
                    throw iaex;
                }
            } else {
            	throw new IllegalArgumentException("unknown data "+dataObject);
            }
        } else if (this.repositoryFactories.containsKey(type)) {
            IRepositoryFactory factory = (IRepositoryFactory)
                this.repositoryFactories.get(type);
            return factory.createRepository(type, dataObject);
        }
        
        throw new IllegalArgumentException("unknown type "+type);
	}
    
	public void addRepository(IRepository repository) {
    	if (repository == null) {
    		throw new IllegalArgumentException("no repository");
    	}
        if (repository instanceof AbstractRepository) {
        	((AbstractRepository)repository).setManager(this);
        }
		Object old = this.repositories.put(repository.getName(), repository);
		notifyListeners(new RepositoryObjectChangeEvent(repository,
			(old != null) ? RepositoryObjectChangeEvent.MODIFY : 
            RepositoryObjectChangeEvent.CREATE));
	}

	public void removeRepository(String name) {
    	if (name == null || name.length() == 0) {
    		throw new IllegalArgumentException("no name");
    	}
        IRepository old = (IRepository)this.repositories.remove(name);
        if (old != null) {
        	notifyListeners(new RepositoryObjectChangeEvent(old,
                RepositoryObjectChangeEvent.REMOVE)); 
        }
	}

	public void addChangeListener(IRepositoryListener listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(IRepositoryListener listener) {
		listeners.remove(listener);
	}

	public void shutdown() throws Exception {
		saveRepositories();      
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

    public void refresh() throws RepositoryException {
        Iterator repoi = this.repositories.values().iterator();
        while (repoi.hasNext()) {
            IRepository repository = (IRepository)repoi.next();
            if (repository instanceof ProjectRepository) {
            	IProject project = ((ProjectRepository)repository).getProject();
                if (project.exists() == false || project.isOpen() == false) {
                	removeRepository(project.getName());
                    continue;
                }
            }
            
        	repository.refresh();
        }
        IWorkspaceRoot wsr = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] projects = wsr.getProjects();
        for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			if (this.repositories.containsKey(project.getName()) == false) {
                try {
    				if (project.hasNature(WikiNature.ID)) {
                        addRepository(new ProjectRepository(project));
                    }
                } catch (Exception ex) {
                	WikedPlugin.handleError("Cannot load project "+project.getName(), ex);
                }
            }
		}
    }

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            event.getDelta().accept(new IResourceDeltaVisitor() {
                public boolean visit(IResourceDelta delta) {
                    int kind = delta.getKind();
                    IResource resource = delta.getResource();
                    if (resource instanceof IProject) {
                        IProject project = (IProject)resource;
                        IRepository repo = findRepositoryByProjectName(project.getName());
                        try {
                            switch (kind) {
                                case IResourceDelta.ADDED:
                                    if (repo == null) {
    									repo = createRepository(ProjectRepository.TYPE, resource);
                                        addRepository(repo);
                                    } else {
                                        repo.setName(project.getName());
                                    }
    								break;
                                case IResourceDelta.DESCRIPTION:
                                    if (repo == null) {
    									repo = createRepository(ProjectRepository.TYPE, resource);
                                        addRepository(repo);
                                    } else {
                                        repo.setName(project.getName());
                                    }
                                    break;
                                case IResourceDelta.MOVED_TO:
                                    if (repo != null) {
                                        repo.setName(project.getName());
                                    }
                                    break;
                                case IResourceDelta.REMOVED:
                                    if (repo != null) {
                                        removeRepository(repo.getName());
                                    }
                                    break;
                            }
                        } catch (RepositoryException ex) {
                            WikedPlugin.logError("cannot process "+resource, ex);
                        }
                       	return false;
                    }
                    return true; // visit the children
                }
            });
        } catch (CoreException ex) {
        	WikedPlugin.logError("error during resource chg", ex);
        }
    }

    void notifyListeners(RepositoryObjectChangeEvent event) {
		IRepositoryListener[] larr = (IRepositoryListener[]) 
            listeners.toArray(new IRepositoryListener[listeners.size()]);
		for (int i = 0; i < larr.length; i++) {
			larr[i].repositoryChanged(event);
		}
	}

    private void visit(IResourceDelta[] deltas) {
        for (int i = 0; i < deltas.length; i++) {
        }
    }

    private void loadRepositoryFactories() throws CoreException {
    	IExtensionRegistry registry = Platform.getExtensionRegistry();
//    	IPluginRegistry pluginRegistry = Platform.getPluginRegistry();
    	IExtensionPoint point = registry.getExtensionPoint(WikedPlugin.ID, "repository");
    	if (point != null) {
    		IExtension[] extensions = point.getExtensions();
    		for (int i = 0; i < extensions.length; i++) {
    			IExtension currentExtension = extensions[i];
                IConfigurationElement[] configElements = currentExtension.getConfigurationElements();
                for (int j = 0; j < configElements.length; j++) {
                    try {
                        RepositoryDescriptor descriptor = new RepositoryDescriptor(configElements[i]);
                        String factoryType = descriptor.getType();
                        if (this.repositoryFactories.put(factoryType, descriptor) != null) {
                            WikedPlugin.logError("overriding factory "+descriptor.getType(), null);
                        }
                    } catch (Exception ex) {
                        WikedPlugin.logError("cannot create repository", ex);
                    }
                }
            }
        }
    }
    
    private void loadRepositories(InputStream bookmarksStream) 
        throws WorkbenchException, IOException {
        BookmarksReader reader = new BookmarksReader(this, bookmarksStream);
        List bookmarks = reader.readBookmarks();
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        Set sync = new HashSet();
        
        // Iterate over bookmarked projects
        Iterator iter = bookmarks.iterator();
        while (iter.hasNext()) {
            IRepository repository = (IRepository)iter.next();
            try {
                if (repository instanceof ProjectRepository) {
                    ProjectRepository prepo = (ProjectRepository)repository;
                	IProject project = ((ProjectRepository)repository).getProject();
                    if (project.exists() && project.hasNature(WikiNature.ID)) {
                        sync.add(project.getName());
                        addRepository(repository);
                    } else {
                        iter.remove();
                    }
                } else {
                    addRepository(repository);
                }
            } catch (Exception ex) {
                WikedPlugin.logError("cannot load "+repository.getName(), ex);
            }
        }
        
        // Add new projects with proper nature
        if (ws != null) {
            IProject[] projects = ws.getRoot().getProjects();
            for (int i = 0; i < projects.length; i++) {
                IProject project = projects[i];
                String name = project.getName();
                try {
                    if (sync.contains(name) == false && project.exists() && 
                        project.hasNature(WikiNature.ID)) {
                        sync.add(name);
                        addRepository(new ProjectRepository(project));
                    }
                } catch (Exception ex) {
                    WikedPlugin.logError("cannot load "+project.getName(), ex);
                }
    		}

            ws.addResourceChangeListener(this); //, IResourceChangeEvent.POST_CHANGE);
        }
    }

    private IRepository findRepositoryByProjectName(String projectName) {
    	Iterator repoi = this.repositories.values().iterator();
    	while (repoi.hasNext()) {
    		IRepository repo = (IRepository)repoi.next();
            if (repo instanceof ProjectRepository) {
                String name = ((ProjectRepository)repo).getProject().getName();
            	if (projectName.equals(name)) {
                    return repo;
                }
            }
        }

        return null;
    }
    
    private void saveRepositories() throws WorkbenchException, IOException {
        BookmarksWriter writer = new BookmarksWriter(getConfigPath().toFile());
        writer.writeBookmarks(repositories.values());
    }
                        
    /**
     * Reader of bookmark file.
     * TODO Use extension mechanism to register different types of repositories
     * and relevant readers.
     */
    public static class BookmarksReader {
        
        private InputStream bookmarksStream;
		private RepositoryManager manager;
        
        public BookmarksReader(RepositoryManager manager, File bookmarksFile) 
            throws FileNotFoundException {
            this(manager, new FileInputStream(bookmarksFile));
        }
        
        public BookmarksReader(RepositoryManager manager, InputStream stream) {
            this.manager = manager;
            this.bookmarksStream = stream;
        }
        
        public List readBookmarks() 
            throws IOException, WorkbenchException {
            List bookmarkList = new ArrayList();
            InputStreamReader reader = new InputStreamReader(bookmarksStream);
            IMemento memento = XMLMemento.createReadRoot(reader);
            IMemento[] children = memento.getChildren(ELEMENT_BOOKMARK);
            for (int i = 0; i < children.length; i++) {
                try {
                	IRepository bookmark = readBookmark(children[i]);    
                    if (bookmark != null) {
                        bookmarkList.add(bookmark);
                    }
                } catch (Exception ex) {
                    WikedPlugin.handleError("Cannot read "+children[i], ex);
                }
            }
            
            return bookmarkList;
        }

        private IRepository readBookmark(IMemento bookmark) 
            throws MalformedURLException, CoreException, RepositoryException {
            String type = bookmark.getString(ATTR_TYPE);
            RepositoryDescriptor desc = manager.getRepositoryDescriptor(type);
            if (ProjectRepository.TYPE.equals(type)) {
                String projectName = bookmark.getTextData();
                if (projectName != null && projectName.length() > 0) {
	                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	                IProject project = root.getProject(projectName);
					if (project != null && project.isAccessible()) {
						return new ProjectRepository(project);
					}
                }
            } else if (desc != null) {
                return desc.createRepository(type, bookmark);
            }
            throw new IllegalStateException("cannot read "+bookmark);
        }
    }
    
    /**
     * Writer of bookmark to file
     * TODO Use extension mechanism to register different types of repositories
     * and relevant writers.
     */
    private static class BookmarksWriter {
        
        private File bookmarksFile;
        
        public BookmarksWriter(File bookmarksFile) {
            this.bookmarksFile = bookmarksFile;
        }

        public void writeBookmarks(Collection bookmarks) 
            throws IOException {
	    	if (bookmarks == null) {
	    		throw new IllegalArgumentException("no bookmarks");
	    	}
            if (bookmarks.size() == 0) {
                getConfigPath().toFile().delete();
            } else {
                XMLMemento memento = XMLMemento.createWriteRoot(ELEMENT_BOOKMARKS);
                Iterator iter = bookmarks.iterator();
                while (iter.hasNext()) {
                    writeBookmark((IRepository)iter.next(), memento);
                }
                memento.save(new FileWriter(getConfigPath().toFile()));
            }
        }

		private void writeBookmark(IRepository bmk, IMemento memento) {
            IMemento bookmarkData = memento.createChild(ELEMENT_BOOKMARK);
            bookmarkData.putString(ATTR_TYPE, bmk.getType());
            bookmarkData.putString(ATTR_NAME, bmk.getName());
            if (bmk instanceof IBookmarkSaveParticipant) {
                IBookmarkSaveParticipant savep = (IBookmarkSaveParticipant)bmk;
            	savep.storeBookmark(bookmarkData);
            }
		}
    }
    
}