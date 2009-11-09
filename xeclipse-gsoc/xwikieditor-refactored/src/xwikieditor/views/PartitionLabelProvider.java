package xwikieditor.views;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.viewers.LabelProvider;

public class PartitionLabelProvider extends LabelProvider
{

    @Override
    public String getText(Object element)
    {
        if (element instanceof ITypedRegion) {
            ITypedRegion partition = (ITypedRegion) element;
            return String.format("(%d, %d) %s", partition.getOffset(), partition.getLength(), partition.getType());
        }

        return super.getText(element);
    }

}
