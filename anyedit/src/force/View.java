package force;

/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
 */
import java.util.List;

import model.DocModel;
import model.Translator;
import network.Client;
import network.Payload;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.accessibility.AccessibleListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import figures.BoxFigure;

public class View extends ViewPart {
	Composite top;

	public static final String ID = "force.view";

	private Canvas canvas = null;

	private List a;

	private List<BoxFigure> b;

	private Figure contents;

	private LightweightSystem lws;

	public void createPartControl(Composite parent) {
		top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);
		createCanvas();
		lws = new LightweightSystem(canvas);
		contents = new Figure();
		// XYLayout contentsLayout = new XYLayout();
		FlowLayout contentsLayout = new FlowLayout();
		contents.setLayoutManager(contentsLayout);
	}

	public void transmitKeyEvent(KeyEvent ke) {
		try {
			Payload p = Translator.translate(ke);
			if (p.getTarget().equals("bad text"))
				return;
			System.out.println("Payload generated");// TODO DEBUG
			Client c = Client.getInstance();
			System.out.println("Got Client");// TODO DEBUG
			c.transmitCommand(p);
			System.out.println("Payload sent");// TODO DEBUG
		} catch (Exception e) {
			e.printStackTrace();
		}
		update();
	}

	public void setFocus() {
		update();
	}

	private void update() {
		if (DocModel.getInstance().isUpdated()) {
			contents.erase();
			a = DocModel.getInstance().getRoot().getContent();

			for (Object o : a) {
				Label temp = new Label(o.toString());
				// TODO add if-thens for other object types
				BoxFigure bf = new BoxFigure(temp);
				b.add(bf);
			}
			drawContents();
		}
	}

	private void drawContents() {
		for(BoxFigure bf: b){
			contents.add(bf);
		}
		lws.setContents(contents);
	}

	/**
	 * This method initializes canvas
	 * 
	 */
	private void createCanvas() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		canvas = new Canvas(top, SWT.NONE);
		canvas.setLayoutData(gridData);
		canvas.setBackground(new Color(null,255,255,255));
		canvas.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
				transmitKeyEvent(e);
				update();
			}
		});
		canvas
				.addMouseMoveListener(new org.eclipse.swt.events.MouseMoveListener() {
					public void mouseMove(org.eclipse.swt.events.MouseEvent e) {
						try {
							Thread.sleep(100);
							update();
						} catch (Exception ee) {
							ee.printStackTrace();
						}
					}
				});
	}
}
