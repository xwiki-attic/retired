package network;
/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
/**
 * The Server is the hub of communication between clients.
 * - Accepts connections from clients
 * - Starts threads to host clients
 * - Broadcasts serialized commands to threads hosting clients
 */
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.p2psockets.P2PNetwork;
import org.p2psockets.P2PServerSocket;

public class Server implements Runnable {
	private P2PServerSocket serverSocket;

	private List<ServerThread> threadList;

	public Server() throws Exception {
		construct("DefaultNetwork", "DefaultHostname", 64444);
	}

	public Server(String network, String hostname, int port) throws Exception {
		construct(network, hostname, port);
	}

	private void construct(String network, String hostname, int port)
			throws Exception {
		threadList = Collections.synchronizedList(new LinkedList());

		try {
			P2PNetwork.autoSignin(network);
			serverSocket = new P2PServerSocket(hostname, port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
			System.exit(-1);
		} catch (Exception e) {
			System.err.println("Could not make room: " + network);
			System.exit(1);
		}
	}
	
	public void run(){
		while (true) {
			try {
				Socket client = serverSocket.accept();
				//System.out.println("Accepted Connection from: " + client);//debug
				ServerThread st = new ServerThread(client, this);
				threadList.add(st);
				new Thread(st).start();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	public synchronized void broadcast(Payload c) {
		// System.out.println("Broadcasting");// debug
		if (c.getTarget().equalsIgnoreCase("server")) {
			// do server action
		} else {
			for (ServerThread st : threadList) {
				st.addCommand(c);
			}
		}
	}
}
