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
 *  For more information please contact devs@xwiki.org
 */

package com.xpn.xwiki.cbdf.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.objetdirect.tatami.client.ColorChooser;
import com.objetdirect.tatami.client.Slider;
import com.objetdirect.tatami.client.gfx.*;
import com.objetdirect.tatami.client.gfx.TextBox;
import java.util.ArrayList;


/**
 * For the application porpuses, this is the mains class, the class that handles all the user interaction with the UI and therefore all the objects interaction.
 *
 * To develop this, we are using mainly the 1.1.1 version of Tatami's library. Mainly because we had to make several alterations to it, since it doesn't supported a few things we wanted.
 * First, since we are using the gwt lib version 1.4.61 i had to make a getType() method that returned the type of the object(Action.RECTANGLE, Action.CIRCLE and so on).
 * To support object rotation and scalling while rotated 
 */
public class CBDF extends Composite implements EntryPoint, GraphicObjectListener, ClickListener, ChangeListener, KeyboardListener  {

	/** the last position of the last widget which had been moved/created */
	private int[] lastPosition = { 0, 0 };

	/** the source position of the last graphic object which had been moved */
	private int[] objPosition = { 0, 0 };

    /** The buttons panel */
	private VerticalPanel buttonPanel;

	/** The main panel that allow the widgets to be placed one over another. It's needed to support the text Objects, because in that case we need to create a TextArea over the selected area for the text. */
    private AbsolutePanel apanel;

    /** The graphic canvas which is used to draw and display the objects */
	private GraphicCanvas canvas;

	/** Variable that stores the current object being drawn */
	private GraphicObject curr_obj = null;

    /** Variable used to store the intermediate object when needed */
	private GraphicObject aux_obj = null;

    /** Array of GraphicObjects used to access the Transform Points in a unique and fast way */
    private GraphicObject[] transformPointers = null;

	/** Graphic canvas width */
	private int width = 600;

	/** Graphic canvas height */
	private int height = 600;

    /** Current User action */
	private Action action = new Action(Action.LINE);

	/** Button to create a Circle */
	private Image selectButton;

	/** Button to create a Circle */
	private Image circleButton;

	/** Button to create a Rectangle */
	private Image rectButton;

	/** Button to create a Line */
	private Image lineButton;

	/** Button to create an Ellipse */
	private Image ellipseButton;

	/** Button to create an Polyline */
	private Image polylineButton;

	/** Button to create an pencil drawing */
	private Image pencilButton;

    /** Button to write text*/
    private Image textButton;

	/** Button to delete an object */
	private Image deleteButton;

	/** Button to send an object back */
	private Image backButton;

	/** Button to send an object to the front */
	private Image frontButton;

    /** Button to save an object */
	private Image saveButton;

    private Image strokeButton;

    /** Slider that let's the user choose the object's fill opacity */
	private Slider fillOpacity;

    /** int that stores the value of the fill opacity when a user click on a object so that it can be changed back to original when finisehd. */
    private int lastFillOpacity;

    /** Slider that let's the user choose the object's stroke opacity */
    private Slider strokeOpacity;

    /** int that stores the value of the stroke opacity when a user click on a object so that it can be changed back to original when finisehd. */
    private int lastStrokeOpacity;

    /** TextArea used to edit or create text objects */
    private TextArea editor = new TextArea();

	/** Variable that stores the current fill color */
	private Color currentFillColor = Color.BLACK;

    /** variable that stores the value of the fill color when a user click on a object so that it can be changed back to original when finisehd. */
    private Color lastFillColor;

    /** Variable that stores the current stroke color */
	private Color currentStrokeColor = Color.BLACK;

    /** variable that stores the value of the stroke color when a user click on a object so that it can be changed back to original when finisehd. */
    private Color lastStrokeColor;

    /** Slider that store the current Stroke size used to draw objects */
    private Slider currentStrokeSize;

    /** variable that stores the value of the fill color when a user click on a object so that it can be changed back to original when finisehd. */
    private int lastStrokeSize;

    /** The backedup variable serves as a semaphore in the process of backing up the current style */
    private boolean backedup = false;

    /** This arraylist is needed to save a Path's path during vector creation */
    private ArrayList currentPath;

    /** This variable stores the current Font that is being used to create text objects */
    private Font currFont;

    /** List box used to choose and store the current font family */
    private ListBox currentFontFamily;

    /** List box used to choose and store the current font size */
    private ListBox currentFontSize;

    /** List box used to choose and store the current font style */
    private ListBox currentFontStyle;

    /** List box used to choose and store the current font weight */
    private ListBox currentFontWeight;

    /** Html used to display the currentFillColor in the GUI */
    private HTML fill;

    /** Html used to display the currentStrokeColor in the GUI */
    private HTML stroke;

    /** Variable that indicates if the object can be translated at a given moment */
    private boolean isMovable;

    /** Variable to indicate if the selected object can be transformed at a given moment */
    private boolean isTransformable;

    /** This virtual group is used to contain all the transform points when a object is selected */
    private VirtualGroup transformPoints;

    /** Instance of the SVGFormatter class used to save the drawing content to svg */
    private SVGFormatter saver;

    /** Variable to store the selected object's rotatio angle. Needed to overlap a Tatami's limitation in order to implement object rotation and object transformation when the object is rotated. */
    private double currRotation;


    /**
     * This is the entry point method.
    */
	public void onModuleLoad() {
		InitComponents();
        RootPanel.get("cbdt_app").add(apanel);

        apanel.add(stroke, buttonPanel.getAbsoluteLeft() + 22, buttonPanel.getAbsoluteTop() + 147 + 15);
        apanel.add(fill, buttonPanel.getAbsoluteLeft() + 7, buttonPanel.getAbsoluteTop() + 147);

    }

