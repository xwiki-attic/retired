/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
 */
package controller;

import model.DocModel;
import network.Client;
import network.Payload;

/**
 * @author kevin
 * 
 */
public class ControllerRecieveThread implements Runnable {
	private DocModel dm;

	/**
	 * 
	 */
	public ControllerRecieveThread() {
		super();
		this.dm = DocModel.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Payload p = Client.getInstance().recieveCommand();
				dm.doCommand(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
