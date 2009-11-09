package org.xwiki.model.dao;

/**
 * @author MikhailKotelnikov
 */
public class UserValue {

    public String login;

    public String password;

    /**
     * @param password
     * @param login
     */
    public UserValue(String login, String password) {
        this.login = login;
        this.password = password;
    }

}
