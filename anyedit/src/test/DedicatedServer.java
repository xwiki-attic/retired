package test;

import network.Server;

public class DedicatedServer {

	public DedicatedServer() {

	}

	public static void main(String[] args) {
		try {
			new Thread(new Server()).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
