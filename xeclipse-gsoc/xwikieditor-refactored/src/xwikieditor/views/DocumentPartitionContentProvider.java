package xwikieditor.views;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DocumentPartitionContentProvider implements ITreeContentProvider
{
    private Object[] NO_OBJECTS = new Object[0];

    public void dispose()
    {
        // TODO Auto-generated method stub

    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // TODO Auto-generated method stub

    }

    public Object[] getChildren(Object parentElement)
    {
        return NO_OBJECTS;
    }

    public Object getParent(Object element)
    {
        return null;
    }

    public boolean hasChildren(Object element)
    {
        return false;
    }

    public Object[] getElements(Object inputElement)
    {
        if (inputElement instanceof IDocument) {
            IDocument document = (IDocument) inputElement;

            try {
                return document.computePartitioning(0, document.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        return NO_OBJECTS;
    }

}
