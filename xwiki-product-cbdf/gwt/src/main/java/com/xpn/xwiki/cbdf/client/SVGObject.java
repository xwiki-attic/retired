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
 *
 *	Author: Miguel Bento
 *	Date: 02/08/2008
 *
 *  This class represents a SVG object created in the DOM. Needed in order to overlap a Tatami limitation.
 *
 *  For more information please contact devs@xwiki.org
 */

package com.xpn.xwiki.cbdf.client;

public class SVGObject {
    private int objNumber;
    private int objType;

    /**
     * Possible object types
     */
    public static final int PENCIL = 1;
    public static final int LINE = 2;
    public static final int RECTANGLE = 3;
    public static final int CIRCLE = 4;
    public static final int ELLIPSE = 5;
    public static final int POLYLINE = 6;

    /**
     * Class constructor
     * @param number   The Graphic Object's number on the current Graphic Canvas. This permits a unique identification of each Graphic Object.
     * @param type     The Graphic Object's type
     */
    SVGObject(int number, int type) {
        this.objNumber = number;
        this.objType = type;
    }

    /**
     * obj_type variable getter
     * @return    the variable obj_type of the object
     */
    public int getType() {
        return this.objType;
    }

    /**
     * obj variable getter
     * @return    the variable obj of the object
     */
    public int getNumber() {
        return this.objNumber;
    }
}
