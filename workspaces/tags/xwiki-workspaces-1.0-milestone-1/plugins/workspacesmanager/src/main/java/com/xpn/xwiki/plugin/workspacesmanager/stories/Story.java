/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.xpn.xwiki.plugin.workspacesmanager.stories;

import java.util.Date;

/**
 * A story interface that represent an event that occured on a space. Is intended to be used for
 * notifications and spaces activity streams.
 * 
 * @version $Id: $
 */
public interface Story
{

    String getTitle();

    String getURL();

    String getApplicationName();

    String getApplicationURL();

    String getLastAuthor();

    Date getDate();

    String getVersionComment();

    boolean hasVersionComment();

}
