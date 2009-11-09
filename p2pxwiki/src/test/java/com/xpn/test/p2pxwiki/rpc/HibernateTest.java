package com.xpn.test.p2pxwiki.rpc;

import org.hibernate.HibernateException;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.store.XWikiHibernateStore;
import com.xpn.xwiki.test.HibernateTestCase;

public class HibernateTest extends HibernateTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		cleanUpDNS(getXWiki().getHibernateStore(), false, false,
				getXWikiContext());

	}

	public static void cleanUpDNS(XWikiHibernateStore hibstore,
			boolean bFullCleanup, boolean bSchemaUpdate, XWikiContext context)
			throws HibernateException, XWikiException {
		hibstore.checkHibernate(context);
		hibstore.beginTransaction(context);
		String database = context.getDatabase();
		if (database == null)
			context.setDatabase("xwikitest");
		if (bFullCleanup) {

		} else {
			runSQL(hibstore, "delete from records", context);
		}
		hibstore.endTransaction(context, true);
	}

	protected void tearDown() {
		super.tearDown();
	}
}
