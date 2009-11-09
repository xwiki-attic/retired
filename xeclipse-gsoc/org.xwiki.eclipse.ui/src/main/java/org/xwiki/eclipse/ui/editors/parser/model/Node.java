package org.xwiki.eclipse.ui.editors.parser.model;


public interface Node {
	
	public static final Object[] CHILDREN = new Object[0];
	
	boolean hasChildren();
	
	Object[] getChildren();
	
	int getEndLine();
	
	String getName();
	
	Object getParent();
	
	int getStartLine();
	
	String getUniqueID();
	
	boolean accept(NodeVisitor nodeVisitor);
}