    /**
	 * Method that initializes all the variables and creates the html required for the color picker and so on.
	 */
	private void InitComponents() {
        apanel = new AbsolutePanel();
        FocusPanel focus = new FocusPanel();
        DockPanel panel = new DockPanel();

        lastPosition[0] = 0;
		lastPosition[1] = 0;

		isMovable = false;
        isTransformable = false;

        transformPoints = new VirtualGroup();
		transformPointers = new GraphicObject[9];

        currRotation = 0.0;

        this.currentFontFamily = new ListBox();
        this.currentFontFamily.setMultipleSelect(false);
        this.currentFontFamily.insertItem("Arial", 0);
        this.currentFontFamily.insertItem("Courier", 1);
        this.currentFontFamily.insertItem("Times New Roman", 2);
        this.currentFontFamily.insertItem("Verdana", 3);
        this.currentFontFamily.insertItem("Georgia", 4);
        this.currentFontFamily.setSelectedIndex(0);
        this.currentFontFamily.addChangeListener(this);

        this.currentFontSize = new ListBox();
        this.currentFontSize.setMultipleSelect(false);
        this.currentFontSize.insertItem("8", 0);
        this.currentFontSize.insertItem("10", 1);
        this.currentFontSize.insertItem("12", 2);
        this.currentFontSize.insertItem("14", 3);
        this.currentFontSize.insertItem("16", 4);
        this.currentFontSize.insertItem("18", 5);
        this.currentFontSize.insertItem("20", 6);
        this.currentFontSize.insertItem("24", 7);
        this.currentFontSize.insertItem("28", 8);
        this.currentFontSize.insertItem("36", 9);
        this.currentFontSize.insertItem("48", 10);
        this.currentFontSize.insertItem("72", 11);
        this.currentFontSize.setSelectedIndex(2);
        this.currentFontSize.addChangeListener(this);

        this.currentFontStyle = new ListBox();
        this.currentFontStyle.setMultipleSelect(false);
        this.currentFontStyle.insertItem("normal", 0);
        this.currentFontStyle.insertItem("italic", 1);
        this.currentFontStyle.setSelectedIndex(0);
        this.currentFontStyle.addChangeListener(this);

        this.currentFontWeight = new ListBox();
        this.currentFontWeight.setMultipleSelect(false);
        this.currentFontWeight.insertItem("normal",0);
        this.currentFontWeight.insertItem("bold", 1);
        this.currentFontWeight.setSelectedIndex(0);
        this.currentFontWeight.addChangeListener(this);

        this.updateFont();

        canvas = new GraphicCanvas();
		canvas.setStyleName("drawboard");
		canvas.setPixelSize(width, height);
		canvas.addGraphicObjectListener(this);

        saver = new SVGFormatter("1.1", "http://www.w3.org/2000/svg", "demo", width, height);

        buttonPanel = new VerticalPanel();
		buttonPanel.setSpacing(0);

        Grid gridShape = new Grid(4, 2);
		gridShape.setCellSpacing(2);
		gridShape.setCellPadding(2);

        Grid gridTransform = new Grid(3, 2);
		gridTransform.setCellPadding(2);
		gridTransform.setCellSpacing(2);

        fill = new HTML(" ");
		DOM.setStyleAttribute(fill.getElement(), "backgroundColor", this.currentFillColor.toHex());
		DOM.setStyleAttribute(fill.getElement(), "border", "solid");
		DOM.setStyleAttribute(fill.getElement(), "borderWidth", "thin");
		DOM.setStyleAttribute(fill.getElement(), "borderColor", "#000000");
		fill.setSize("30px", "30px");
		fill.addClickListener(this);

        stroke = new HTML(" ");
        DOM.setStyleAttribute(stroke.getElement(), "backgroundColor", this.currentStrokeColor.toHex());
        DOM.setStyleAttribute(stroke.getElement(), "border", "solid");
        DOM.setStyleAttribute(stroke.getElement(), "borderWidth", "thin");
        DOM.setStyleAttribute(stroke.getElement(), "borderColor", "#000000");
        stroke.setSize("30px","30px");
        stroke.addClickListener(this);

        HTML fake = new HTML("&nbsp;&nbsp;&nbsp;");
        DOM.setStyleAttribute(fake.getElement(), "backgroundColor", "#FFFFFF");
        fake.setSize("30px", "30px");

        HTML fake2 = new HTML("&nbsp;&nbsp;&nbsp;");
        DOM.setStyleAttribute(fake2.getElement(), "backgroundColor", "#FFFFFF");
        fake2.setSize("30px", "30px");

        buttonPanel.add(gridShape);
        buttonPanel.add(fake);
        buttonPanel.add(fake2);

        this.strokeButton = new Image("gfx/color.gif");
        this.strokeButton.setTitle("Choose the stroke");
        this.strokeButton.setSize("34px", "34px");
        this.strokeButton.addClickListener(this);

        buttonPanel.add(this.strokeButton);
        buttonPanel.setCellHorizontalAlignment(this.strokeButton, VerticalPanel.ALIGN_CENTER);

		buttonPanel.add(gridTransform);

        fillOpacity = new Slider(Slider.HORIZONTAL, 0, 255, 255, true);
		fillOpacity.addChangeListener(this);

        strokeOpacity = new Slider(Slider.HORIZONTAL, 0, 255, 255, true);
		strokeOpacity.addChangeListener(this);

        currentStrokeSize = new Slider(Slider.HORIZONTAL, 0, 50, 1, true);
        currentStrokeSize.addChangeListener(this);

        /** This adds the buttons to the two grids */
        selectButton = addToGrid(gridShape, 0, 0, "Select object", "gfx/select.gif");
		pencilButton = addToGrid(gridShape, 0, 1, "Draw with Pencil", "gfx/pencil.gif");
		lineButton = addToGrid(gridShape, 1, 0, "Draw a Line", "gfx/line.gif");
		rectButton = addToGrid(gridShape, 1, 1, "Draw a Rect", "gfx/rect.gif");
		circleButton = addToGrid(gridShape, 2, 0, "Draw a Circle", "gfx/circle.gif");
		ellipseButton = addToGrid(gridShape, 2, 1, "Draw a Ellipse", "gfx/ellipse.gif");
		polylineButton = addToGrid(gridShape, 3, 0, "Draw a Path", "gfx/polyline.gif");
        textButton = addToGrid(gridShape, 3, 1, "Write Text", "gfx/text.gif");

		deleteButton = addToGrid(gridTransform, 0, 0, "Delete object","gfx/delete.gif");
        saveButton = addToGrid(gridTransform, 0, 1, "Save SVG to page","gfx/save.gif");
        backButton = addToGrid(gridTransform, 1, 0, "Send object Back","gfx/back.gif");
		frontButton = addToGrid(gridTransform, 1, 1, "Send object Front","gfx/front.gif");

        apanel.add(focus);

        focus.add(panel);
        panel.add(this.canvas, DockPanel.CENTER);
		panel.add(this.buttonPanel, DockPanel.WEST);
        panel.add(this.currentFontFamily, DockPanel.SOUTH);
        panel.add(this.currentFontSize, DockPanel.SOUTH);
        panel.add(this.currentFontStyle, DockPanel.SOUTH);
        panel.add(this.currentFontWeight, DockPanel.SOUTH);
        this.apanel.setSize("100%", "100%");
        focus.addKeyboardListener(this);
        focus.setSize("100%", "100%");
    }


    /**
     * This method handles the mouse double click event on any graphic object and in the graphic canvas
     * @param graphicObject  The graphic object which has been double clicked
     * @param event          A reference to the mouse event
     */
	public void mouseDblClicked(GraphicObject graphicObject, Event event) {
        /** if it's a polyline, then we close it's path and therefore finish it */
        if(action.isPolyline() && (curr_obj != null)) {
            ((Path)this.curr_obj).closePath();
            curr_obj = null;
			aux_obj = null;

        /** if the user double clicked on a text object, a textbox or the text itself, this method spawns a Textarea above the text in the canvas so that the user can edit the text that was created.,  */
        } else if(action.isSelect() && (graphicObject != null) && (graphicObject.getType() == 7 || graphicObject.getType() == 8) ) {
            this.curr_obj = graphicObject;

            /** TextArea initialization */
            this.editor = new TextArea();
            this.editor.setWidth((int)(graphicObject.getBounds().getWidth()+20)+"px");
            this.editor.setHeight((int)(graphicObject.getBounds().getHeight()+20)+"px");
            if(graphicObject.getType() == 7) {
                this.editor.setText( ((Text)graphicObject).getText() );
            } else {
                this.editor.setText( ((TextBox)graphicObject).text.getText() );
            }
            /** We add a keyboard listener to handle the Esc key. In the event of a Esc key, the TextArea should disapear and the text object should be updated. */
            this.editor.addKeyboardListener(new KeyboardListenerAdapter() {
                public void onKeyDown(Widget sender, char keyCode, int modifiers) {
                    if (keyCode == (char) KEY_ESCAPE) {

                        editor.cancelKey();

                        aux_obj = null;
                        aux_obj = new Text(editor.getText());
                        if(curr_obj.getType() == 7) {
                            ((Text)aux_obj).setFont( ((Text)curr_obj).getFont());
                            addTextObject(aux_obj,  (int)curr_obj.getX(), (int)curr_obj.getY());

                            canvas.remove(((Text)curr_obj).bounder);
                        } else {
                            ((Text)aux_obj).setFont( ((TextBox)curr_obj).text.getFont());
                            addTextObject(aux_obj,  (int)((TextBox)curr_obj).text.getX(), (int)((TextBox)curr_obj).text.getY());
                            canvas.remove(((TextBox)curr_obj).text);
                        }
                        canvas.remove(curr_obj);

                        curr_obj = null;
                        curr_obj = new TextBox(aux_obj.getBounds(), (Text)aux_obj);
                        addTextBox(curr_obj, (int)aux_obj.getBounds().getX(), (int)aux_obj.getBounds().getY());
                        ((Text)aux_obj).bounder = (TextBox)curr_obj;

                        Color textColor = new Color(aux_obj.getFillColor().getRed(), aux_obj.getFillColor().getGreen(), aux_obj.getFillColor().getBlue(), 0);
                        curr_obj.setFillColor(textColor);
                        curr_obj.setStroke(textColor, 1);

                        aux_obj = null;
                        curr_obj = null;
                        apanel.remove(editor);
                        editor = null;
                    }
                }
            });
            this.apanel.add(editor, (int)this.curr_obj.getX() + this.canvas.getAbsoluteLeft() - Window.getScrollLeft() - 2, (int)this.curr_obj.getY() + this.canvas.getAbsoluteTop() - Window.getScrollTop() - 2);
        }
    }

