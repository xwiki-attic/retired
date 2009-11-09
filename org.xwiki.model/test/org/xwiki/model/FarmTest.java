package org.xwiki.model;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.xwiki.model.dao.IWikiDao;
import org.xwiki.model.dao.memory.MemoryWikiDao;

/**
 * @author MikhailKotelnikov
 */
public class FarmTest extends TestCase {

    /**
     * @param name
     */
    public FarmTest(String name) {
        super(name);
    }

    /**
     * @throws XWikiException
     */
    public void test() throws XWikiException {
        IWikiDao dao = new MemoryWikiDao();
        Farm farm = new Farm(dao);
        String login = "test";
        String password = "";
        // User-related operations
        User user = farm.getUser(login);
        assertNull(user);

        user = farm.createUser(login, password);
        assertNotNull(user);

        Session session = farm.newSession(login, password);
        assertNotNull(session);
        assertEquals(user, session.getUser());

        Set<Wiki> wikiList = session.getAvailableWikis();
        assertNotNull(wikiList);
        assertEquals(0, wikiList.size());

        Wiki wiki = session.getWiki("test");
        assertNull(wiki);

        wiki = session.newWiki("test");
        assertNotNull(wiki);
        assertEquals("test", wiki.getName());

        // Space-related operations...
        Set<Space> spaces = wiki.getAllSpaces();
        assertNotNull(spaces);
        assertEquals(0, spaces.size());

        Space space = wiki.getSpace("main");
        assertNull(space);

        Space newSpace = wiki.newSpace("main");
        assertNotNull(newSpace);
        assertEquals("main", newSpace.getName());

        spaces = wiki.getAllSpaces();
        assertNotNull(spaces);
        assertEquals(1, spaces.size());
        assertEquals(newSpace, spaces.iterator().next());

        space = wiki.getSpace("main");
        assertEquals(newSpace, space);

        // Document-related operations
        Document doc = wiki.getDocument("main", "index");
        assertNull(doc);
        doc = space.getDocument("index");
        assertNull(doc);

        doc = space.newDocument("index");
        assertNotNull(doc);
        assertEquals("index", doc.getName());

        Document testDoc = space.getDocument("index");
        assertNotNull(testDoc);
        assertEquals(doc, testDoc);

        testDoc = wiki.getDocument("main", "index");
        assertNotNull(testDoc);
        assertEquals(doc, testDoc);

        //
        // doc = wiki.newDocument();
        // assertEquals(space, doc.getSpace());
        // Iterator<Document> iterator = space.getAllDocuments();
        // assertNotNull(iterator);

    }

}
