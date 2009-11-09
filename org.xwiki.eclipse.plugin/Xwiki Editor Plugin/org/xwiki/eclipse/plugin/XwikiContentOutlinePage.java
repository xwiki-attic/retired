package org.xwiki.eclipse.plugin;

/**
 * @author venkatesh
 *
 */

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class XwikiContentOutlinePage extends ContentOutlinePage {

	protected static class Title {
		private int type;
		private Position position;
		private IDocument document;
		public Title(int type,Position position,IDocument document)
		{
			this.document = document;
			this.type = type;
			this.position = position;
		}
		public String getStart(int required)
		{
			if(required<7 && required>0)
				return TYPE_STRING[required-1]; 
			return TYPE_STRING[0];
		}
		public Position nextTitle(int required_type)
		{
			try {
				int offset = document.get(position.getOffset()+1,document.getLength()-position.getOffset()-1).indexOf("\n"+TYPE_STRING[required_type-1]);
				if(offset>-1)
					return new Position(position.getOffset()+offset+1);
				if(offset == -1)
					return new Position(position.getOffset());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return null;
		}
		public Position endTitle()
		{
			for(int i=this.type;i>0;i--)
			{
				Position temp = nextTitle(i);
				if(temp.getOffset()!=this.position.getOffset())
					return temp;
			}
			return new Position(document.getLength());
		}
		public int getLength()
		{
			try {
				return document.getLineInformationOfOffset(position.getOffset()).getLength();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return 0;
		}
		public String toString()
		{
			try {
				return "Title "+type+": "+document.get(position.getOffset()+TYPE_STRING[type-1].length(),getLength()-TYPE_STRING[type-1].length())+"...";
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return null;
		}
		public Position nextTitle()
		{
			return nextTitle(this.type);
		}

		public String getContent()
		{
			Position temp = endTitle();
			if(temp==null)
				return "";
			try {
				return document.get(this.position.getOffset(), temp.getOffset()-this.position.getOffset());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return "";
		}
		public int find(String str)
		{
			return getContent().indexOf(str);
		}
		public List recursive_find(int required_type, List taken_content)
		{
			try {
				int initial_line = document.getLineOfOffset(position.getOffset());
				int final_line = document.getLineOfOffset(nextTitle().getOffset());
				if(final_line==initial_line)
					final_line = document.getNumberOfLines();
				List content= new ArrayList(final_line-initial_line);
				int last_index=0;
				int next_offset_length = 0;
				while(true)
				{
					try
					{
						int offset = getContent().indexOf("\n"+TYPE_STRING[required_type-1], last_index);
						if(offset > -1)
						{	
							offset++;
							last_index += offset;
							next_offset_length = getContent().indexOf("\n"+TYPE_STRING[required_type-1], last_index);
							if(next_offset_length == -1)
								next_offset_length = document.getLength() - position.getOffset()+last_index;
							int i=0;
							boolean found=false;
							if(taken_content!=null)
							{
								//System.out.println("intermeditate..."+i+" "+taken_content.size()+" "+(i<taken_content.size()));
								while(i<taken_content.size())
								{
									//System.out.println("inside... "+((Title)taken_content.toArray()[i]).position.getOffset()+" "+((Title)taken_content.toArray()[i]).position.getLength()+" "+(position.getOffset()+last_index)+" "+(((Title)taken_content.toArray()[i]).position.getOffset() <= (position.getOffset()+last_index) && (((Title)taken_content.toArray()[i]).position.getOffset() + ((Title)taken_content.toArray()[i]).position.getLength()) >= (position.getOffset()+last_index)));
									if(((Title)taken_content.toArray()[i]).position.getOffset() <= (position.getOffset()+last_index) && (((Title)taken_content.toArray()[i]).position.getOffset() + ((Title)taken_content.toArray()[i]).position.getLength()) >= (position.getOffset()+last_index))
									{
										i++;
										found=true;
										continue;
									}
									i++;
								}
							}
							if(!found)
							{
							//System.out.println("Adding data to content...");
							content.add(new Title(required_type,new Position(position.getOffset()+last_index,next_offset_length),document));
							}
						}
						else
							return content;
					}
					catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				}

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return null;
		}
		public Object[] getChildren()
		{
			List content = null;
			//System.out.println("-----------ignore---------------");
			for(int i=this.type;i<TYPE_STRING.length;i++)
			{
				content = recursive_find(i+1,content);
				if(content!=null)	//at least something found...
					break;
			}
			if(content==null)
				return null;
			//System.out.println("###########dontignore############");
			content.clear();
			for(int i=this.type;i<TYPE_STRING.length;i++)
				content.addAll(recursive_find(i+1,content));
			if(content!=null)
			{
				//System.out.println("outside "+this.toString()+" "+content.size()+" "+content.toString());
				return content.toArray();
			}
			return null;
		}

		public int getType()
		{
			return type;
		}

		public Title getParent()
		{
			if(type>1)
			{
				try {
					int offset = position.getOffset();
					for(int i=1;i<6;i++)
					{
						offset = document.get(0,position.getOffset()).lastIndexOf("\n"+TYPE_STRING[this.type-i-1]);
						if(offset > -1)
							break;
					}
					if(offset > -1)
						return new Title(type-1,new Position(offset),document);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

	}

	protected class ContentProvider implements ITreeContentProvider {

		protected final static String xwiki_segment = "__xwiki_content"; //$NON-NLS-1$
		protected IPositionUpdater positionupdater = new DefaultPositionUpdater(xwiki_segment);
		protected List content= new ArrayList(10);

		protected void parse(IDocument document) {

			int lines= document.getNumberOfLines();
			for (int line= 0; line < lines; line ++) {
				try {
					int offset= document.getLineOffset(line);
					int length= document.getLineLength(line);
					//TODO make this bold on the outline if possible, add context info, like, on hover, should show the whole title.
					if(document.get(offset,length).startsWith(TYPE_STRING[0]))
						content.add(new Title(1,new Position(offset),document));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}

		/*
		 * @see IContentProvider#inputChanged(Viewer, Object, Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (oldInput != null) {
				IDocument document= documentprovider.getDocument(oldInput);
				if (document != null) {
					try {
						document.removePositionCategory(xwiki_segment);
					} catch (BadPositionCategoryException x) {
					}
					document.removePositionUpdater(positionupdater);
				}
			}

			content.clear();

			if (newInput != null) {
				IDocument document= documentprovider.getDocument(newInput);
				if (document != null) {
					document.addPositionCategory(xwiki_segment);
					document.addPositionUpdater(positionupdater);
					parse(document);
				}
			}
		}

		/*
		 * @see IContentProvider#dispose
		 */
		public void dispose() {
			if (content != null) {
				content.clear();
				content= null;
			}
		}

		/*
		 * @see IContentProvider#isDeleted(Object)
		 */
		public boolean isDeleted(Object element) {
			return false;
		}

		/*
		 * @see IStructuredContentProvider#getElements(Object)
		 */
		public Object[] getElements(Object element) {
			return content.toArray();
		}

		/*
		 * @see ITreeContentProvider#hasChildren(Object)
		 */
		public boolean hasChildren(Object element) {
			if(element instanceof Title)
			{
				//TODO is this good enough??
				if(getChildren(element).length > 0)
					return true;
			}
			return false;
		}

		/*
		 * @see ITreeContentProvider#getParent(Object)
		 */
		public Object getParent(Object element) {
			/*	if (element instanceof Segment)
				return input;*/
			if(element instanceof Title)
			{
				if(((Title) element).getType()>1)
					return ((Title) element).getParent();
			}
			return null;			
		}

		/*
		 * @see ITreeContentProvider#getChildren(Object)
		 */
		public Object[] getChildren(Object element) {
			if(element instanceof Title)
				return ((Title) element).getChildren();
			return null;
		}
	}

	protected Object input;
	protected IDocumentProvider documentprovider;
	protected ITextEditor texteditor;
	protected static final String[] TYPE_STRING={"1 ","1.1 ","1.1.1 ","1.1.1.1 ","1.1.1.1.1 ","1.1.1.1.1.1 "};

	public XwikiContentOutlinePage(IDocumentProvider provider, ITextEditor editor) {
		super();
		documentprovider= provider;
		texteditor= editor;
	}

	/* (non-Javadoc)
	 * Method declared on ContentOutlinePage
	 */
	public void createControl(Composite parent) {

		super.createControl(parent);

		TreeViewer viewer= getTreeViewer();
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.addSelectionChangedListener(this);

		if (input != null)
			viewer.setInput(input);
	}

	/* (non-Javadoc)
	 * Method declared on ContentOutlinePage
	 */
	public void selectionChanged(SelectionChangedEvent event) {

		super.selectionChanged(event);
		ISelection selection= event.getSelection();
		if (selection.isEmpty())
			texteditor.resetHighlightRange();
		else {
			Title title= (Title) ((IStructuredSelection) selection).getFirstElement();
			int start= title.position.getOffset();
			int length= title.getLength();
			try {
				texteditor.setHighlightRange(start, length, true);
			} catch (IllegalArgumentException x) {
				texteditor.resetHighlightRange();
			}
		}
	}

	public void setInput(Object input) {
		this.input= input;
		update();
	}

	public void update() {
		TreeViewer viewer= getTreeViewer();
		if (viewer != null) {
			Control control= viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);
				viewer.setInput(input);
				viewer.expandAll();
				control.setRedraw(true);
			}
		}
	}
}