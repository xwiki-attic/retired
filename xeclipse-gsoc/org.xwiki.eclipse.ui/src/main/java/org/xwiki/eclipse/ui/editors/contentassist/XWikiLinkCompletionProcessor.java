package org.xwiki.eclipse.ui.editors.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.XWikiEclipseException;
import org.xwiki.eclipse.core.model.XWikiEclipsePageSummary;
import org.xwiki.eclipse.core.model.XWikiEclipseSpaceSummary;

public class XWikiLinkCompletionProcessor extends AbstractCompletionProcessor
{

    private DataManager dataManager;

    public XWikiLinkCompletionProcessor(DataManager dataManager)
    {
        this.dataManager = dataManager;
    }

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
    {
        List<CompletionProposal> completionProposals = new ArrayList<CompletionProposal>();

        try {
            List<XWikiEclipseSpaceSummary> spaces = dataManager.getSpaces();
            for (XWikiEclipseSpaceSummary spaceSummary : spaces) {
                List<XWikiEclipsePageSummary> pages = dataManager.getPages(spaceSummary.getData().getKey());
                for (XWikiEclipsePageSummary page : pages) {
                    String replacement = page.getData().getId() + "]";
                    completionProposals.add(new CompletionProposal(replacement, offset, 0, replacement.length(), null,
                        page.getData().getId(), null, null));
                }
            }
        } catch (XWikiEclipseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ICompletionProposal[] proposals = new ICompletionProposal[completionProposals.size()];

        return completionProposals.toArray(proposals);
    }
}
