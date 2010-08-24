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
 *
 * Author: Miguel Bento
 * Date: 02/08/2008
 * This class handles all the SVG page saving
 * 
 *  For more information please contact devs@xwiki.org
 */

package com.xpn.xwiki.cbdf.client;

import com.google.gwt.user.client.Window;
import com.objetdirect.tatami.client.gfx.*;

public class SVGFormatter {
    private String nameSpace;
    private String docName;
    private String svgVersion;
    private int canvasWidth;
    private int canvasHeight;

    /**
     * This is the SVGFormatter class constructor. It creates a SVGFormatter object given the right parameters.
     *
     * @param version   This parameter represents the SVG version that is being used to save the drawing.
     * @param ns        This parameter represents the namespace that will be used to save the drawing.
     * @param name      This parameter contains the drawing name.
     * @param width     This parameter contains the drawing width.
     * @param height    This parameter contains the drawing height.
     */
    SVGFormatter(String version, String ns, String name, int width, int height) {
        this.svgVersion = version;
        this.nameSpace = ns;
        this.docName = name;
        this.canvasWidth = width;
        this.canvasHeight = height;
    }

    /**
     * This method constructs the xml needed in order to save a GraphicObject's style.
     *
     * @param obj   The GraphicObject of which we are going to construct the needed xml style.
     * @return      A String containing the xml code needed to represent the GraphicObject's style.
     */
    private String concatSVGObjectStyle(GraphicObject obj) {
        String xml = "";
        xml = xml.concat("\nstyle=\"fill:rgb("+obj.getFillColor().getRed()+","+obj.getFillColor().getGreen()+","+obj.getFillColor().getBlue()+");");
        xml = xml.concat("fill-opacity:"+((double)obj.getFillColor().getAlpha()/255.0)+";\n");
        xml = xml.concat("stroke:rgb("+obj.getStrokeColor().getRed()+","+obj.getStrokeColor().getGreen()+","+obj.getStrokeColor().getBlue()+");");
        xml = xml.concat("stroke-opacity:"+(obj.getStrokeColor().getAlpha()/255.0)+";");
        xml = xml.concat("stroke-width:"+obj.getStrokeWidth()+";");
        if(obj.getType() == Action.TEXT) {
            xml = xml.concat("\nfont-family:"+((Text)obj).getFont().getFamily()+";");
            xml = xml.concat("font-size:"+((Text)obj).getFont().getSize()+";");
            xml = xml.concat("font-style:"+((Text)obj).getFont().getStyle()+";");
            xml = xml.concat("font-weight:"+((Text)obj).getFont().getWeight()+";");
            xml = xml.concat("\">\n");
        } else {
            xml = xml.concat("\"/>\n\n");    
        }

        return xml;
    }

    /**
     * This method constructs a Path object. It's needed to avoid code duplication since there is more than one drawing object that uses the Path object.
     * @param obj   The graphic object of which we are going to construct the needed xml code
     * @return      A String containg the xml code needed to represent a Path svg object.
     */
    private String concatSVGPath(GraphicObject obj) {
        String xml = "";
        xml = xml.concat("<path d=\""+((Path)obj).getPath()+"\"");
        xml = xml.concat(concatSVGObjectStyle(obj));
        return xml;
    }

    /**
     * This method constructs the xml header needed to save a SVG drawing.
     *
     * @return      A String containing the needed xml code.
     */
    private String concatSVGHeader() {
        String xml = "";
        xml = xml.concat("{svg:"+docName+"|height="+canvasHeight+"|width="+canvasWidth+"}\n");
        xml = xml.concat("<svg width=\""+canvasWidth+"\" height=\""+canvasHeight+"\" version=\""+svgVersion+"\" xmlns=\""+nameSpace+"\">\n\n");
        return xml;
    }

    /**
     * This method iterates all the GraphicObjects embeeded in the GraphicCanvas and constructs the xml needed in order to save them.
     *
     * @param canvas     The GraphicCanvas that is going to be iterated.
     */
    public void saveDocument(GraphicCanvas canvas) {
        String xmlSintax= "";
        GraphicObject obj;
        int size = canvas.countGraphicObject();

        xmlSintax = xmlSintax.concat(concatSVGHeader());


        for(int i=0; i<size; i++) {
            obj = canvas.getGraphicObject(i);
            switch(obj.getType()) {
                case Action.PENCIL:
                    xmlSintax = xmlSintax.concat(concatSVGPath(obj));
                    break;
                case Action.LINE:
                    xmlSintax = xmlSintax.concat("<line x1=\""+ obj.getX()+"\" y1=\""+obj.getY()+"\" x2=\""+(obj.getX()+((Line)obj).getXB())+"\" y2=\""+(obj.getY()+((Line)obj).getYB())+"\"");
                    xmlSintax = xmlSintax.concat(concatSVGObjectStyle(obj));
                    break;
                case Action.RECTANGLE:
                    xmlSintax = xmlSintax.concat("<rect x=\""+obj.getX()+"\" y=\""+obj.getY()+"\" width=\""+((Rect)obj).getWidth()+"\" height=\""+((Rect)obj).getHeight()+"\"");
                    xmlSintax = xmlSintax.concat(concatSVGObjectStyle(obj));
                    break;
                case Action.CIRCLE:
                    xmlSintax = xmlSintax.concat("<circle cx=\""+obj.getX()+"\" cy=\""+obj.getY()+"\" r=\""+((Circle)obj).getRadius()+"\"");
                    xmlSintax = xmlSintax.concat(concatSVGObjectStyle(obj));
                    break;
                case Action.ELLIPSE:
                    xmlSintax = xmlSintax.concat("<ellipse cx=\""+obj.getX()+"\" cy=\""+obj.getY()+"\" rx=\""+((Ellipse)obj).getRadiusX()+"\" ry=\""+((Ellipse)obj).getRadiusY()+"\"");
                    xmlSintax = xmlSintax.concat(concatSVGObjectStyle(obj));
                    break;
                case Action.POLYLINE:
                    xmlSintax = xmlSintax.concat(concatSVGPath(obj));
                    break;
                case Action.TEXT:
                    xmlSintax = xmlSintax.concat("<text x=\""+obj.getX()+"\" y=\""+obj.getY()+"\" ");
                    xmlSintax = xmlSintax.concat(concatSVGObjectStyle(obj));
                    xmlSintax = xmlSintax.concat(((Text)obj).getText());
                    xmlSintax = xmlSintax.concat("</text>\n\n");
            }
        }
        xmlSintax = xmlSintax.concat("</svg>");
        xmlSintax = xmlSintax.concat("\n\n{svg}");
        Window.alert("SVG content:\n"+xmlSintax);

    }
}
