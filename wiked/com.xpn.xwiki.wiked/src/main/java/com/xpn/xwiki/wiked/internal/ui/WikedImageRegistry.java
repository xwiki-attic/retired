package com.xpn.xwiki.wiked.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

public class WikedImageRegistry extends ImageRegistry {

    public static final String REPOSITORY = "repository";
    public static final String REPOSITORY_ERR = "repositoryErr";
    public static final String REPOSITORY_RO = "repositoryRO";
    public static final String SPACE = "space";
    public static final String PAGE = "page";
    public static final String PAGE_ERR = "pageErr";
    public static final String DELETE = "delete";
	public static final String COLLAPSE_ALL = "collapseall";
	public static final String REFRESH = "refresh";
	public static final String ADD_REPOSITORY = "addServer";
    public static final String ADD_SPACE = "addSpace";
    public static final String ADD_PAGE = "addPage";
	public static final String OPEN_IN_EXTERNAL_BROWSER = "openInExternalBrowser";
	public static final String COMPLETION = "completion";
    public static final String FORWARD = "forward";
    public static final String BACK = "back";
    public static final String STOP = "stop";
    public static final String NODE = "node";
    public static final String NEW_REPOSITORY = "newRepository";
    
//    public static final String IMG_EDIT_PAGE = "icons/editPage.gif";
//    public static final String IMG_DEL_PAGE = "icons/delPage.gif";
//    public static final String IMG_DEL_SERVER = "icons/delServer.gif";
//    public static final String IMG_ADD_PAGE = "icons/addPage.gif";
//    public static final String IMG_ADD_CHILD_PAGE = "icons/addChildPage.gif";
//    public static final String IMG_COMMENT = "icons/comment.gif";
//    public static final String IMG_ATTACHMENT = "icons/attachment.gif";
//    public static final String IMG_USER = "icons/user.gif";
//    public static final String IMG_BLOGENTRY = "icons/blogentry.gif";
//    public static final String IMG_RENAME_PAGE = "icons/renamePage.gif";
//    public static final String IMG_REFRESH = "icons/refresh.gif";
//    public static final String IMG_ADDSERVER = "icons/addServer.gif";
//    public static final String IMG_REFRESH_NODE = "icons/refreshServer.gif";
//    public static final String IMG_SEARCH = "icons/search.gif";
//    public static final Object IMG_MAIL = "icons/mail.gif";

    // overlay
//    private static final String IMG_READONLY_DECORATOR = "icons/readonly-decorator.gif";
//    private static final String IMG_READONLY_PAGE = "readonlypage";
//    private static final String IMG_READONLY_HOMEPAGE = "readonlyhomepage";
//    private static final String IMG_READONLY_SPACE = "readonlyspace";
    
    public static void configure(ImageRegistry registry, URL root) 
        throws MalformedURLException {
        registry.put(REPOSITORY, getImageDescriptor(root, REPOSITORY));
        registry.put(REPOSITORY_ERR, getImageDescriptor(root, REPOSITORY, true, false));
        registry.put(REPOSITORY_RO, getImageDescriptor(root, REPOSITORY, false, true));
        registry.put(SPACE, getImageDescriptor(root, SPACE));
        registry.put(PAGE, getImageDescriptor(root, PAGE));
        registry.put(PAGE_ERR, getImageDescriptor(root, PAGE, true, false));
        registry.put(DELETE, getImageDescriptor(root, DELETE));
        registry.put(REFRESH, getImageDescriptor(root, REFRESH));
        registry.put(COLLAPSE_ALL, getImageDescriptor(root, COLLAPSE_ALL));

        registry.put(FORWARD, getImageDescriptor(root, FORWARD));
        registry.put(BACK, getImageDescriptor(root, BACK));
        registry.put(STOP, getImageDescriptor(root, STOP));
        registry.put(NODE, getImageDescriptor(root, NODE));
        registry.put(NEW_REPOSITORY, getImageDescriptor(root, NEW_REPOSITORY));
    }
    
    private static ImageDescriptor getImageDescriptor(URL root, String key) 
        throws MalformedURLException {
        return getImageDescriptor(root, key, false, false);
    }
    
    private static ImageDescriptor getImageDescriptor(URL root, String key, 
        boolean error, boolean readOnly) throws MalformedURLException {
        URL url = new URL(root, "icons/"+key+".gif");
        ImageDescriptor base = ImageDescriptor.createFromURL(url);
        if (error) {
        	ImageDescriptor errorImage = getErrorOverlay(root);
            base = new DecoratorOverlayImageDescriptor(base.createImage(), 
                errorImage, DecoratorOverlayImageDescriptor.BOTTOM_RIGHT);
        }
        if (readOnly) {
            ImageDescriptor roImage = getReadOnlyOverlay(root);
            base = new DecoratorOverlayImageDescriptor(base.createImage(), 
                roImage, DecoratorOverlayImageDescriptor.BOTTOM_LEFT);
        }
        
        return base;
    }

	private static ImageDescriptor getErrorOverlay(URL root) 
        throws MalformedURLException {
        return ImageDescriptor.createFromURL(new URL(root, "icons/error_co.gif"));
    }

    private static ImageDescriptor getWarningOverlay(URL root) 
        throws MalformedURLException {
        return ImageDescriptor.createFromURL(new URL(root, "icons/warning_co.gif"));
    }

	private static ImageDescriptor getReadOnlyOverlay(URL root) 
        throws MalformedURLException {
        return ImageDescriptor.createFromURL(new URL(root, "icons/readonly_co.gif"));
	}
}
