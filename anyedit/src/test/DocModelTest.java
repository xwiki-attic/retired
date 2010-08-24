/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
package test;

import java.util.ArrayList;

import junit.framework.TestCase;
import model.DocModel;
import network.Payload;

public class DocModelTest extends TestCase {
private DocModel doc;
	/*
	 * Test method for 'model.DocModel.DocModel()'
	 */
	public final void testDocModel() {
		doc = DocModel.getInstance();
		assertEquals("", doc.getText());

	}

	/*
	 * Test method for 'model.DocModel.doCommand(Payload)'
	 */
	public final void testDoCommand() {
		doc = DocModel.getInstance();
		ArrayList a = new ArrayList();
		a.add("add");
		a.add(0);
		a.add("a");
		Payload p = new Payload("text", a);
		doc.doCommand(p);
		assertEquals("a", doc.getText());
		a.clear();
		a.add("remove");
		a.add(0);
		a.add(0);
		a.add(99);
		doc.doCommand(p);//should not do anything bad
	}

	/*
	 * Test method for 'model.DocModel.getText()'
	 */
	public final void testGetText() {
		doc = DocModel.getInstance();
		// TODO Auto-generated method stub
		ArrayList a = new ArrayList();
		a.add("add");
		a.add(0);
		a.add("q");
		a.add("w");
		a.add("e");
		a.add("r");
		a.add("t");
		a.add("y");
		Payload p = new Payload("text", a);
		doc.doCommand(p);
		assertEquals("qwerty",doc.getText());
		a.clear();
		a.add("remove");
		a.add(0);
		a.add(0);
		a.add(10);
		p = new Payload("text", a);
		doc.doCommand(p);
		assertEquals("",doc.getText());
		doc.doCommand(p);//should not do anything bad
	}

}