    /**
     * This method handles the mouse click event on any graphic object and in the graphic canvas
     * @param graphicObject   The graphic object which has been double clicked
     * @param event           A reference to the mouse event
     */
    public void mouseClicked(GraphicObject graphicObject, Event event) {
		int x, y;

        /** if there's no graphic Object selected, we unSelect any other that was previously selected */
        if(action.isSelect() && (graphicObject == null)) {
			if(curr_obj!= null) {
                this.unSelect();
                this.restoreStyle();
            }
			
		} else {
			x = DOM.eventGetClientX(event) - canvas.getAbsoluteLeft() + Window.getScrollLeft();
			y = DOM.eventGetClientY(event) - canvas.getAbsoluteTop() + Window.getScrollTop(); 
			
			
			if(action.isPolyline() && curr_obj == null) {
                /* If there is no Polyline object created, starts to draw a Polyline */
                curr_obj = new Path();
                ((Path)curr_obj).moveTo(x, y);

                addGraphicObject( curr_obj, 0, 0);
                this.currentPath = ((Path)this.curr_obj).commands;
			}
		}
		
	}


    /**
     * This method handles the mouse mouve event on any graphic object and in the graphic canvas.
     * Mainly, this class is used to animate the object's creation so that the user can have an idea of the final object before it really creates it.
     * @param graphicObject   The graphic object over which the mouse move has been done
     * @param event           A reference to the mouse event
     */
	public void mouseMoved(GraphicObject graphicObject, Event event) {
		int x, y;
		
		x = DOM.eventGetClientX(event) - canvas.getAbsoluteLeft() + Window.getScrollLeft();
		y = DOM.eventGetClientY(event) - canvas.getAbsoluteTop() + Window.getScrollTop(); 
		
		if(action.isSelect()) {
            /** If the user clicked on the object, it then performs a translation */
            if((isMovable) && (curr_obj != null)) {
				curr_obj.uTranslate(x - lastPosition[0], y - lastPosition[1]);
				transformPoints.uTranslate(x - lastPosition[0], y - lastPosition[1]);

                lastPosition[0] = x;
				lastPosition[1] = y;
            /** If the user has clicked on a Transformation Point, it performs the given transformation */
            } else if(isTransformable) {
				transformObject(x, y);
			}

        /* Drawing with pencil. This adds a new point to the path */
        } else if(action.isPencil() && (curr_obj != null)) {
			((Path)curr_obj).lineTo(x, y);

        /* Drawing a line. This updates the line end point */
        } else if(action.isLine() && (curr_obj != null)) {
			canvas.remove(curr_obj);
			
			curr_obj = new Line(0, 0, x - objPosition[0], y - objPosition[1]);
			addGraphicObject( curr_obj, objPosition[0], objPosition[1]);

        /* Drawing a rectangle. This updates the rectangle's size and position*/
        } else if(action.isRectangle() && (curr_obj != null)) {
			canvas.remove(curr_obj);
			
			/* Lower right corner */
			if(x > objPosition[0] && y > objPosition[1]) {	
				curr_obj = new Rect(x - objPosition[0], y - objPosition[1]);
				addGraphicObject( curr_obj, objPosition[0], objPosition[1]);
				
			/* Upper right corner*/
			} else if(x > objPosition[0] && y < objPosition[1]) {
				curr_obj = new Rect(x - objPosition[0], objPosition[1] - y);
				addGraphicObject( curr_obj, objPosition[0], y);
			
			/* Lower left corner*/
			} else if(x < objPosition[0] && y > objPosition[1]) {
				curr_obj = new Rect(objPosition[0] - x, y - objPosition[1]);
				addGraphicObject( curr_obj, x, objPosition[1]);
			
			/* Upper left corner*/
			} else if(x < objPosition[0] && y < objPosition[1]) {
				curr_obj = new Rect(objPosition[0] - x, objPosition[1] - y);
				addGraphicObject( curr_obj, x, y);				
			}

        /* Drawing a circle. This updates the circle's diameter */
        } else if(action.isCircle() && (curr_obj != null)) {
			int abs_x = Math.abs(x - objPosition[0]);
			int abs_y = Math.abs(y - objPosition[1]);
			
			canvas.remove(curr_obj);
			
			if(abs_x > abs_y) {
				curr_obj = new Circle(abs_x);
			} else {
				curr_obj = new Circle(abs_y);
			}
			addGraphicObject( curr_obj, objPosition[0], objPosition[1] );

        /* Drawing a ellipse. This updates both ellipse's diameters  */
        } else if(action.isEllipse() && (curr_obj != null)) {
			canvas.remove(curr_obj);
			
			/* Lower right corner */
			if(x > objPosition[0]+1 && y > objPosition[1]+1) {
				curr_obj = new Ellipse((x - objPosition[0])/2, (y - objPosition[1])/2);
				addGraphicObject( curr_obj, objPosition[0] + ((x - objPosition[0])/2), objPosition[1] + ((y - objPosition[1])/2));
				
			/* Upper right corner*/
			} else if(x > objPosition[0]+1 && y+1 < objPosition[1]) {				
				curr_obj = new Ellipse((x - objPosition[0])/2, (objPosition[1] - y)/2 );
				addGraphicObject( curr_obj, objPosition[0] + ((x - objPosition[0])/2), objPosition[1] - ((objPosition[1] - y)/2));
			
			/* Lower left corner*/
			} else if(x+1 < objPosition[0] && y > objPosition[1]+1) {
				curr_obj = new Ellipse((objPosition[0] - x)/2, (y - objPosition[1])/2);
				addGraphicObject( curr_obj, objPosition[0] - ((objPosition[0] - x)/2), objPosition[1] + ((y - objPosition[1])/2));
			
			/* Upper left corner*/
			} else if(x+1 < objPosition[0] && y+1 < objPosition[1]) {
				curr_obj = new Ellipse((objPosition[0] - x)/2, (objPosition[1] - y)/2);
				addGraphicObject( curr_obj, objPosition[0] - ((objPosition[0] - x)/2), objPosition[1] - ((objPosition[1] - y)/2));				
			}

        /** Drawing a TextBox. This updates the TextBox's size and position. */
        } else if(action.isTextBox() && (curr_obj != null)) {
			canvas.remove(curr_obj);

			/* Lower right corner */
			if(x > objPosition[0] && y > objPosition[1]) {
				curr_obj = new com.objetdirect.tatami.client.gfx.TextBox(x - objPosition[0], y - objPosition[1], null);
				addTextBox( curr_obj, objPosition[0], objPosition[1]);

			/* Upper right corner*/
			} else if(x > objPosition[0] && y < objPosition[1]) {
				curr_obj = new com.objetdirect.tatami.client.gfx.TextBox(x - objPosition[0], objPosition[1] - y, null);
				addTextBox( curr_obj, objPosition[0], y);

			/* Lower left corner*/
			} else if(x < objPosition[0] && y > objPosition[1]) {
				curr_obj = new com.objetdirect.tatami.client.gfx.TextBox(objPosition[0] - x, y - objPosition[1], null);
				addTextBox( curr_obj, x, objPosition[1]);

			/* Upper left corner*/
			} else if(x < objPosition[0] && y < objPosition[1]) {
				curr_obj = new com.objetdirect.tatami.client.gfx.TextBox(objPosition[0] - x, objPosition[1] - y, null);
				addTextBox( curr_obj, x, y);
			}

        /** Drawing a polyline, this updates the current point's position */
        } else if(this.action.isPolyline() && (this.curr_obj != null)) {
            if(DOM.eventGetButton(event) == Event.BUTTON_LEFT) {

                this.canvas.remove(this.curr_obj);
                this.curr_obj = new Path(this.currentPath);
                ((Path)this.curr_obj).smoothCurveTo( x, y, lastPosition[0], lastPosition[1]);
                this.addGraphicObject(this.curr_obj, 0, 0);
            } else {
                this.canvas.remove(this.curr_obj);
                this.curr_obj = new Path(this.currentPath);
                ((Path)this.curr_obj).lineTo(x, y);
                this.addGraphicObject(this.curr_obj, 0, 0);
            }
        }
    }

