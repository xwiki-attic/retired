
package com.xpn.xwiki.wiked.internal.ui.editor;

import org.eclipse.jface.text.BadLocationException;

/**
 * 
 * @author psenicka_ja
 */
public interface INavigableEditor {

    void navigateTo(int lineNumber) throws BadLocationException;
}
