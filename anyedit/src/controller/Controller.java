package controller;

/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
 */

public class Controller {

	public Controller() {
		ControllerRecieveThread crt = new ControllerRecieveThread();
		new Thread(crt).start();
	}
}
