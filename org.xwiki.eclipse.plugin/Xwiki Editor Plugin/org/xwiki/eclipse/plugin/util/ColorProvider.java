package org.xwiki.eclipse.plugin.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author venkatesh
 *
 */

public class ColorProvider {

	//public static final RGB SINGLE_LINE_COMMENT= new RGB(128, 128, 0);
	public static final RGB STRING= new RGB(0, 128, 0);
	public static final RGB DEFAULT= new RGB(0, 0, 0);
	public static final RGB DEFAULT_BACKGROUND = new RGB(255,255,255);
	public static final RGB DEFAULT_BLUE_BACKGROUND = new RGB(232,242,254);
	public static final RGB XWIKI_LIST= new RGB(255,185,15);
	public static final RGB XWIKI_INFO= new RGB(141,182,205);
	public static final RGB XWIKI_WARNING= new RGB(238,220,130);
	public static final RGB XWIKI_ERROR= new RGB(255,0,0);
	public static final RGB XWIKI_PRECODE= new RGB(107,107,107);
	public static final RGB XWIKI_HR= new RGB(139,69,19);
	public static final RGB XWIKI_LINK= new RGB(0, 0, 255);
	public static final RGB XWIKI_LINK_BACK= new RGB(255, 255, 224);
	public static final RGB XWIKI_IMAGE= new RGB(139, 69, 19);

	protected Map fColorTable= new HashMap(10);

	public void dispose() {
		Iterator e= fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}

	public Color getColor(RGB rgb) {
		Color color= (Color) fColorTable.get(rgb);
		if (color == null) {
			color= new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}