    /**
     * This method handles the mouse press event on any graphic object and in the graphic canvas.
     * It's mainly used to start creating objects and also to select objects.
     * @param graphicObject   The graphic object which has been pressed
     * @param event           A reference to the mouse event
     */
	public void mousePressed(GraphicObject graphicObject, Event event) {
		int x, y;
		
		x = DOM.eventGetClientX(event) - canvas.getAbsoluteLeft() + Window.getScrollLeft();
		y = DOM.eventGetClientY(event) - canvas.getAbsoluteTop() + Window.getScrollTop(); 
		
		if(!action.isSelect() && !action.isPolyline() && !action.isTextBox() && !action.isPencil()) {

            switch(action.getAction()) {

			
				case Action.LINE:
					/* Starts to draw a line */
					curr_obj = new Line(0, 0, 1, 1);
					break;
				case Action.RECTANGLE:
					/* Starts to draw a rectangle */
					curr_obj = new Rect(1, 1);
					break;
				
				case Action.CIRCLE:
					/* Starts to draw a circle */
					curr_obj = new Circle(1);
					break;
				
				case Action.ELLIPSE:
					/* Starts to draw a Ellipse */
					curr_obj = new Ellipse(1, 1);
					break;

            }
			
			lastPosition[0] = x;
			lastPosition[1] = y;
			
			objPosition[0] = x;
			objPosition[1] = y;
			
			addGraphicObject( curr_obj, x, y);
			
		/* Selects a object */
		} else if(action.isSelect() && (graphicObject != null)) {
            /** If there was another object previously selected, unselect it. */
            if(curr_obj != null && graphicObject != curr_obj && graphicObject.getGroup() != transformPoints) {
                unSelect();
            }

            /** If we didn't click on any Transform Point, then we select the object. */
            if((graphicObject.getGroup() != transformPoints)) {

                if(curr_obj == null) {
                    Rectangle bnds = graphicObject.getBounds();
                    if(bnds != null) {
                        /** This may seem strange but for some object types, mainly Paths and Ellipses, Tatami's method getBounds() will return null until the object is translated. */
                        graphicObject.translate(1, 1);
                        graphicObject.translate(-1, -1);
                        if(bnds.getHeight() == 0 && bnds.getWidth() == 0) {
                            bnds = graphicObject.getBounds();
                        }

                        this.backupStyle();

                        fillOpacity.setValue(graphicObject.uGetFillColor().getAlpha());
                        strokeOpacity.setValue(graphicObject.getStrokeColor().getAlpha());

                        currentFillColor = graphicObject.uGetFillColor();
                        DOM.setStyleAttribute(fill.getElement(), "backgroundColor",currentFillColor.toCss(false));
                        currentFillColor.setAlpha(graphicObject.uGetFillColor().getAlpha());

                        currentStrokeColor = graphicObject.getStrokeColor();
                        DOM.setStyleAttribute(stroke.getElement(), "backgroundColor",currentStrokeColor.toCss(false));
                        this.currentStrokeSize.setValue(graphicObject.getStrokeWidth());
                        //chooseStrokeSize(graphicObject.getStrokeWidth()-1, graphicObject.getStrokeWidth());
                        createTransformPoints(bnds, graphicObject);
                    }

                    curr_obj = graphicObject;
                }
                lastPosition[0] = x;
                lastPosition[1] = y;
                objPosition[0] = x;
                objPosition[1] = y;
                isMovable = true;

            /** if the user clicked on a transform point, this settles the variables for the object tranformation. */
            } else {
                lastPosition[0] = x;
                lastPosition[1] = y;
                isTransformable = true;
                aux_obj = curr_obj;
				curr_obj = graphicObject;
                currRotation = 0.0;
                if(curr_obj == transformPointers[Action.ROTATE]) {
                    canvas.remove(transformPoints);
                    transformPoints.clear();
                }
            }

        /** Starts to draw a TextBox.
         *  To add usability, a Text object is allways composed by a Text object and a TextBox. The TextBox is simillar to a Rectangle but it's alpha values are set to 0(it's transparent) and has the size of the text's bounds.
         *  This allows the user to select a Text object more easily because now he has a rectangle with the size of the Text's boundaries to click on. The TextBox contains a link to the Text (and also the Text to the TextBox) so when a user click on it, we know which Text Object is selected and perform the given actions on it as well.
         *  Note: This weren't supported by Tatami, i had to implement it.
         *  */
        } else if(this.action.isTextBox()) {

            this.curr_obj = new com.objetdirect.tatami.client.gfx.TextBox(1.0, 1.0, null);
            this.lastPosition[0] = x;
			this.lastPosition[1] = y;

			this.objPosition[0] = x;
			this.objPosition[1] = y;

			this.addTextBox( this.curr_obj, x, y);
        } else if(this.action.isPencil()) {
            /* Starts to draw with the pencil */
            curr_obj = new Path();
            ((Path)curr_obj).moveTo(x, y);
            Color fill = new Color(this.currentFillColor.getRed(), this.currentFillColor.getGreen(), this.currentFillColor.getBlue(), 0);

            objPosition[0] = x;
			objPosition[1] = y;

            curr_obj.setFillColor(fill);
            curr_obj.setStroke(currentStrokeColor, currentStrokeSize.getValue());
            canvas.add(curr_obj, 0, 0);
        /* Otherwise it adds a new point in the Polyline */
        } else if(this.action.isPolyline() && curr_obj != null) {
            this.lastPosition[0] = x;
            this.lastPosition[1] = y;
        }
	}

    /**
     * This method handles the mouse release event on any graphic object and in the graphic canvas
     * @param graphicObject   The graphic object over which the mouse release has been done
     * @param event           A reference to the mouse event
     */
	public void mouseReleased(GraphicObject graphicObject, Event event) {
        int x, y;

		x = DOM.eventGetClientX(event) - canvas.getAbsoluteLeft() + Window.getScrollLeft();
		y = DOM.eventGetClientY(event) - canvas.getAbsoluteTop() + Window.getScrollTop();

        /** If we were draing with the pencil, then we need to finish it */
		if((curr_obj!= null) && action.isPencil()) {
            curr_obj = null;

        /** If we were with a object selected, we need to update the variables to finish the transformation or translation, given the case. */
        } else if((curr_obj != null) && action.isSelect()) {
            if(isTransformable) {

                /** This is needed because to increase performance during object rotation, the transform points aren't displayed. This is needed because this class is performing the Transform Points rotation and not Tatami(it doesn't works..). */
                if(curr_obj == transformPointers[Action.ROTATE]) {
                    Rectangle bnds = aux_obj.getBounds();
                    canvas.remove(transformPoints);
                    transformPoints.clear();
                    createTransformPoints(bnds, aux_obj);
                }

                curr_obj = aux_obj;
                isTransformable = false;
            } else {
                isMovable = false;
            }
        /** If we were creating a TextBox, then we create a TextArea to allow the user to write and handle the Esc key functionality. */
        } else if(curr_obj != null && action.isTextBox()) {
            this.action.setAction(Action.TEXT);

            this.editor = new TextArea();
            this.editor.setWidth((int)(this.curr_obj.getBounds().getWidth()+2)+"px");
            this.editor.setHeight((int)(this.curr_obj.getBounds().getHeight()+2)+"px");
            this.editor.setFocus(true);
            this.editor.setEnabled(true);
            this.editor.addKeyboardListener(new KeyboardListenerAdapter() {
                public void onKeyDown(Widget sender, char keyCode, int modifiers) {
                    if (keyCode == (char) KEY_ESCAPE) {
                        int size = Integer.parseInt(currentFontSize.getItemText(currentFontSize.getSelectedIndex()));
                        int x = (int)aux_obj.getX(), y = (int)aux_obj.getY();
                        editor.cancelKey();

                        curr_obj = new Text(editor.getText());
                        ((Text)curr_obj).setFont(currFont);
                        addTextObject(curr_obj, x + (int)((1.0/4.0)*size), y+ (size) + (int)((1.0/4.0)*size));

                        canvas.remove(aux_obj);
                        aux_obj = new TextBox(curr_obj.getBounds(), (Text)curr_obj);
                        addTextBox(aux_obj, (int)curr_obj.getBounds().getX(), (int)curr_obj.getBounds().getY());
                        ((Text)curr_obj).bounder = (TextBox)aux_obj;

                        Color textColor = new Color(curr_obj.getFillColor().getRed(), curr_obj.getFillColor().getGreen(), curr_obj.getFillColor().getBlue(), 0);
                        aux_obj.setFillColor(textColor);
                        aux_obj.setStroke(textColor, 1);

                        aux_obj = null;
                        curr_obj = null;
                        apanel.remove(editor);
                        editor = null;
                        action.setAction(Action.TEXTBOX);
                    }
                }
            });

            this.apanel.add(editor, (int)this.curr_obj.getX() + this.canvas.getAbsoluteLeft() - Window.getScrollLeft() - 2, (int)this.curr_obj.getY() + this.canvas.getAbsoluteTop() - Window.getScrollTop() - 2);
            this.aux_obj = this.curr_obj;
            this.curr_obj = null;
            
        } else if(curr_obj != null && this.action.isPolyline()) {
            this.canvas.remove(curr_obj);
            this.curr_obj = new Path(this.currentPath);
            if(x != this.lastPosition[0] && y != this.lastPosition[1]) {
                ((Path)this.curr_obj).smoothCurveTo( x, y, lastPosition[0], lastPosition[1]);
            } else {
                ((Path)this.curr_obj).lineTo( x, y);
            }
            this.addGraphicObject(this.curr_obj, 0, 0);
            this.currentPath = ((Path)this.curr_obj).commands;

        } else if(curr_obj != null) {
            curr_obj = null;
		}
	}

