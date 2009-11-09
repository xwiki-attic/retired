package org.xwiki.eclipse.ui.editors.parser;

import java.util.Stack;

import org.apache.velocity.runtime.parser.node.ASTAddNode;
import org.apache.velocity.runtime.parser.node.ASTAndNode;
import org.apache.velocity.runtime.parser.node.ASTAssignment;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTComment;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ASTDivNode;
import org.apache.velocity.runtime.parser.node.ASTEQNode;
import org.apache.velocity.runtime.parser.node.ASTElseIfStatement;
import org.apache.velocity.runtime.parser.node.ASTElseStatement;
import org.apache.velocity.runtime.parser.node.ASTEscape;
import org.apache.velocity.runtime.parser.node.ASTEscapedDirective;
import org.apache.velocity.runtime.parser.node.ASTExpression;
import org.apache.velocity.runtime.parser.node.ASTFalse;
import org.apache.velocity.runtime.parser.node.ASTFloatingPointLiteral;
import org.apache.velocity.runtime.parser.node.ASTGENode;
import org.apache.velocity.runtime.parser.node.ASTGTNode;
import org.apache.velocity.runtime.parser.node.ASTIdentifier;
import org.apache.velocity.runtime.parser.node.ASTIfStatement;
import org.apache.velocity.runtime.parser.node.ASTIntegerLiteral;
import org.apache.velocity.runtime.parser.node.ASTIntegerRange;
import org.apache.velocity.runtime.parser.node.ASTLENode;
import org.apache.velocity.runtime.parser.node.ASTLTNode;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.ASTMethod;
import org.apache.velocity.runtime.parser.node.ASTModNode;
import org.apache.velocity.runtime.parser.node.ASTMulNode;
import org.apache.velocity.runtime.parser.node.ASTNENode;
import org.apache.velocity.runtime.parser.node.ASTNotNode;
import org.apache.velocity.runtime.parser.node.ASTObjectArray;
import org.apache.velocity.runtime.parser.node.ASTOrNode;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTSetDirective;
import org.apache.velocity.runtime.parser.node.ASTStop;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.ASTSubtractNode;
import org.apache.velocity.runtime.parser.node.ASTText;
import org.apache.velocity.runtime.parser.node.ASTTrue;
import org.apache.velocity.runtime.parser.node.ASTWord;
import org.apache.velocity.runtime.parser.node.ASTprocess;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import org.xwiki.eclipse.ui.editors.parser.model.Block;
import org.xwiki.eclipse.ui.editors.parser.model.DirectiveNode;
import org.xwiki.eclipse.ui.editors.parser.model.RootNode;

public class VeloParserVisitor implements ParserVisitor {

	private String name;
	private RootNode rootNode;
	private Stack<Block> blocks = new Stack<Block>();
	private Block currentBlock;

	public VeloParserVisitor(String name) {
		this.name = name;
	}

	public RootNode getTemplate() {
		return rootNode;
	}

	private Object visitBlockDirective(Node node, Object data,
			DirectiveNode directive, boolean addToParentBlock) {
		
		if (addToParentBlock && currentBlock instanceof DirectiveNode) {
			Block parent = (Block) ((DirectiveNode) currentBlock).getParent();
			parent.addDirective(directive);
		} else {
			currentBlock.addDirective(directive);
		}
		blocks.push(currentBlock);
		currentBlock = directive;
		data = node.childrenAccept(this, data);
		currentBlock = (Block) blocks.pop();
		return data;
	}

	public Object visit(SimpleNode arg0, Object arg1) {
		return arg0.childrenAccept(this, arg1);
	}

	public Object visit(ASTprocess arg0, Object arg1) {
		rootNode = new RootNode(name);
		currentBlock = rootNode;
		return arg0.childrenAccept(this, arg1);
	}

