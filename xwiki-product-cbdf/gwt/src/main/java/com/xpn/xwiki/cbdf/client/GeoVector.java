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
 * This class is used to repsent a vector and handle simple vector operations like vector rotation, vector normalization, calculating the angle between vectors and point addition.
 *
 *	Author: Miguel Bento
 *	Date: 02/08/2008
 *
 *  For more information please contact devs@xwiki.org
 */

package com.xpn.xwiki.cbdf.client;


public class GeoVector {
    private double vectorX, vectorY;

    /**
     * This is the class constructor.
     *
     * @param xDest     The X coordinate of the edge of the vector you want to create
     * @param yDest     The Y coordinate of the edge of the vector you want to create
     * @param xOrigin   The X coordinate of the origin of the vector you want to create
     * @param yOrigin   The Y coordinate of the origin of the vector you want to create
     */
    GeoVector(double xDest, double yDest, double xOrigin, double yOrigin) {
        this.vectorX = xDest - xOrigin;
        this.vectorY = yDest - yOrigin;
    }

    /**
     * This method performs a rotation on the current vector given a radians angle
     *
     * @param angle   The angle, in radians, to which the object should be rotated.
     */
    public void rotate(double angle) {
        double newX, newY;

        newX = (Math.cos(angle)*this.vectorX) - (Math.sin(angle)*this.vectorY);
        newY = (Math.sin(angle)*this.vectorX) + (Math.cos(angle)*this.vectorY);
        this.vectorX = newX;
        this.vectorY = newY;
    }

    /**
     * This method normalizes the current vector.
     *
     */
    public void normalize() {
        double lenght;

        lenght = Math.sqrt((this.vectorX*this.vectorX) + (this.vectorY*this.vectorY));
        this.vectorX /= lenght;
        this.vectorY /= lenght;
    }

    /**
     * This method sums a point to the current vector
     *
     * @param x   The point X coordinate
     * @param y   The point Y coordinate
     */
    public void addPoint(double x, double y) {
        this.vectorX += x;
        this.vectorY += y;
    }

    /**
     * vectorX field getter.
     *
     * @return   The vectorX variable
     */
    public double getX() {
        return this.vectorX;
    }

    /**
     * vectorY field getter.
     *
     * @return   The vectorY variable
     */
    public double getY() {
        return this.vectorY;
    }

    /**
     * This method calculates the angle between the current vector and another one. To do this it uses the atan2 function which gives you a result in the [-PI, PI] range.
     *
     * @param vx2    The X component of the other vector
     * @param vy2    The Y component of the other vector
     * @return       The angle between the two vectors in radians.
     */
    public double getAngleWith( double vx2, double vy2) {
        return (js_atan2((this.vectorX*vy2)-(vx2*this.vectorY), (this.vectorX*vx2)+(this.vectorY*vy2))) ;
    }

    /**
     * This is a support method for the getAngleWith method needed because the GWT Math library doesn't supports the atan2() function. It does a native javascript call of the function atan2 and returns its result.
     *
     * @param d1   The y parameter for the atan2 function
     * @param d2   The x parameter for the atan2 function
     * @return     The result of the atan2 calculation
     */
    private static native double js_atan2(double d1, double d2)/*-{
         return Math.atan2(d1, d2);
    }-*/;
}
