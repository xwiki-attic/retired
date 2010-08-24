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
 * Author: Miguel Bento
 * Date: 02/08/2008
 *
 * For more information please contact devs@xwiki.org
 *
 * This class saves and represents the action the user is currently doing or has currently selected.
 *
 * Possible actions:
 *  0 - Select objects
 *  1 - Draw with pencil
 *  2 - Draw a line
 *  3 - Draw a rectangle
 *  4 - Draw a circle
 *  5 - Draw a ellipse
 *  6 - Draw a Polyline
 */
package com.xpn.xwiki.cbdf.client;


public class Action {

    private int action=0;
    public static final int SELECT = 0;
    public static final int PENCIL = 1;
    public static final int LINE = 2;
    public static final int RECTANGLE = 3;
    public static final int CIRCLE = 4;
    public static final int ELLIPSE = 5;
    public static final int POLYLINE = 6;
    public static final int TEXT = 7;
    public static final int TEXTBOX = 8;
    public static final int ROTATE = 0;
    public static final int NORTHWEST = 1;
    public static final int NORTH = 2;
    public static final int NORTHEAST = 3;
    public static final int WEST = 4;
    public static final int EAST = 5;
    public static final int SOUTHWEST = 6;
    public static final int SOUTH = 7;
    public static final int SOUTHEAST = 8;


    /**
     * Constructor of the class Action
     * @param act   Represents the initial action to be stored in the current Action object
     */
    public Action(int act) {
        this.action = act;
    }

    /**
     * This method returns the current action of the Action object
     * @return   The integer value of the action stored.
     *  Possible actions:
     *  0 - Select objects
     *  1 - Draw with pencil
     *  2 - Draw a line
     *  3 - Draw a rectangle
     *  4 - Draw a circle
     *  5 - Draw a ellipse
     *  6 - Draw a Polyline
    */
    public int getAction() {
        return this.action;
    }

    /**
     * action variable setter for the Object Action
     * @param act  The integer to which the variable is assigned
     * Possible actions:
     *  0 - Select objects
     *  1 - Draw with pencil
     *  2 - Draw a line
     *  3 - Draw a rectangle
     *  4 - Draw a circle
     *  5 - Draw a ellipse
     *  6 - Draw a Polyline
     */
    public void setAction(int act) {
        this.action = act;
    }

    /**
     * This method finds out if the current action is the SELECT action
     * @return   If the action is SELECT, true, otherwise false
     */
    public boolean isSelect() {
        return (this.getAction() == Action.SELECT);
    }

    /**
     * This method finds out if the current action is the PENCIL action
     * @return   If the action is PENCIL, true, otherwise false
     */
    public boolean isPencil() {
        return this.getAction() == Action.PENCIL;
    }

    /**
     * This method finds out if the current action is the LINE action
     * @return   If the action is LINE, true, otherwise false
     */
    public boolean isLine() {
        return this.getAction() == Action.LINE;
    }

    /**
     * This method finds out if the current action is the RECTANGLE action
     * @return   If the action is RECTANGLE, true, otherwise false
     */
    public boolean isRectangle() {
        return this.getAction() == Action.RECTANGLE;
    }

    /**
     * This method finds out if the current action is the CIRCLE action
     * @return   If the action is CIRCLE, true, otherwise false
     */
    public boolean isCircle() {
        return this.getAction() == Action.CIRCLE;
    }

    /**
     * This method finds out if the current action is the ELLIPSE action
     * @return   If the action is ELLIPSE, true, otherwise false
     */
    public boolean isEllipse() {
        return this.getAction() == Action.ELLIPSE;
    }

    /**
     * This method finds out if the current action is the POLYLINE action
     * @return   If the action is POLYLINE, true, otherwise false
     */
    public boolean isPolyline() {
        return this.getAction() == Action.POLYLINE;
    }

    /**
     * This method finds out if the current action is the TEXTBOX action
     * @return   If the action is TEXTBOX, true, otherwise false
     */
    public boolean isTextBox() {
        return this.getAction() == Action.TEXTBOX;
    }

    /**
     * This method finds out if the current action is the TEXT action
     * @return   If the action is TEXT, true, otherwise false
     */
    public boolean isText() {
        return this.getAction() == Action.TEXT;
    }
}
