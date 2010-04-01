package com.xpn.xwiki.plugin.workspacesmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jmock.Mock;
import org.jmock.core.Invocation;
import org.jmock.core.stub.CustomStub;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.notify.XWikiNotificationManager;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.XWikiPluginManager;
import com.xpn.xwiki.plugin.rightsmanager.RightsManagerPlugin;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceImpl;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceManagerImpl;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspacesActivityStreamPlugin;
import com.xpn.xwiki.plugin.workspacesmanager.apps.activities.WorkspacesActivityStreamPluginApi;
import com.xpn.xwiki.store.XWikiHibernateStore;
import com.xpn.xwiki.store.XWikiHibernateVersioningStore;
import com.xpn.xwiki.store.XWikiStoreInterface;
import com.xpn.xwiki.store.XWikiVersioningStoreInterface;
import com.xpn.xwiki.test.AbstractXWikiComponentTestCase;

/**
 * Setup copied from SpaceManagerTestImp until I find how to extend tests cross projects.
 * 
 * @see com.xpn.xwiki.plugin.spacemanager.impl.SpaceManagerImplTest
 * 
 * @version $Id :$
 */
public abstract class AbstractWorkspacesTest extends AbstractXWikiComponentTestCase
{

    /**
     * Name of the document holding the global rights class.
     */
    private static final String XWIKI_GLOBAL_RIGHTS_CLASSNAME = "XWiki.XWikiGlobalRights";

    /**
     * Name of the document holding the default Admin user profile on the xwiki database.
     */
    private static final String XWIKI_ADMIN_DOCNAME = "xwiki:XWiki.Admin";

    /**
     * The getPlugin method name of the plugin manager.
     */
    private static final String PLUGIN_MANAGER_GET_PLUGIN_METHOD = "getPlugin";

    /**
     * Name of the default database.
     */
    private static final String XWIKI_DATABASE_NAME = "xwiki";

    /**
     * The name of the rights manager plugin, used to retrieve it from the plugin manager.
     */
    private static final String RIGHT_MANAGER_PLUGIN_NAME = "rightsmanager";

    /**
     * The XWiki context used along the tests.
     */
    protected XWikiContext context;

    /**
     * The space manager instance used in the tests.
     */
    protected SpaceManagerImpl spaceManager;

    /**
     * XWiki object used in the tests.
     */
    protected XWiki xwiki;

    /**
     * Mock of a XWiki store.
     */
    protected Mock mockXWikiStore;

    /**
     * Mock of a XWiki versioning store.
     */
    protected Mock mockXWikiVersioningStore;

    /**
     * Mock of an activity stream plugin.
     */
    protected Mock mockXWSActivityStreamPlugin;

    /**
     * Mock of an activity stream plugin Api.
     */
    protected Mock mockXWSActivityStreamApi;

    /**
     * Mock of XWiki plugin manager.
     */
    protected Mock mockPluginManager;

    /**
     * Map of documents used by the store mock.
     */
    private Map docs = new HashMap();

    /**
     * Map of spaces used by the store mock.
     */
    private List spaces = new ArrayList();

