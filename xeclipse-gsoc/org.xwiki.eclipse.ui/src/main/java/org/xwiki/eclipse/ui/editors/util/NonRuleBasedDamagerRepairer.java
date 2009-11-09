package org.xwiki.eclipse.ui.editors.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.swt.custom.StyleRange;

public class NonRuleBasedDamagerRepairer implements IPresentationDamager, IPresentationRepairer
{
    protected IDocument fDocument;

    protected TextAttribute fDefaultTextAttribute;

    public NonRuleBasedDamagerRepairer(TextAttribute aDefaultTextAttribute)
    {
        fDefaultTextAttribute = aDefaultTextAttribute;
    }

    public void setDocument(IDocument aDocument)
    {
        fDocument = aDocument;
    }

    protected int endOfLineOf(int anOffset) throws BadLocationException
    {

        IRegion info = fDocument.getLineInformationOfOffset(anOffset);
        if (anOffset <= info.getOffset() + info.getLength())
            return info.getOffset() + info.getLength();

        int line = fDocument.getLineOfOffset(anOffset);
        try {
            info = fDocument.getLineInformation(line + 1);
            return info.getOffset() + info.getLength();
        } catch (BadLocationException x) {
            return fDocument.getLength();
        }
    }

    public IRegion getDamageRegion(ITypedRegion aPartition, DocumentEvent anEvent, boolean aDocumentPartitioningChanged)
    {
        if (!aDocumentPartitioningChanged) {
            try {

                IRegion info = fDocument.getLineInformationOfOffset(anEvent.getOffset());
                int start = Math.max(aPartition.getOffset(), info.getOffset());

                int end =
                    anEvent.getOffset()
                        + (anEvent.getText() == null ? anEvent.getLength() : anEvent.getText().length());
                if (info.getOffset() <= end && end <= (info.getOffset() + info.getLength())) {
                    // optimize the case of the same line
                    end = info.getOffset() + info.getLength();
                } else {
                    end = endOfLineOf(end);
                }
                end = Math.min(aPartition.getOffset() + aPartition.getLength(), end);
                return new Region(start, end - start);
            } catch (BadLocationException x) {
            }
        }
        return aPartition;
    }

    public void createPresentation(TextPresentation aPresentation, ITypedRegion aRegion)
    {
        addRange(aPresentation, aRegion.getOffset(), aRegion.getLength(), fDefaultTextAttribute);
    }

    protected void addRange(TextPresentation aPresentation, int anOffset, int aLength, TextAttribute anAttr)
    {
        if (anAttr != null)
            aPresentation.addStyleRange(new StyleRange(anOffset, aLength, anAttr.getForeground(), anAttr
                .getBackground(), anAttr.getStyle()));
    }
}
