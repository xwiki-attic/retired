package org.xwiki.eclipse.ui.editors.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.xwiki.eclipse.ui.editors.PageEditor;
import org.xwiki.eclipse.ui.editors.parser.model.Node;
import org.xwiki.eclipse.ui.editors.parser.model.RootNode;

public class VeloReconcilingStrategy implements IReconcilingStrategy {
	
	private PageEditor pageEditor = null;
	
	private ArrayList<String> variables = null;
	
	Annotation[] previousAnnotations;
	
	ISourceViewer sourceViewer;
	
	private RootNode rootNode;
	
    private RootNode lastRootNode;
    
    private boolean errorFree;
    
    private String errorMsg;
	
	public VeloReconcilingStrategy(PageEditor pageEditor){		
		this.pageEditor = pageEditor;
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		parseVelocity();
		
	}

	public void reconcile(IRegion partition) {
		parseVelocity();
		
	}

	public void setDocument(IDocument document) {
		parseVelocity();
		
	}
	
	public void parseVelocity(){
		
		Reader reader = new StringReader(pageEditor.getDocument().get());
		RootNode rootNode = null;
		String name = pageEditor.getTitle();
		try {
			RuntimeInstance runtime = new VelocityParser();
			SimpleNode root = runtime.parse(reader, name);

			VeloParserVisitor visitor = new VeloParserVisitor(name);
			root.jjtAccept(visitor, null);
			rootNode = visitor.getTemplate();
			errorMsg = "";
			errorFree=true;
		} catch (ParseException e) {
		    
			if (e.getMessage() != null) {
				errorMsg = e.toString();
				errorFree=false;
			} else {
				errorMsg = "";
			}
		} catch (Exception e) {
			errorMsg = "";
        } finally {
        	try {
				reader.close();        
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
		}

		// Replace saved template with the new parsed one
		synchronized (this) {
        	if (rootNode != null) {
				this.rootNode = rootNode;

				// Save last successful parse tree
				this.lastRootNode = rootNode;
        	} else {
        		this.rootNode = null;
        	}
		}
	}
	
	public void setISourceViewer(ISourceViewer sourceViewer){
		this.sourceViewer = sourceViewer;
	}
	
	public ArrayList<String> getVariableList(){
		return variables;
	}
	
	/**
	 * Returns root elements of current parse tree.
	 */    
    public Object[] getRootElements() {
		return (rootNode != null ? rootNode.getChildren() :
									 Node.CHILDREN);
    }

	/**
	 * Returns root node of current parse tree.
	 */    
    public Node getRootNode() {
        return rootNode;
    }

	/**
	 * Returns last successful parse tree.
	 */    
    public Node getLastRootNode() {
        return lastRootNode;
    }

	public boolean isErrorFree() {
		return errorFree;
	}

    public String getErrorMsg()
    {
        return errorMsg.split("\n")[0];
    }
	
}
