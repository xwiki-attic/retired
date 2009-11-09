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
 *
 */
package org.xwiki.eclipse.ui;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.xwiki.eclipse.ui.editors.scanners.EditorScanners;
import org.xwiki.eclipse.ui.utils.XwkiAPIResourceReader;

/**
 * The activator class controls the plug-in life cycle
 */
public class UIPlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.xwiki.eclipse.ui";

	// The shared instance
	private static UIPlugin plugin;

	private static List<Template> contextApiList = null;
	
	private static List<Template> docApiList = null;
	
	private static List<Template> requestApiList = null;
	
	private static List<Template> xwikiApiList = null;

	/**
	 * The constructor
	 */
	public UIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static UIPlugin getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public List<Template> getXwikiTemplates() {
		if (xwikiApiList == null) {
			xwikiApiList = XwkiAPIResourceReader.getXwikiTeplates("xwiki");
		}
		return xwikiApiList;
	}
	
	public List<Template> getContextTemplates() {
		if (contextApiList == null) {
			contextApiList = XwkiAPIResourceReader.getXwikiTeplates("context");
		}
		return contextApiList;
	}
	
	public List<Template> getDocTemplates() {
		if (docApiList == null) {
			docApiList = XwkiAPIResourceReader.getXwikiTeplates("doc");
		}
		return docApiList;
	}
	
	public List<Template> getRequestTemplates() {
		if (requestApiList == null) {
			requestApiList = XwkiAPIResourceReader.getXwikiTeplates("request");
		}
		return requestApiList;
	}




}
