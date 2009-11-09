
package com.xpn.xwiki.wiked.repository;

import org.eclipse.ui.IMemento;

/**
 * Bookmark save paricipant may save custom bookmark data. 
 * You typically implement the interface on new repository interfaces,
 * where additional attributes need to be saved (e.g. remote URL, login
 * name and password etc).
 *   
 * @author Psenicka_Ja
 */
public interface IBookmarkSaveParticipant {

    /**
     * Store the bookmark into given repository. 
     * @param bookmarkData
     */
	void storeBookmark(IMemento bookmarkData);
}