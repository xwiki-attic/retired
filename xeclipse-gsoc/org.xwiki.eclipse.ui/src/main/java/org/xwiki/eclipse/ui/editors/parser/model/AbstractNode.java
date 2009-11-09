package org.xwiki.eclipse.ui.editors.parser.model;

public abstract class AbstractNode implements Node {
	
	private Node parentNode;
	private int startLine;
	private int endLine;

	protected AbstractNode(Node parentNode, int startLine, int endLine) {
		this.parentNode = parentNode;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	public abstract String getName();

	public Object getParent() {
	    return parentNode;
	}
	
	public int getStartLine() {
	    return startLine;
	}

	public int getEndLine() {
	    return endLine;
	}
}
