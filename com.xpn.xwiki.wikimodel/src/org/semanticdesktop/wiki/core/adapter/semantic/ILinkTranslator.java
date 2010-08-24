/*******************************************************************************
 * Copyright (c) 2005,2006 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.semanticdesktop.wiki.core.adapter.semantic;

/**
 * Instances of this type are used to transform references from wiki pages to
 * corresponding uris.
 * 
 * @author kotelnikov
 */
public interface ILinkTranslator {

    /**
     * @param uri an uri to transform to a link
     * @return a wiki reference corresponding to the given uri
     */
    String getLinkFromUri(String uri);

    /**
     * @param link the link to transform to an uri
     * @return an uri corresponding to the given link from a wiki page
     */
    String getUriFromLink(String link);

}
