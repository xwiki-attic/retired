package org.xwiki.model;

import org.xwiki.model.dao.UserValue;

/**
 * @author MikhailKotelnikov
 */
public class User {

    private Farm fFarm;

    private UserValue fUserValue;
    
    /**
     * @param farm
     * @param userValue
     */
    public User(Farm farm, UserValue userValue) {
        fFarm = farm;
        fUserValue = userValue;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof User))
            return false;
        User user = (User) obj;
        return fUserValue.equals(user.fUserValue);
    }
    
    @Override
    public int hashCode() {
        return fUserValue.hashCode() ;
    }

    @Override
    public String toString() {
        return fUserValue.toString();
    }

}
