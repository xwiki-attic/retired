package org.xwiki.eclipse.ui.editors.parser.model;

import java.util.ArrayList;
import java.util.List;

import org.xwiki.eclipse.ui.editors.PageEditor;

public class VeloObjectModel {
	
	private PageEditor pageEditor;

	public VeloObjectModel(PageEditor pageEditor) {
		this.pageEditor = pageEditor;	
	}
	
	public List getVariables(int lineNumber) {
		Node node = pageEditor.getLastRootNode();
		if (node != null) {
			TreeNodeVariableVisitor visitor =
											new TreeNodeVariableVisitor(lineNumber);
			node.accept(visitor);
			List variables = visitor.getVariables();
			/*if (isLineWithinLoop(pageEditor.getCursorLine())) {
				Preferences prefs =
							VelocityPlugin.getDefault().getPluginPreferences();
				String countName = "$" + prefs.getString(
								  IPreferencesConstants.VELOCITY_COUNTER_NAME);
				variables.add(countName);
			}*/
			return variables;
		}
		return new ArrayList();
	}
	
	public boolean isLineWithinLoop(int lineNumber) {
		Node node = pageEditor.getRootNode();
		if (node != null) {
			if (lineNumber > 0) {
				// Use visitor pattern to find node which contains given line
				TreeNodeLineVisitor visitor = new TreeNodeLineVisitor(lineNumber);
				node.accept(visitor);
				node = visitor.getNode();
				while (node != null) {
					if (node instanceof DirectiveNode) {
						DirectiveNode directive = (DirectiveNode)node;
						if (directive.getType() == DirectiveNode.TYPE_FOREACH) {
							return true;
						}
					}
					node = (Node)node.getParent();
				}
			}
		}
		return false;
	}
	
	private class TreeNodeVariableVisitor implements NodeVisitor {
		private int fLine;
		private List fVariables;

		public TreeNodeVariableVisitor(int aLine) {
			fLine = aLine;
			fVariables = new ArrayList();
		}

		public boolean visitNode(Node node) {
			if (node instanceof DirectiveNode) {
				int type = ((DirectiveNode)node).getType();
				if (type == DirectiveNode.TYPE_FOREACH ||
												  type == DirectiveNode.TYPE_SET) {
					String variable = ((DirectiveNode)node).getId();
					if (!fVariables.contains(variable)) {
						fVariables.add(variable);
					}
				} else if (type == DirectiveNode.TYPE_MACRO) {
					if (fLine >= node.getStartLine() &&
												 fLine <= node.getEndLine()) {
						List parameters = ((DirectiveNode)node).getParameters();
						if (parameters != null) {
							fVariables.addAll(parameters);
						}
					}
				}
			}
			return true; 
		}

		public List getVariables() {
			return fVariables;
		}
	}

	private class TreeNodeLineVisitor implements NodeVisitor {
		private int fLine;
		private Node node;

		public TreeNodeLineVisitor(int lineNumber) {
			fLine = lineNumber;
			node = null;
		}

		public boolean visitNode(Node aNode) {
			boolean more;
			if (fLine >= aNode.getStartLine() && fLine <= aNode.getEndLine()) {
				node = aNode;
				more = false;
			} else {
				more = true;
			}
			return more; 
		}

		public Node getNode() {
			return node;
		}
	}

}
