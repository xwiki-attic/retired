package network;
/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
import java.io.ObjectOutputStream;
import java.util.List;

public class ServerTransmitThread implements Runnable {
	ObjectOutputStream oos;

	Payload command;

	List commandQueue;

	public ServerTransmitThread(ObjectOutputStream oos,
			List<Payload> commandQueue) {
		this.oos = oos;
		this.commandQueue = commandQueue;
	}

	public void run() {
		//System.out.println("Server Transmit Thread Started on: " + oos);// debug
		while (true) {
			try {
				if (commandQueue.isEmpty() == false) {
					//System.out.println("Server Transmitting Command");// debug
					command = (Payload) commandQueue.remove(0);
					oos.writeObject(command);
					oos.flush();
				}
				Thread.sleep(10);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
}
