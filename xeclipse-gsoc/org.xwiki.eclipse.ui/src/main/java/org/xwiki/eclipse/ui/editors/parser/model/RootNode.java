package org.xwiki.eclipse.ui.editors.parser.model;

import java.util.Iterator;
import java.util.Vector;

public class RootNode extends AbstractNode implements Block{
	
	private String directiveName;
	private Vector directives = new Vector();
	
	public RootNode(String aName) {
		super(null, -1, -1);
		directiveName = aName;
	}

	public void addDirective(DirectiveNode aDirective) {
		directives.add(aDirective);
	}
	
	public String getName() {
		return directiveName;
	}

	public boolean hasChildren() {
		return !directives.isEmpty();
	}

	public Object[] getChildren() {
	    return directives.toArray();
	}

	public boolean accept(NodeVisitor visitor) {
		boolean more = true;
		
		// Visit all directives of this template
		Iterator iter = directives.iterator();
		while (more && iter.hasNext()) {
			more = ((Node)iter.next()).accept(visitor);
		}

		// Finally visit this template
		if (more) {
			more = visitor.visitNode(this);
		}
		return more;
	}

	public String getUniqueID() {
		return getName();
	}

	public String toString() {
	    return getUniqueID() + " [" + getStartLine() + ":" + getEndLine() +
	    		"] with directive(s) " + directives;
	}

}