	public Object visit(ASTEscapedDirective arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTEscape arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTComment arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTFloatingPointLiteral arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTIntegerLiteral arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTStringLiteral arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTIdentifier arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTWord arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTDirective arg0, Object arg1) {
		String name = arg0.getDirectiveName();
		int type = DirectiveNode.getType('#' + name);
		DirectiveNode directive;
		String id = null;
		switch (type) {
			case DirectiveNode.TYPE_MACRO :
				if (arg0.jjtGetNumChildren() > 0 &&
						arg0.jjtGetChild(0) instanceof ASTWord) {
					name = arg0.jjtGetChild(0).literal();
				} else {
					name = "";
				}
				id = name;
				break;

			case DirectiveNode.TYPE_FOREACH :
				if (arg0.jjtGetNumChildren() > 2) {
					name = arg0.jjtGetChild(2).literal();
				} else {
					name = "";
				}
				if (arg0.jjtGetNumChildren() > 0 &&
						arg0.jjtGetChild(0) instanceof ASTReference) {
					id = arg0.jjtGetChild(0).literal();
				} else {
					id = "";
				}
				break;

			case DirectiveNode.TYPE_INCLUDE :
			case DirectiveNode.TYPE_PARSE :
				if (arg0.jjtGetNumChildren() > 0) {
					name = arg0.jjtGetChild(0).literal();
				} else {
					name = "";
				}
				id = "";
				break;

			case DirectiveNode.TYPE_MACRO_CALL :
				id = "";
				break;
			case DirectiveNode.TYPE_USER_DIRECTIVE :
				id = "";
				break;
		}

		// If valid directive then visit embedded nodes too 
		if (id != null) {
			directive = new DirectiveNode(type, name, id,
									  (org.xwiki.eclipse.ui.editors.parser.model.Node)currentBlock,
									  arg0.getFirstToken().beginLine,
									  arg0.getLastToken().endLine);
			arg1 = visitBlockDirective(arg0, arg0, directive, false);

			// Add parameters of macro definition
			if (type == DirectiveNode.TYPE_MACRO && arg0.jjtGetNumChildren() > 1) {
				for (int i = 1; i < arg0.jjtGetNumChildren(); i++) {
					Node node = arg0.jjtGetChild(i);
					if (node instanceof ASTReference) {
						directive.addParameter(node.literal());
					} else {
						break;
					}
				}
			}
		}
		return arg1;
	}

	public Object visit(ASTBlock arg0, Object arg1) {
		return arg0.childrenAccept(this, arg1);
	}

	public Object visit(ASTMap arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTObjectArray arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTIntegerRange arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTMethod arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTReference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTTrue arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTFalse arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTText arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTIfStatement arg0, Object arg1) {
		int endLine = arg0.getLastToken().endLine;
		int numChildren = arg0.jjtGetNumChildren();
		for (int i = 1; i < numChildren; i++) {
			Node node = arg0.jjtGetChild(i);
			if (node instanceof ASTElseStatement ||
										  node instanceof ASTElseIfStatement) {
				endLine = node.getFirstToken().beginLine - 1;
				break;
			}
		}
		DirectiveNode directive = new DirectiveNode(DirectiveNode.TYPE_IF,
				arg0.jjtGetChild(0).literal(),
											"", (org.xwiki.eclipse.ui.editors.parser.model.Node)currentBlock,
											arg0.getFirstToken().beginLine,
											endLine);
		return visitBlockDirective(arg0, arg1, directive, false);
	}

	public Object visit(ASTElseStatement arg0, Object arg1) {
		DirectiveNode directive = new DirectiveNode(DirectiveNode.TYPE_ELSE,
				null, "", (org.xwiki.eclipse.ui.editors.parser.model.Node)currentBlock,
				arg0.getFirstToken().beginLine,
				arg0.getLastToken().next.endLine);
		return visitBlockDirective(arg0, arg1, directive, true);
	}

	public Object visit(ASTElseIfStatement arg0, Object arg1) {
		DirectiveNode directive = new DirectiveNode(DirectiveNode.TYPE_ELSEIF,
				arg0.jjtGetChild(0).literal(),
				"", (org.xwiki.eclipse.ui.editors.parser.model.Node)currentBlock,
				arg0.getFirstToken().beginLine,
				arg0.getLastToken().endLine);
		return visitBlockDirective(arg0, arg1, directive, true);
	}

	public Object visit(ASTSetDirective arg0, Object arg1) {
		String expr = arg0.jjtGetChild(0).literal();
		int pos = expr.indexOf('=');
		if (pos >= 0) {
			expr = expr.substring(0, pos).trim();
		}
		
		DirectiveNode directive = new DirectiveNode(DirectiveNode.TYPE_SET,expr, expr, (org.xwiki.eclipse.ui.editors.parser.model.Node)currentBlock,0,0);
		currentBlock.addDirective(directive);
		return null;
	}

	public Object visit(ASTStop arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTExpression arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTAssignment arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTOrNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTAndNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTEQNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTNENode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTLTNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTGTNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTLENode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTGENode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTAddNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTSubtractNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTMulNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTDivNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTModNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ASTNotNode arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
