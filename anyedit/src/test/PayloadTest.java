/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
package test;

import junit.framework.TestCase;
import network.Payload;

public class PayloadTest extends TestCase {
private Payload c;
	public static void main(String[] args) {
		junit.textui.TestRunner.run(PayloadTest.class);
	}

	public PayloadTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		c = new Payload("Test","Command");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.roundtable.network.Command.Command(String, Object)'
	 */
	public void testCommandStringObject() {
		c = new Payload("Test","Command");
		assertEquals("Target: Test Command: Command", c.toString());
	}

	/*
	 * Test method for 'org.roundtable.network.Command.Command()'
	 */
	public void testCommand() {
		Payload c2 = new Payload();
		assertEquals("Target: NullTarget Command: NullCommand", c2.toString());
	}

	/*
	 * Test method for 'org.roundtable.network.Command.getTarget()'
	 */
	public void testGetTarget() {
		assertEquals("Test",c.getTarget());
	}

	/*
	 * Test method for 'org.roundtable.network.Command.getCommand()'
	 */
	public void testGetCommand() {
		assertEquals("Command",c.getCommand());
	}

	/*
	 * Test method for 'org.roundtable.network.Command.toString()'
	 */
	public void testToString() {
		assertEquals("Target: Test Command: Command", c.toString());
	}

}
