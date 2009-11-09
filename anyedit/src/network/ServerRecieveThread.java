package network;
/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
import java.io.ObjectInputStream;

public class ServerRecieveThread implements Runnable {
	private ObjectInputStream ois = null;
	
	private Server server = null;
	
	public ServerRecieveThread(ObjectInputStream ois, Server server) {
		this.server = server;
		this.ois = ois;
	}
	
	public void run() {
		//System.out.println("Server Receive Thread Started on: " + ois);// debug
		
		while (true) {
			try {
				Payload clientCommand = (Payload) ois.readObject();
				server.broadcast(clientCommand);
			} catch (Exception e) {
				// timeout, try again
			//	System.out.println("Server Waiting for Command");// debug
			}
		}
	}
}
