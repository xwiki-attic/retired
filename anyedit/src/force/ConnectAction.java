package force;
/**
 * Copyright (c) Kevin Chiu
 * Licensed under LGPL
*/
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;


public class ConnectAction extends Action {

    private final IWorkbenchWindow window;
	ConnectAction(String text, IWorkbenchWindow window) {
        super(text);
        this.window = window;
        // The id is used to refer to the action in a menu or toolbar
        setId(ICommandIds.CMD_CONNECT);
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_CONNECT);
        setImageDescriptor(force.ForcePlugin.getImageDescriptor("/icons/sample3.gif"));
    }

    public void run() {
    	
    }
}