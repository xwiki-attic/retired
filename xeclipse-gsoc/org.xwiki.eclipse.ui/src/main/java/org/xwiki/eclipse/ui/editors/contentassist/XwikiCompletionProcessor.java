package org.xwiki.eclipse.ui.editors.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Point;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.model.XWikiEclipsePage;

/**
 * @author venkatesh
 */

class Tag
{
    String start;

    String end;

    String label;

    int adjust;

    public Tag(String start, String end, String label, int adjust)
    {
        this.start = start;
        this.end = end;
        this.label = label;
        this.adjust = adjust;
    }

    public String getStart()
    {
        return start;
    }

    public String getEnd()
    {
        return end;
    }

    public String getLabel()
    {
        return label;
    }

    public int getAdjust()
    {
        return adjust;
    }
}

public class XwikiCompletionProcessor extends AbstractCompletionProcessor
{

    private DataManager datamanager;

    private XWikiEclipsePage page;

    private List<Tag> tags;

    public XwikiCompletionProcessor(XWikiEclipsePage page)
    {
        this.page = page;
        datamanager = page.getDataManager();

        tags = new ArrayList<Tag>();
        tags.add(new Tag("1 ", "", "1 Title 1", -1));
        tags.add(new Tag("1.1 ", "", "1.1 Title 2", -1));
        tags.add(new Tag("1.1.1 ", "", "1.1.1 Title 3", -1));
        tags.add(new Tag("1.1.1.1 ", "", "1.1.1.1 Title 4", -1));
        tags.add(new Tag("1.1.1.1.1 ", "", "1.1.1.1.1 Title 5", -1));
        tags.add(new Tag("1.1.1.1.1.1 ", "", "1.1.1.1.1.1 Title 6", -1));
        tags.add(new Tag("<dl>\n\t", "</dl>", "<dl> Definition list", -1));
        tags.add(new Tag("*", "*", "*Bold*", -1));
        tags.add(new Tag("~~", "~~", "*Italic*", -1));
        tags.add(new Tag("__", "__", "__Underline__", -1));
        tags.add(new Tag("--", "--", "--Strikethrough--", -1));
        tags.add(new Tag("<tt>", "</tt>", "<tt> Monospace", -1));
        tags.add(new Tag("<sup>", "</sup>", "<sup> Superscript", -1));
        tags.add(new Tag("<sub>", "</sub>", "<sub> Subscript", -1));
        tags.add(new Tag("{quote:}", "{quote}", "{Quote:}", 0));
        tags.add(new Tag("{table}\n\t", "\n{table}", "{Table}", -1));
        tags.add(new Tag("{pre}", "{/pre}", "{pre}", -1));
        tags.add(new Tag("{code}", "{code}", "{Code}", -1));
        tags.add(new Tag("----\n", "", "Horizontal rule", -1));
        tags.add(new Tag("\n'n", "", "New paragraph", -1));
    }

    protected static class Validator implements IContextInformationValidator, IContextInformationPresenter
    {

        protected int fInstallOffset;

        /*
         * @see IContextInformationValidator#isContextInformationValid(int)
         */
        public boolean isContextInformationValid(int offset)
        {
            return Math.abs(fInstallOffset - offset) < 5;
        }

        /*
         * @see IContextInformationValidator#install(IContextInformation, ITextViewer, int)
         */
        public void install(IContextInformation info, ITextViewer viewer, int offset)
        {
            fInstallOffset = offset;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter #updatePresentation(int,
         * TextPresentation)
         */
        public boolean updatePresentation(int documentPosition, TextPresentation presentation)
        {
            return false;
        }
    }

    protected IContextInformationValidator fValidator = new Validator();

    /*
     * (non-Javadoc) Method declared on IContentAssistProcessor
     */
    @SuppressWarnings("unchecked")
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset)
    {
        // Retrieve current document
        IDocument doc = viewer.getDocument();

        ICompletionProposal[] proposals = null;
        // Retrieve current selection range
        Point selectedRange = viewer.getSelectedRange();

        List propList = new ArrayList();

        if (selectedRange.y > 0)
            try {
                // Retrieve selected text
                String text = doc.get(selectedRange.x, selectedRange.y);

                // Compute completion proposals
                computeStyleProposals(text, selectedRange, propList);
            } catch (BadLocationException e) {

            }
        else {
            // Retrieve qualifier
            String qualifier = getQualifier(doc, documentOffset);

            // Compute completion proposals
            computeStructureProposals(qualifier, documentOffset, propList);
        }

        proposals = new ICompletionProposal[propList.size()];

        // and fill with list elements
        propList.toArray(proposals);

        // Return the proposals
        return proposals;
    }

    @SuppressWarnings("unchecked")
    private void computeStyleProposals(String selectedText, Point selectedRange, List propList)
    {
        for (Tag tag : tags) {
            // Compute replacement text
            String replacement = tag.getStart() + selectedText + tag.getEnd();

            // Derive cursor position
            int cursor = 0;

            if (tag.getAdjust() == -1) {
                cursor = tag.getEnd().length() + tag.getStart().length() + selectedText.length();
            } else {
                cursor = tag.getEnd().length() + tag.getAdjust(); // For quote.. i think
                // its an anomaly.
            }

            // Compute a suitable context information
            IContextInformation contextInfo = new ContextInformation(null, tag.getLabel());

            // Construct proposal
            CompletionProposal proposal =
                new CompletionProposal(replacement, selectedRange.x, selectedRange.y, cursor, null, tag.getLabel(),
                    contextInfo, replacement);

            // and add to result list
            propList.add(proposal);
        }
    }

    private String getQualifier(IDocument doc, int documentOffset)
    {
        StringBuffer sb = new StringBuffer();

        int offset = documentOffset - 1;
        while (offset > 0) {
            try {
                char c = doc.getChar(offset);
                if (Character.isWhitespace(c)) {
                    break;
                }

                /* Prepend to the qualifier string. This is done because we are scanning the main text backwards. */
                sb.insert(0, c);

                offset = offset - 1;
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void computeStructureProposals(String qualifier, int documentOffset, List propList)
    {
        int qlen = qualifier.length();

        // Loop through all proposals
        for (Tag tag : tags) {
            // Check if proposal matches qualifier
            if (tag.getStart().toLowerCase().startsWith(qualifier.toLowerCase()) || qlen == 0) {

                // Yes -- compute whole proposal text
                String text = tag.getStart().toLowerCase() + tag.getEnd().toLowerCase();

                // Derive cursor position
                int cursor = tag.getStart().length();

                // Construct proposal
                CompletionProposal proposal =
                    new CompletionProposal(text, documentOffset - qlen, qlen, cursor, null, tag.getLabel(), null, null);

                // and add to result list
                propList.add(proposal);
            }
        }
    }
}
