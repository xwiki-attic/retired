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
 * Default implementation of the Story interface
 * 
 * @version $Id$
 */
public class StoryImpl implements Story
{

    private String appName;

    private String appURL;

    private Date date;

    private String author;

    private String title;

    private String url;

    private String comment;

    private boolean hasComment;

    public StoryImpl(String title, String url, String appName, String appURL, Date date,
        String author)
    {
        this.title = title;
        this.url = url;
        this.appName = appName;
        this.appURL = appURL;
        this.date = date;
        this.author = author;
        this.hasComment = false;
        this.comment = new String();
    }

    public StoryImpl(String title, String url, String appName, String appURL, Date date,
        String author, String comment)
    {
        this(title, url, appName, appURL, date, author);
        this.hasComment = true;
        this.comment = comment;
    }

    public String getApplicationName()
    {
        return appName;
    }

    public String getApplicationURL()
    {
        return appURL;
    }

    public Date getDate()
    {
        return date;
    }

    public String getLastAuthor()
    {
        return author;
    }

    public String getTitle()
    {
        return title;
    }

    public String getURL()
    {
        return url;
    }

    public String getVersionComment()
    {
        return comment;
    }

    public boolean hasVersionComment()
    {
        return hasComment;
    }

    public boolean equals(StoryImpl st)
    {
        return this.hashCode() == st.hashCode() ? true : false;
    }

    public int hashCode()
    {
        return (getDate().toString() + ":" + getURL()).hashCode();
    }

    public String toString()
    {
        return getDate().toString() + ":" + getURL();
    }

}
