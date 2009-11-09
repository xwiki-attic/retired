package network;

/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.p2psockets.P2PNetwork;
import org.p2psockets.P2PSocket;

public class Client {
	private static Client uniqueInstance;

	private Socket server;

	private ObjectInputStream ois;

	private ObjectOutputStream oos;

	private static String host;
	

	public static Client getInstance() {
		if (uniqueInstance == null) {
			if (host == null) {
				uniqueInstance = new Client();
			} else if (host != Client.getInstance().getHost()) {
				uniqueInstance = new Client("FORCE", host, 69999);
			}
		}
		return uniqueInstance;
	}

	public static void setHost(String s){
		host = s;
		getInstance();//start up the client
	}
	public static String getHost(){
		return host;
	}

	public Client() {
		construct("DefaultNetwork", "DefaultHostname", 64444);
	}

	private Client(String network, String hostname, int port) {
		construct(network, hostname, port);
	}

	private void construct(String network, String hostname, int port) {
		try {
			P2PNetwork.autoSignin(network);
			server = new P2PSocket(hostname, port);
			this.oos = new ObjectOutputStream(server.getOutputStream());
			oos.flush();
			this.ois = new ObjectInputStream(server.getInputStream());
			// System.out.println("ObjectOutputStream: " + this.oos);// debug
			// System.out.println("ObjectInputSTream: " + this.ois);// debug
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host:" + hostname);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to:"
					+ hostname + ":" + port);
			System.exit(1);
		} catch (Exception e) {
			System.err.println("Could not join network:" + network);
			System.exit(1);
		}
	}

	public synchronized void sendPayload(String pluginName, Object pluginCommand) {
		Payload p = new Payload(pluginName, pluginCommand);
		transmitCommand(p);
	}

	public void transmitCommand(Payload command) {
		try {
			// System.out.println("client sending using: " + this.oos);// debug
			this.oos.writeObject(command);
			this.oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized Payload recieveCommand() {
		// System.out.println("client recieving using: " + this.ois);// debug
		Payload command = null;
		while (true) {
			try {
				command = (Payload) this.ois.readObject();
				return command;
			} catch (Exception e) {
				// timeout, try again
				// System.out.println("Client Waiting for Command");//debug
			}
		}
	}
}
