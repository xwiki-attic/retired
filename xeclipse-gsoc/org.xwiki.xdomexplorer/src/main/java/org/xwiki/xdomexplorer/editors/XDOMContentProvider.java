package org.xwiki.xdomexplorer.editors;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.XDOM;

public class XDOMContentProvider implements ITreeContentProvider
{
    private static final Object[] NO_OBJECTS = new Object[0];

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
        System.out.format("Children for %s\n", parentElement.getClass());
        if (parentElement instanceof Block) {
            Block block = (Block) parentElement;

            return block.getChildren().toArray();
        }

        return NO_OBJECTS;
    }

    public Object getParent(Object element)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasChildren(Object element)
    {
        return getChildren(element).length > 0;
    }

    public Object[] getElements(Object inputElement)
    {
        if (inputElement instanceof XDOM) {
            XDOM xdom = (XDOM) inputElement;

            return xdom.getChildren().toArray();
        }

        return NO_OBJECTS;
    }

}
