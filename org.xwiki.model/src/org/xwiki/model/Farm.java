package org.xwiki.model;

import org.xwiki.model.dao.IWikiDao;
import org.xwiki.model.dao.UserValue;

/**
 * @author MikhailKotelnikov
 */
public class Farm {

    private IWikiDao fWikiDao;

    /**
     * @param session
     */
    public Farm(IWikiDao dao) {
        super();
        fWikiDao = dao;
    }

    /**
     * This method checks that the given passwords are the same and silently
     * retuns if they are corresponds to each other. Otherwise it rises an
     * exception.
     * 
     * @param first the expected hash of the password
     * @param second the password to check
     * @throws XWikiException
     */
    private void checkPassword(String first, String second)
        throws XWikiException {
        boolean ok = false;
        if (first == null || second == null)
            ok = first == second;
        ok = first.equals(second);
        if (!ok)
            throw new XWikiException("Access denied");
    }

    public User createUser(String login, String password) throws XWikiException {
        UserValue userValue = fWikiDao.loadUserInfo(login);
        if (userValue != null)
            throw new XWikiException(
                "User already exists with such a login. Login: '"
                    + login
                    + "'.");
        userValue = new UserValue(login, password);
        fWikiDao.storeUserInfo(userValue);
        return new User(this, userValue);
    }

    public User getUser(String login) {
        UserValue userValue = fWikiDao.loadUserInfo(login);
        return userValue != null ? new User(this, userValue) : null;
    }

    /**
     * Returns the wikiDao.
     * 
     * @return the wikiDao.
     */
    public IWikiDao getWikiDao() {
        return fWikiDao;
    }

    /**
     * @param login the user login
     * @param password the password to check
     * @return a new user's session
     * @throws XWikiException
     */
    public Session newSession(String login, String password)
        throws XWikiException {
        UserValue userValue = fWikiDao.loadUserInfo(login);
        checkPassword(userValue.password, password);
        User user = new User(this, userValue);
        return new Session(this, user);
    }

}
