package network;
/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
/**
 * The ServerThread hosts a single client.
 * - Starts other threads to tend to clients' needs.
 */
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ServerThread implements Runnable {

	private Socket client;

	private Server server;

	private List<Payload> commandQueue;

	public ServerThread(Socket client, Server server) {
		this.client = client;
		this.server = server;
		commandQueue = Collections.synchronizedList(new LinkedList());
	}
	public void addCommand(Payload c){
		commandQueue.add(c);
	}
	public void run() {
		try {
			InputStream is = client.getInputStream();
			OutputStream os = client.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(is);
			ServerRecieveThread srt = new ServerRecieveThread(ois, server);
			ServerTransmitThread stt = new ServerTransmitThread(oos,
					commandQueue);
			new Thread(srt).start();
			new Thread(stt).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