    /**
     * Set up unit testing.
     * 
     * @throws Exception when an error occurred during the test setup.
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        this.context = new XWikiContext();
        this.context.setUser("XWiki.TestUser");
        this.xwiki = new XWiki();

        this.xwiki.setDatabase(XWIKI_DATABASE_NAME);

        this.context.setWiki(xwiki);
        this.context.setUser("XWiki.NotAdmin");
        this.context.setDatabase(XWIKI_DATABASE_NAME);
        this.context.setMainXWiki(XWIKI_DATABASE_NAME);

        this.xwiki.setConfig(new XWikiConfig());
        this.xwiki.setNotificationManager(new XWikiNotificationManager());

        RightsManagerPlugin rightManagerPlugin =
            new RightsManagerPlugin(RIGHT_MANAGER_PLUGIN_NAME, RightsManagerPlugin.class.getName(), context);

        this.mockXWSActivityStreamPlugin =
            mock(WorkspacesActivityStreamPlugin.class, new Class[] {String.class, String.class, XWikiContext.class},
                new Object[] {"", "", this.context});

        this.mockXWSActivityStreamApi =
            mock(WorkspacesActivityStreamPluginApi.class, new Class[] {
                WorkspacesActivityStreamPlugin.class, XWikiContext.class}, 
                new Object[] {new WorkspacesActivityStreamPlugin("", "", this.context), this.context});

        this.mockXWSActivityStreamApi.expects(once()).method("addDocumentActivityEvent");

        WorkspacesActivityStreamPluginApi wasApi = (WorkspacesActivityStreamPluginApi) mockXWSActivityStreamApi.proxy();

        this.mockXWSActivityStreamPlugin.expects(once()).method("getPluginApi").will(
            returnValue(mockXWSActivityStreamApi.proxy()));

        Vector pluginNames = new Vector();
        pluginNames.add("xwsactivitystream");
        pluginNames.add(RIGHT_MANAGER_PLUGIN_NAME);

        this.mockPluginManager = mock(XWikiPluginManager.class);
        this.mockPluginManager.stubs().method(PLUGIN_MANAGER_GET_PLUGIN_METHOD).with(eq(RIGHT_MANAGER_PLUGIN_NAME))
            .will(returnValue(rightManagerPlugin));

        this.mockPluginManager.stubs().method(PLUGIN_MANAGER_GET_PLUGIN_METHOD)
            .with(not(eq(RIGHT_MANAGER_PLUGIN_NAME))).will(returnValue(mockXWSActivityStreamPlugin.proxy()));
        this.mockPluginManager.stubs().method("getPlugins").will(returnValue(pluginNames));

        this.xwiki.setPluginManager((XWikiPluginManager) mockPluginManager.proxy());

        this.mockXWikiStore =
            mock(XWikiHibernateStore.class, new Class[] {XWiki.class, XWikiContext.class}, new Object[] {this.xwiki,
                this.context});
        this.mockXWikiStore.expects(atLeastOnce()).method("executeWrite");
        this.mockXWikiStore.stubs().method("loadXWikiDoc").will(
            new CustomStub("Implements XWikiStoreInterface.loadXWikiDoc")
            {
                public Object invoke(Invocation invocation) throws Exception
                {
                    XWikiDocument shallowDoc = (XWikiDocument) invocation.parameterValues.get(0);

                    if (docs.containsKey(shallowDoc.getFullName())) {
                        return (XWikiDocument) docs.get(shallowDoc.getFullName());
                    } else {
                        return shallowDoc;
                    }
                }
            });
        this.mockXWikiStore.stubs().method("saveXWikiDoc").will(
            new CustomStub("Implements XWikiStoreInterface.saveXWikiDoc")
            {
                public Object invoke(Invocation invocation) throws Throwable
                {
                    XWikiDocument document = (XWikiDocument) invocation.parameterValues.get(0);
                    document.setNew(false);
                    document.setStore((XWikiStoreInterface) mockXWikiStore.proxy());
                    docs.put(document.getFullName(), document);
                    if ((document.getName().equals("WebPreferences"))) {
                        String type = null;
                        try {
                            type = document.getStringValue(spaceManager.getSpaceClassName(), SpaceImpl.SPACE_TYPE);
                        } catch (Exception e) {
                        }
                        if (spaceManager.getSpaceTypeName().equals(type)) {
                            if (!spaces.contains("" + document.getSpace()))
                                spaces.add("" + document.getSpace());
                        }
                        if ("deleted".equals(type))
                            spaces.remove("" + document.getSpace());
                    }
                    return null;
                }
            });

        this.mockXWikiStore.stubs().method("saveXWikiObject");

        this.mockXWikiStore.stubs().method("getTranslationList").will(returnValue(Collections.EMPTY_LIST));
        this.mockXWikiStore.stubs().method("search").will(returnValue(spaces));

        this.mockXWikiStore.stubs().method("searchDocumentsNames").will(returnValue(new ArrayList()));

        this.mockXWikiVersioningStore =
            mock(XWikiHibernateVersioningStore.class, new Class[] {XWiki.class, XWikiContext.class}, new Object[] {
            this.xwiki, this.context});
        this.mockXWikiVersioningStore.stubs().method("getXWikiDocumentArchive").will(returnValue(null));

        this.xwiki.setStore((XWikiStoreInterface) mockXWikiStore.proxy());
        this.xwiki.setVersioningStore((XWikiVersioningStoreInterface) mockXWikiVersioningStore.proxy());

        this.spaceManager = new SpaceManagerImpl("spacemanager", SpaceManagerImpl.class.toString(), context);
        this.spaceManager.setMailNotification(false);
        this.spaceManager.init(context);

        XWikiDocument prefdoc = new XWikiDocument("XWiki", "XWikiPreferences");
        BaseObject obj = new BaseObject();
        obj.setName("XWiki.XWikiPreferences");
        obj.setClassName(XWIKI_GLOBAL_RIGHTS_CLASSNAME);
        obj.setStringValue("users", "XWiki.Admin");
        obj.setStringValue("groups", "");
        obj.setStringValue("levels", "admin,programming");
        obj.setIntValue("allow", 1);
        prefdoc.addObject(XWIKI_GLOBAL_RIGHTS_CLASSNAME, obj);
        this.xwiki.saveDocument(prefdoc, context);

        XWikiDocument doc = new XWikiDocument("Main", "WebHome");
        doc.setAuthor(XWIKI_ADMIN_DOCNAME);
        doc.setContentAuthor(XWIKI_ADMIN_DOCNAME);
        context.setDoc(doc);
    }
}
