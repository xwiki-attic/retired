/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
 */
package model;

import java.util.ArrayList;

import network.Payload;

import org.eclipse.swt.SWT;
import org.jdom.Element;
import org.jdom.Text;

public class DocModel {

	private Element root;

	private static DocModel uniqueInstance;

	private static boolean updated = true;

	public static DocModel getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DocModel();
		}
		return uniqueInstance;
	}

	public boolean isUpdated() {
		boolean temp = updated;
		updated = false;
		return temp;
	}

	private DocModel() {
		root = new Element("ROOT");
	}

	public synchronized void doCommand(Payload p) {
		updated = true;
		System.out.println("Command being done!!!");// TODO DEBUG
		String target = p.getTarget();
		Object cmd = p.getCommand();
		if (target.equals("text")) {
			doTextCommand((ArrayList) cmd);
		} else if (target.equals("navigation")) {
			doNavigationCommand((Integer) cmd);
		}
	}

	private void doNavigationCommand(Integer cmd) {
		switch (cmd) {
		case SWT.ARROW_DOWN:
		case SWT.ARROW_LEFT:
			Cursor.getInstance().setPosition(Cursor.getPosition() - 1);
		case SWT.ARROW_RIGHT:
		case SWT.ARROW_UP:
			Cursor.getInstance().setPosition(Cursor.getPosition() + 1);
		}
	}

	private void doTextCommand(ArrayList cmd) {
		String s0 = (String) cmd.get(0);
		int s1 = (Integer) cmd.get(1);
		if (s0.equals("add")) {
			for (int i = 2; i < cmd.size(); i++) {
				System.out.println("Adding text");
				addText(s1 - 2 + i, (String) cmd.get(i));
			}
		} else if (s0.equals("remove")) {
			int s3 = (Integer) cmd.get(3);
			for (int i = 0; i < s3; i++) {
				System.out.println("Removing text");
				removeText(s1);
			}
		}
	}

	private void addText(int index, String s) {
		if (index <= 0) {
			root.addContent(0, new Text(s));
		} else if (index > root.getContentSize() - 1) {
			//add spaces up to the place where the user wants to put the character
			for(int i = 0; i <= (index - root.getContentSize() - 1); i++){
			root.addContent(" ");
			Cursor.getInstance().setPosition(Cursor.getPosition() + 1);
			}
			root.addContent(index, new Text(s));
		} else {
			root.addContent(index, new Text(s));
		}
	}

	private void removeText(int index) {
		if (root.getContentSize() > 0) {
			if (index <= 0) {
				root.removeContent(0);
			} else if (index > root.getContentSize()) {
				root.removeContent(root.getContentSize() - 1);
			} else if (index < root.getContentSize()) {
				root.removeContent(index);
			}
		}
		Cursor.getInstance().setPosition(Cursor.getPosition() - 1);
	}

	public String getText() {
		return root.getText();
	}

	public Element getRoot() {
		return root;
	}
}
