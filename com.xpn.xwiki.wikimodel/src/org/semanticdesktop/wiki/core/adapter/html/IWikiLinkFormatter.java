/*
 * $Header$
 * $Revision$
 * $Date$
 */
/*******************************************************************************
 * Copyright (c) 2005 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.semanticdesktop.wiki.core.adapter.html;

import java.io.PrintWriter;


/**
 * @author kotelnikov
 */
public interface IWikiLinkFormatter {

	/**
	 * Serializes the given reference into the given stream.
	 * 
	 * @param label
	 *            the label of the link
	 * @param uri
	 *            the reference to serialize
	 * @param writer
	 *            in this stream the reference will be serialized
	 */
	void formatLink(String label, String uri, PrintWriter writer);

}
