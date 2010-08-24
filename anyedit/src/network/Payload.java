package network;

/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
/**
 * A Payload is a String and Object pair. The String is meant to be the name
 * of the target (server, username, etc). The Object is meant to be the command/payload for the target
 * plugin.
 */
import java.io.Serializable;

public class Payload implements Serializable {
	private String target;
	private static final long serialVersionUID = 1;
	private Object command;

	public Payload(String pluginName, Object pluginCommand) {
		this.target = pluginName;
		this.command = pluginCommand;
	}

	public Payload() {
		this.target = "NullTarget";
		this.command = "NullCommand";
	}

	public String getTarget() {
		return target;
	}

	public Object getCommand() {
		return command;
	}

	public String toString() {
		return new String("Target: " + target + " Command: " + command);
	}
}