    public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        /** Not used so far. It may be needed to increase usability on the application. */
    }

    public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        if(this.action.isPolyline() && this.curr_obj != null && keyCode == (char) KEY_ESCAPE) {
            this.canvas.remove(this.curr_obj);
            this.curr_obj = new Path(this.currentPath);
            ((Path)this.curr_obj).closePath();
            this.addGraphicObject(this.curr_obj, 0, 0);
            curr_obj = null;
			aux_obj = null;
            
        }
    }

    public void onKeyUp(Widget sender, char keyCode, int modifiers) {
        /** Not used so far. It may be needed to increase usability on the application. */
    }


    /**
     * This method handles all the button's click events from the gridShape and gridTransform buttons
     * @param sender   The button which has been clicked
     */
	public void onClick(Widget sender) {
        if(sender.equals(this.deleteButton)) {
			this.removeObject();
		} else if(sender.equals(this.backButton)) {
			this.backObject();
		} else if(sender.equals(this.frontButton)) {
			this.frontObject();
		} else if(sender.equals(this.saveButton)) {
			this.saver.saveDocument(this.canvas);
        }else if(sender.equals(this.selectButton)) {
            this.action.setAction(Action.SELECT);
		} else if(sender.equals(this.pencilButton)) {
            this.unSelect();
            this.action.setAction(Action.PENCIL);
		} else if(sender.equals(this.lineButton)) {
            this.unSelect();
            this.action.setAction(Action.LINE);
		} else if(sender.equals(this.rectButton)) {
            this.unSelect();
            this.action.setAction(Action.RECTANGLE);
		} else if(sender.equals(this.circleButton)) {
            this.unSelect();
            this.action.setAction(Action.CIRCLE);
		} else if(sender.equals(this.ellipseButton)) {
            this.unSelect();
            this.action.setAction(Action.ELLIPSE);
		} else if(sender.equals(this.polylineButton)) {
            this.unSelect();
            this.action.setAction(Action.POLYLINE);
        } else if(sender.equals(this.textButton)) {
            this.unSelect();
            this.action.setAction(Action.TEXTBOX);
        } else if(sender.equals(this.strokeButton)) {
            this.showPopUpStroke();
		} else if(sender.equals(this.fill)) {
            this.showPopupColorFill(true);
        } else if(sender.equals(this.stroke)) {
            this.showPopupColorFill(false);
        }
		
	}

    /**
     * This method implements the onChange listener of the Opacity slider and the Text's ListBoxes
     * @param sender    The opacity slider
     */
	public void onChange(Widget sender) {
        /** if there was a change in opacity, we update the currentFillColor and the selected object if there's any. */
        if (sender.equals(this.fillOpacity)) {
			int value = this.fillOpacity.getValue();
			if (this.curr_obj != null) {
				final Color color = this.curr_obj.getFillColor();
				Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), value);
				this.curr_obj.uSetFillColor(newColor);

            } else {
                this.currentFillColor.setAlpha(value);
			}


        }else if(sender.equals(this.strokeOpacity)) {
            int value = this.strokeOpacity.getValue();

            if(this.curr_obj != null) {
				final Color color = this.curr_obj.getStrokeColor();
				Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), value);
				this.curr_obj.uSetStroke(newColor, this.curr_obj.getStrokeWidth());

            } else {
                this.currentStrokeColor.setAlpha(value);
			}
        /** If there was any change in the ListBoxes we update the current font. */
        } else if(sender.equals(this.currentFontFamily) || sender.equals(this.currentFontSize) || sender.equals(this.currentFontStyle) || sender.equals(this.currentFontWeight)) {
            this.updateFont();
        } else if(sender.equals(this.currentStrokeSize) && this.curr_obj != null) {
            this.curr_obj.uSetStroke(this.currentStrokeColor, this.currentStrokeSize.getValue());
        }
	}


    private void showPopUpStroke() {
        final PopupPanel popupStroke = new PopupPanel(true);
        popupStroke.addStyleName("color_Pop-up");

        TabPanel tabPanel = new TabPanel();

        tabPanel.add(this.currentStrokeSize, new Label("Stroke"));
        tabPanel.selectTab(0);

        popupStroke.add(tabPanel);
        popupStroke.setPopupPosition(this.strokeButton.getAbsoluteLeft(), this.strokeButton.getAbsoluteTop());
        popupStroke.show();
    }

    /**
	 * This method creates a Pop-up to display the color chooser and the opacity slider
     * @param isFill   boolean that represents if it's the fill color pop-up or the stroke color pop-up
     */
	private void showPopupColorFill(final boolean isFill) {

		final PopupPanel popupColor = new PopupPanel(true);
        popupColor.addStyleName("color_Pop-up");
		
		TabPanel tabPanel = new TabPanel();
        VerticalPanel colPanel = new VerticalPanel();
		colPanel.setSpacing(5);

		final ColorChooser colorChooser = new ColorChooser();
		
		colPanel.add(colorChooser);

		tabPanel.add(colPanel, new Label("Color"));
		

		ChangeListener colorChange = new ChangeListener() {
			public void onChange(Widget sender) {
				String color = colorChooser.getColor();
				Color colorSelected = Color.getColor(color);
				if (isFill) {
					currentFillColor = colorSelected;
					DOM.setStyleAttribute(fill.getElement(), "backgroundColor",color);
					currentFillColor.setAlpha(fillOpacity.getValue());
					if (curr_obj != null) {
                        curr_obj.uSetFillColor(currentFillColor);
                    }
				} else {
					currentStrokeColor = colorSelected;
					DOM.setStyleAttribute(stroke.getElement(), "backgroundColor",color);
					currentStrokeColor.setAlpha(strokeOpacity.getValue());

                    /** Mudar para grupos **/
					if (curr_obj != null) {
                        curr_obj.uSetStroke(currentStrokeColor, currentStrokeSize.getValue());
                    }
				}
			}
		};
		colorChooser.addChangeListener(colorChange);

        if(isFill) {
            tabPanel.add(this.fillOpacity, new Label("Opacity"));
        } else {
            tabPanel.add(this.strokeOpacity, new Label("Opacity"));
        }

        tabPanel.selectTab(0);
		popupColor.add(tabPanel);
		popupColor.setPopupPosition(this.fill.getAbsoluteLeft(), this.stroke.getAbsoluteTop());
		popupColor.show();
	}

    /**
	 * This method adds a icon to the Grid given the grid, the position and the image
	 * @param grid  the Button grid
	 * @param row   the row number
	 * @param col   the colum number
	 * @param title  the title of the button
	 * @param icon  the path to the icon used
	 * @return  returns an reference to the image added
	 */
	private Image addToGrid(Grid grid, int row, int col, String title, String icon) {
		Image button = new Image(icon);
		button.setTitle(title);
		button.setSize("34px", "34px");
		grid.setWidget(row, col, button);
		button.addClickListener(this);
		return button;
	}


    /**
	 * This method adds a given Graphic Object to the Graphic Canvas with the default stroke and fill caracteristics.
	 * @param g_obj   The graphic object to be added
	 * @param x       The X coordinate where it should be added
	 * @param y       The Y coordinate where it should be added
	 */
	private void addGraphicObject(GraphicObject g_obj, int x, int y) {
		g_obj.setFillColor(this.currentFillColor);
        g_obj.setStroke(this.currentStrokeColor, this.currentStrokeSize.getValue());

		this.canvas.add(g_obj, x, y);
	}

    /**
	 * This method adds a given TextBox to the Graphic Canvas
	 * @param g_obj   The graphic object to be added
	 * @param x       The X coordinate where it should be added
	 * @param y       The Y coordinate where it should be added
	 */
	private void addTextBox(GraphicObject g_obj, int x, int y) {
        Color boxColor = new Color(119, 136, 153, 30);

        g_obj.setFillColor(boxColor);
        g_obj.setStroke(Color.GRAY, 1);

		this.canvas.add(g_obj, x, y);
	}

    /**
	 * This method adds a given Text object to the Graphic Canvas
	 * @param g_obj   The graphic object to be added
	 * @param x       The X coordinate where it should be added
	 * @param y       The Y coordinate where it should be added
	 */
    private void addTextObject(GraphicObject g_obj, int x, int y) {
        Color stroke = new Color(0, 0, 0, 0);

        g_obj.setFillColor(this.currentFillColor);
        g_obj.setStroke(stroke, 0);

		this.canvas.add(g_obj, x, y);
	}

    /**
     * This method handles the object's scalling and rotation when a user click on any transform point.
     *
     * If the user click on the rotation point, it will calculate the angle from the vector composed by the object's origin and the mouse coordinates with the vector composed from the object's origin to the rotation point, increasing it counter-clockwise.
     *
     * On the other hand, if the user clicks on any of the eight transformation points, the method will identify which one the user is clicking and therefore perform the scalling and the translation needed acording to the object's center(X,Y) point.
     * Note: To overlap a Tatami's limitation, if the current object has a rotation degree, first we rotate the object, the transformation points and the mouse cordinates back to the angle 0, perform the scalling and translation needed and then rotate back to the previous angle. This is needed because Tatami's scalling crashes with rotated objects.
     *
     * @param x      The X coordinate of the user's mouse
     * @param y      The Y coordinate of the user's mouse
     */
    private void transformObject(int x, int y) {
        double sx, sy, l, r, nx, ny, tx, ty, alpha, origCX = 0, origCY = 0;
        Rectangle bnds;

        if(curr_obj == transformPointers[Action.ROTATE]) {
            /* the mouse vector 1 */
            GeoVector mouseVector = new GeoVector(x, y, this.aux_obj.getCenterX(), this.aux_obj.getCenterY());

            /* the rotation vector. i.e. the fixed vector that will be used as origin composed by the subtraction of the object center with the rotation point */
            GeoVector rotationVector = new GeoVector(this.lastPosition[0], this.lastPosition[1], this.aux_obj.getCenterX(), this.aux_obj.getCenterY());

            mouseVector.normalize();
            rotationVector.normalize();

            alpha = mouseVector.getAngleWith(rotationVector.getX(), rotationVector.getY());

            /** After passing the 180 degrees, the atan2 function returns a negative angle from 0 to PI. So this will convert the final gobal angle to 0-2PI */
            if(alpha < 0 ) {
                alpha = (2 * Math.PI) + alpha;    
            }

            alpha -= this.currRotation;
            this.currRotation += alpha;

            Point c = new Point(this.aux_obj.getCenterX(), this.aux_obj.getCenterY());
            this.aux_obj.uRotate((float)(-1.0*(alpha*(180/Math.PI))), c);
            
        } else {
            alpha = this.aux_obj.getRotation();

            /** Here we rotate the selected Graphic Object, it's tranformation points and the mouse coordinates back to the zero angle, to permit a correct object scalling. */
            if(alpha != 0.0) {
                origCX = this.aux_obj.getCenterX();
                origCY = this.aux_obj.getCenterY();
                this.aux_obj.uRotate((float)(alpha*-1.0), this.aux_obj.getCenter());
                rotateTransformPoints(alpha);
                GeoVector mouseCoord = new GeoVector(x, y, this.aux_obj.getCenterX(), this.aux_obj.getCenterY());

                mouseCoord.rotate( ((alpha*-1.0) * (2.0*Math.PI))/360 );
                mouseCoord.addPoint(this.aux_obj.getCenterX(), this.aux_obj.getCenterY());
                x = (int)mouseCoord.getX();
                y = (int)mouseCoord.getY();

            }

            /** Tatami rotates the object from it's x and y point to the edges. So this means that sometimes we need to translate the object a few pixels to asure that it's upper left corner is on the same position. */
            if(curr_obj == transformPointers[Action.NORTHWEST]) {
                if(x < (transformPointers[Action.EAST].getX()-2) && y < (transformPointers[Action.SOUTH].getY()-2)) {
                    l = aux_obj.getX() - (transformPointers[Action.WEST].getX()+2);
                    r = (transformPointers[Action.EAST].getX()+2) - aux_obj.getX();
                    nx = (transformPointers[Action.EAST].getX()+2) - x ;
                    sx = nx / (l+r);
                    tx = (sx*l-l) + (sx*r-r);

                    l = aux_obj.getY() - (transformPointers[Action.NORTH].getY()+2);
                    r = (transformPointers[Action.SOUTH].getY()+2) - aux_obj.getY();
                    ny = (transformPointers[Action.SOUTH].getY()+2) - y;
                    sy = ny / (l+r);
                    ty = (sy*l-l) + (sy*r-r);

                    aux_obj.uTranslate((int)-tx, (int)-ty);
                    aux_obj.scale( (float)sx, (float)sy);

                    if(alpha != 0.0) {
                        this.aux_obj.uTranslate((int)(origCX - this.aux_obj.getCenterX()), (int)(origCY - this.aux_obj.getCenterY()));
                        this.aux_obj.rotate((float)alpha);
                    }

                    canvas.remove(transformPoints);
                    transformPoints.clear();
                    bnds = aux_obj.getBounds();
                    createTransformPoints(bnds, aux_obj);
                    curr_obj = transformPointers[Action.NORTHWEST];
                }
            } else if(curr_obj == transformPointers[Action.NORTH]) {
                if(y < (transformPointers[Action.SOUTH].getY()-2)) {
                    l = aux_obj.getY() - (transformPointers[Action.NORTH].getY()+2);
                    r = (transformPointers[Action.SOUTH].getY()+2) - aux_obj.getY();
                    ny = (transformPointers[Action.SOUTH].getY()+2) - y;
                    sy = ny / (l+r);
                    ty = (sy*l-l) + (sy*r-r);

                    aux_obj.uTranslate(0, (int)-ty);
                    aux_obj.scale( 1, (float)sy);

                    if(alpha != 0.0) {
                        this.aux_obj.uTranslate((int)(origCX - this.aux_obj.getCenterX()), (int)(origCY - this.aux_obj.getCenterY()));
                        this.aux_obj.rotate((float)alpha);
                    }

                    canvas.remove(transformPoints);
                    transformPoints.clear();
                    bnds = aux_obj.getBounds();
                    createTransformPoints(bnds, aux_obj);
                    curr_obj = transformPointers[Action.NORTH];
                }
            } else if(curr_obj == transformPointers[Action.NORTHEAST]) {
                if(x > (transformPointers[Action.WEST].getX()+2) && y < (transformPointers[Action.SOUTH].getY()-2)) {
                    l = aux_obj.getX() - (transformPointers[Action.WEST].getX()+2);
                    r = (transformPointers[Action.EAST].getX()+2) - aux_obj.getX();
                    nx = x - (transformPointers[Action.WEST].getX()+2);
                    sx = nx / (l+r);
                    tx = sx*l-l;

                    l = aux_obj.getY() - (transformPointers[Action.NORTH].getY()+2);
                    r = (transformPointers[Action.SOUTH].getY()+2) - aux_obj.getY();
                    ny = (transformPointers[Action.SOUTH].getY()+2) - y;
                    sy = ny / (l+r);
                    ty = (sy*l-l) + (sy*r-r);

                    aux_obj.uTranslate((int)tx, (int)-ty);
                    aux_obj.scale( (float)sx, (float)sy);

                    if(alpha != 0.0) {
                        this.aux_obj.uTranslate((int)(origCX - this.aux_obj.getCenterX()), (int)(origCY - this.aux_obj.getCenterY()));
                        this.aux_obj.rotate((float)alpha);
                    }

                    canvas.remove(transformPoints);
                    transformPoints.clear();
                    bnds = aux_obj.getBounds();
                    createTransformPoints(bnds, aux_obj);
                    curr_obj = transformPointers[Action.NORTHEAST];
                }

            } else if(curr_obj == transformPointers[Action.WEST]) {
                if(x < (transformPointers[Action.EAST].getX()-2) ) {
                    l = aux_obj.getX() - (transformPointers[Action.WEST].getX()+2);
                    r = (transformPointers[Action.EAST].getX()+2) - aux_obj.getX();
                    nx = (transformPointers[Action.EAST].getX()+2) - x ;
                    sx = nx / (l+r);
                    tx = (sx*l-l) + (sx*r-r);

                    aux_obj.uTranslate((int)-tx, 0);
                    aux_obj.scale( (float)sx, 1);

                    if(alpha != 0.0) {
                        this.aux_obj.uTranslate((int)(origCX - this.aux_obj.getCenterX()), (int)(origCY - this.aux_obj.getCenterY()));
                        this.aux_obj.rotate((float)alpha);
                    }

                    canvas.remove(transformPoints);
                    transformPoints.clear();

                    bnds = aux_obj.getBounds();
                    createTransformPoints(bnds, aux_obj);
                    curr_obj = transformPointers[Action.WEST];
                }
            } else if(curr_obj == transformPointers[Action.EAST]) {
                if(x > (transformPointers[Action.WEST].getX()+2) ) {
                    l = aux_obj.getX() - (transformPointers[Action.WEST].getX()+2);
                    r = (transformPointers[Action.EAST].getX()+2) - aux_obj.getX();
                    nx = x - (transformPointers[Action.WEST].getX()+2);
                    sx = nx / (l+r);
                    tx = sx*l-l;

                    aux_obj.uTranslate((int)tx, 0);
                    aux_obj.scale( (float)sx, (float)1);

                    if(alpha != 0.0) {
                        this.aux_obj.uTranslate((int)(origCX - this.aux_obj.getCenterX()), (int)(origCY - this.aux_obj.getCenterY()));
                        this.aux_obj.rotate((float)alpha);
                    }

                    canvas.remove(transformPoints);
                    transformPoints.clear();
                    bnds = aux_obj.getBounds();
                    createTransformPoints(bnds, aux_obj);
                    curr_obj = transformPointers[Action.EAST];
                }
            } else if(curr_obj == transformPointers[Action.SOUTHWEST]) {
                if(x < (transformPointers[Action.EAST].getX()-2) && y > (transformPointers[Action.NORTH].getY()+2)) {
                    l = aux_obj.getX() - (transformPointers[Action.WEST].getX()+2);
                    r = (transformPointers[Action.EAST].getX()+2) - aux_obj.getX();
                    nx = (transformPointers[Action.EAST].getX()+2) - x ;
                    sx = nx / (l+r);
                    tx = (sx*l-l) + (sx*r-r);

                    l = aux_obj.getY() - (transformPointers[Action.NORTH].getY()+2);
                    r = (transformPointers[Action.SOUTH].getY()+2) - aux_obj.getY();
                    ny = y - (transformPointers[Action.NORTH].getY()+2);
                    sy = ny / (l+r);
                    ty = sy*l-l;

                    aux_obj.uTranslate((int)-tx, (int)ty);
                    aux_obj.scale( (float)sx, (float)sy);

                    if(alpha != 0.0) {
                        this.aux_obj.uTranslate((int)(origCX - this.aux_obj.getCenterX()), (int)(origCY - this.aux_obj.getCenterY()));
                        this.aux_obj.rotate((float)alpha);
                    }

                    canvas.remove(transformPoints);
                    transformPoints.clear();
                    bnds = aux_obj.getBounds();
                    createTransformPoints(bnds, aux_obj);
                    curr_obj = transformPointers[Action.SOUTHWEST];
                }
            } else if(curr_obj == transformPointers[Action.SOUTH]) {
                if(y > (transformPointers[Action.NORTH].getY()+2)) {
                    l = aux_obj.getY() - (transformPointers[Action.NORTH].getY()+2);
                    r = (transformPointers[Action.SOUTH].getY()+2) - aux_obj.getY();
                    ny = y - (transformPointers[Action.NORTH].getY()+2);
                    sy = ny / (l+r);
                    ty = sy*l-l;

                    aux_obj.uTranslate(0, (int)ty);
                    aux_obj.scale( 1, (float)sy);

                    if(alpha != 0.0) {
                        this.aux_obj.uTranslate((int)(origCX - this.aux_obj.getCenterX()), (int)(origCY - this.aux_obj.getCenterY()));
                        this.aux_obj.rotate((float)alpha);
                    }

                    canvas.remove(transformPoints);
                    transformPoints.clear();
                    bnds = aux_obj.getBounds();
                    createTransformPoints(bnds, aux_obj);
                    curr_obj = transformPointers[Action.SOUTH];
                }
            } else if(curr_obj == transformPointers[Action.SOUTHEAST]) {
                if(x > (transformPointers[Action.WEST].getX()+2) && y > (transformPointers[Action.NORTH].getY()+2)) {

                    l = aux_obj.getX() - (transformPointers[Action.WEST].getX()+2);
                    r = (transformPointers[Action.EAST].getX()+2) - aux_obj.getX();
                    nx = x - (transformPointers[Action.WEST].getX()+2);
                    sx = nx / (l+r);
                    tx = sx*l-l;

                    l = aux_obj.getY() - (transformPointers[Action.NORTH].getY()+2);
                    r = (transformPointers[Action.SOUTH].getY()+2) - aux_obj.getY();
                    ny = y - (transformPointers[Action.NORTH].getY()+2);
                    sy = ny / (l+r);
                    ty = sy*l-l;

                    aux_obj.uTranslate((int)tx, (int)ty);
                    aux_obj.scale( (float)sx, (float)sy);

                    if(alpha != 0.0) {
                        this.aux_obj.uTranslate((int)(origCX - this.aux_obj.getCenterX()), (int)(origCY - this.aux_obj.getCenterY()));
                        this.aux_obj.rotate((float)alpha);
                    }

                    canvas.remove(transformPoints);
                    transformPoints.clear();
                    bnds = aux_obj.getBounds();
                    createTransformPoints(bnds, aux_obj);
                    curr_obj = transformPointers[Action.SOUTHEAST];
                }
            }


        }
            
    }

    /**
     * Method created to remove a Graphic Object and it's Transform Points from the Graphic Canvas
     */
    private void removeObject() {
		if(this.curr_obj != null) {
            this.canvas.uRemove(this.curr_obj);
			this.canvas.remove(this.transformPoints);
            this.transformPoints.clear();

            this.curr_obj = null;
        }
	}

    /**
     * Method to send the Graphic Object to the back of the DOM order
     */
    private void backObject() {
		if(this.curr_obj != null) {
            this.curr_obj.uMoveToBack();
        }
	}

    /**
     * Method to bring the Graphic Object to the front of the DOM order
     */
    private void frontObject() {
		if(this.curr_obj != null) {
            this.curr_obj.uMoveToFront();
		}
	}

    /**
     * This method is used to backup the current style when the user selects any object.
     */
    private void backupStyle() {
        if(!this.backedup) {
            this.backedup = true;
            this.lastFillColor = this.currentFillColor;
            this.lastFillOpacity = this.fillOpacity.getValue();
            this.lastStrokeColor = this.currentStrokeColor;
            this.lastStrokeOpacity = this.strokeOpacity.getValue();
            this.lastStrokeSize = this.currentStrokeSize.getValue();
        }
    }


    /**
     * This method is used to restore the previous style when the user deselects any object.
     */
    private void restoreStyle() {
        if(this.backedup) {
            this.backedup = false;

            this.currentFillColor = this.lastFillColor;
            DOM.setStyleAttribute(fill.getElement(), "backgroundColor",currentFillColor.toCss(false));
            this.fillOpacity.setValue(this.lastFillOpacity);
            this.currentFillColor.setAlpha(this.fillOpacity.getValue());

            this.currentStrokeColor = this.lastStrokeColor;
            DOM.setStyleAttribute(stroke.getElement(), "backgroundColor",currentStrokeColor.toCss(false));
            this.strokeOpacity.setValue(this.lastStrokeOpacity);
            this.currentStrokeColor.setAlpha(this.strokeOpacity.getValue());

            this.currentStrokeSize.setValue(this.lastStrokeSize);
        }
    }


    /**
     * This method adds a Transform Point to the current Graphic Canvas
     *
     * @param x      The X coordinate where the object should be added
     * @param y      The Y coordinate where the object should be added
     * @param index  The index where the Transform Point shoud be added in the array transformPointers
     * @param curr   The GraphicObject for which the transform point is being constructed
     * @param radiansAngle   The GraphicObject's rotation angle in radians.
     */
	private void addTransformPoint(double x, double y, int index, GraphicObject curr, double radiansAngle) {
        /** If the object is rotated, the transform point also has to be rotated.
        *   Here we calculate the new coordinates acording to the rotation angle. */
        if(radiansAngle != 0.0) {
            GeoVector vect = new GeoVector(x, y, curr.getCenterX(), curr.getCenterY());
            vect.rotate(radiansAngle);
            vect.addPoint(curr.getCenterX(), curr.getCenterY());
            x = vect.getX();
            y = vect.getY();
        }
        /** If it's the rotation point, then it creates a green circle */
        if(index==0) {
            Circle aux = new Circle(2);
            aux.setFillColor(Color.GREEN);
            aux.setStroke(Color.GRAY, 1);

            this.transformPoints.add(aux);
		    this.transformPointers[index] = aux;
		    aux.translate((int)x, (int)y);
        /** Otherwise it just creates a normal white rectangle */
        } else {
            Rect aux = new Rect(4,4);
            aux.setFillColor(Color.WHITE);
            aux.setStroke(Color.GRAY, 1);

            this.transformPoints.add(aux);
		    this.transformPointers[index] = aux;
		    aux.translate((int)x-2, (int)y-2);
        }
	}

    /**
     * This method creates the Transform Points of a Graphic object given a Rectangle representing it's boundaries
     * @param bnds   Rectangle that represents the currently select Graphic Object boundaries
     * @param curr   The Graphic Object for which the transform points are being created
     */
    private void createTransformPoints(Rectangle bnds, GraphicObject curr) {
        double degreesAngle, radiansAngle;
        Rectangle novo = bnds;

        this.transformPoints = new VirtualGroup();

        degreesAngle = curr.getRotation();

        /** If the object is rotated, we need to rotate i back to the angle 0 in order to get valid boundaries. Tatami doesn't rotates the object's bounds, instead it gives wrong bounds so we need to perform this operation and rotate the bounds on our side. */
        if(degreesAngle != 0.0) {
            curr.uRotate((float) (degreesAngle * -1.0));
            novo = curr.getBounds();
        }
        radiansAngle = (degreesAngle * (2.0*Math.PI))/360.0;
        
        addTransformPoint(novo.getCenterX(), novo.getY()+novo.getHeight()+(novo.getHeight()/4), 0, curr, radiansAngle);
        addTransformPoint(novo.getX(), novo.getY(), 1, curr, radiansAngle);
        addTransformPoint(novo.getCenterX(), novo.getY(), 2, curr, radiansAngle);
        addTransformPoint(novo.getX() + novo.getWidth(), novo.getY(), 3, curr, radiansAngle);
        addTransformPoint(novo.getX(), novo.getCenterY(), 4, curr, radiansAngle);
        addTransformPoint(novo.getX() + novo.getWidth(), novo.getCenterY(), 5, curr, radiansAngle);
        addTransformPoint(novo.getX(), novo.getY() + novo.getHeight(), 6, curr, radiansAngle);
        addTransformPoint(novo.getCenterX(), novo.getY() + novo.getHeight(), 7, curr, radiansAngle);
        addTransformPoint(novo.getX() + novo.getWidth(), novo.getY() + novo.getHeight(), 8, curr, radiansAngle);

        /** This creates the line between the rotation point and the object */
        Line aux2 = new Line((int)(this.transformPointers[Action.SOUTH].getX()+2), (int)(this.transformPointers[Action.SOUTH].getY()+2), (int)(this.transformPointers[Action.ROTATE].getX()), (int)(this.transformPointers[Action.ROTATE].getY()));
        aux2.setStroke( Color.GRAY, 1);
        this.transformPoints.add(aux2);

        this.canvas.add(this.transformPoints, 0, 0);

        this.transformPointers[Action.SOUTH].moveToFront();
        this.transformPointers[Action.ROTATE].moveToFront();

        if(degreesAngle != 0.0) {
            curr.uRotate((float)degreesAngle);
        }
    }

    /**
     * In this method, we rotate the all the transformation points but the rotation one by "angle" degrees, around the GraphicObject's center. This is needed to implement scalling when the object is rotated
     *
     * @param angle    The angle of the rotation in degrees.
     */
    private void rotateTransformPoints(double angle) {
        int i;
        for(i=1; i<9;i++) {
            GeoVector vec = new GeoVector(this.transformPointers[i].getX(), this.transformPointers[i].getY(), this.aux_obj.getCenterX(), this.aux_obj.getCenterY());
            vec.rotate(((angle*-1.0) * (2.0*Math.PI))/360);
            vec.addPoint(this.aux_obj.getCenterX(), this.aux_obj.getCenterY());
            this.transformPointers[i].translate( (int)(vec.getX() - this.transformPointers[i].getX()), (int)(vec.getY() - this.transformPointers[i].getY()));
        }

    }


    /**
     * This method unselect's the currently object so that the user can perform a new action.
     */
    private void unSelect() {
        /** if we had some object selected we need to delete it's Transformation Points. */
        if(this.action.isSelect() && this.curr_obj != null) {
            this.canvas.remove(this.transformPoints);
            this.transformPoints.clear();
            this.curr_obj = null;

        /** if we were in the middle of a text operation, we have to remove the TextBox and set the text to it's last version*/
        } else if(this.action.isText()) {
            int size = Integer.parseInt(currentFontSize.getItemText(currentFontSize.getSelectedIndex()));

            curr_obj = new Text(editor.getText());
            ((Text)curr_obj).setFont(currFont);
            addTextObject(curr_obj, (int)aux_obj.getX() + (int)((1.0/4.0)*size), (int)aux_obj.getY()+ (size) + (int)((1.0/4.0)*size));

            aux_obj = null;
            curr_obj = null;
            apanel.remove(editor);
            editor = null;
            action.setAction(Action.TEXTBOX);
        }
    }

    /**
     * This method updates the current Font with it's new properties
     */
    private void updateFont() {
        this.currFont = new Font(this.currentFontFamily.getItemText(this.currentFontFamily.getSelectedIndex()),
                                 Integer.parseInt(this.currentFontSize.getItemText(this.currentFontSize.getSelectedIndex())),
                                 this.currentFontStyle.getItemText(this.currentFontStyle.getSelectedIndex()),
                                 Font.NORMAL,
                                 this.currentFontWeight.getItemText(this.currentFontWeight.getSelectedIndex()));    
    }
	
}
