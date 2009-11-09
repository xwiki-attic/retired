/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
package test;
import junit.framework.TestCase;
import network.Client;
import network.Payload;
import network.Server;

public class NetworkTest extends TestCase {
	Client client;
	Client client2;
	Client client3;
	public static void main(String[] args) {
		junit.textui.TestRunner.run(NetworkTest.class);
	}

	public NetworkTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		new Thread(new Server()).start();
		client = new Client();
		client2 = new Client();
		client3 = new Client();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.anyedit.network.Client.transmitCommand(Command)'
	 * and 'org.anyedit.network.Client.recieveCommand()'
	 */
	public void testSendAndrecieveCommand() {
		Payload c1 = new Payload("1", "2");
		Payload c2 = new Payload("3", "4");
		client.transmitCommand(c1);
		try{
		Thread.sleep(1);
		}catch (Exception e){
			e.printStackTrace();
		}
		client2.transmitCommand(c2);
		assertEquals(c1.toString(),client.recieveCommand().toString());
		assertEquals(c2.toString(),client.recieveCommand().toString());
		assertEquals(c1.toString(),client2.recieveCommand().toString());
		assertEquals(c2.toString(),client2.recieveCommand().toString());
		assertEquals(c1.toString(),client3.recieveCommand().toString());
		assertEquals(c2.toString(),client3.recieveCommand().toString());
		
	}

}
