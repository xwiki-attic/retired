package org.xwiki.eclipse.ui.editors.parser.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class DirectiveNode extends AbstractNode implements Block {

	public static final int TYPE_SET = 0;
	public static final int TYPE_IF = 1;
	public static final int TYPE_ELSE = 2;
	public static final int TYPE_ELSEIF = 3;
	public static final int TYPE_END = 4;
	public static final int TYPE_FOREACH = 5;
	public static final int TYPE_INCLUDE = 6;
	public static final int TYPE_PARSE = 7;
	public static final int TYPE_MACRO = 8;
	public static final int TYPE_STOP = 9;
	public static final int TYPE_MACRO_CALL = 10;
	public static final int TYPE_USER_DIRECTIVE = 11;

	private int directiveType;
	private String directiveName;

	private ArrayList parameters;

	public static final String DIRECTIVES[] = { "#set", "#if", "#else",
			"#elseif", "#end", "#foreach", "#include", "#parse", "#macro",
			"#stop" };
	private String id;
	protected Vector directives = new Vector();
	
	public DirectiveNode(int type, String name, String id,
					  Node parentNode, int startLine, int endLine) {
		super(parentNode, startLine, endLine);
		directiveName = name;
		directiveType = type;
		this.id = id;

	}

	public int getType() {
		return directiveType;
	}

	public static int getType(String name) {
		for (int i = 0; i < DIRECTIVES.length; i++) {
			if (DirectiveNode.DIRECTIVES[i].equals(name)) {
				return i;
			}
		}
		// if (VelocityEditorEnvironment.getParser().isUserDirective(aName)) {
		// return TYPE_USER_DIRECTIVE;
		// }
		return TYPE_MACRO_CALL;
	}

	public String getId() {
		return id;
	}

	public void addParameter(String parameter) {
		if (parameters == null) {
			parameters = new ArrayList();
		}
		parameters.add(parameter);
	}

	public ArrayList getParameters() {
		return parameters;
	}

	public void addDirective(DirectiveNode directive) {
		directives.add(directive);
	}

	public String getName() {
		return (directiveType < TYPE_MACRO_CALL ? DIRECTIVES[directiveType]
				+ (directiveType != TYPE_ELSE ? " (" + directiveName + ")" : "")
				: "#" + directiveName);
	}

	public boolean hasChildren() {
		return !directives.isEmpty();
	}

	public Object[] getChildren() {
		return directives.toArray();
	}

	public boolean accept(NodeVisitor visitor) {
		boolean more = true;

		// Visit all embedded directives of this directive
		Iterator iter = directives.iterator();
		while (more && iter.hasNext()) {
			more = ((Node) iter.next()).accept(visitor);
		}

		// Finally visit this directive
		if (more) {
			more = visitor.visitNode(this);
		}
		return more;
	}

	public String getUniqueID() {
		return getName() + ":" + getStartLine();
	}

	public String toString() {
		return getUniqueID() + ":" + getEndLine() + " with directive(s) "
				+ directives;
	}
}
