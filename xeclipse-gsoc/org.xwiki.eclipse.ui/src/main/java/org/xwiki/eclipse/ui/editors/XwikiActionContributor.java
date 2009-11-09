package org.xwiki.eclipse.ui.editors;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.xwiki.eclipse.ui.actions.ConvertAction;
import org.xwiki.eclipse.ui.actions.WikiCommonSyntaxAction;

/**
 * @author venkatesh
 * 
 */

public class XwikiActionContributor extends TextEditorActionContributor {

	protected RetargetTextEditorAction fContentAssistProposal;
	protected ConvertAction fConvertActionRAW;
	protected ConvertAction fConvertActionJSP;
	protected WikiCommonSyntaxAction fWikiCommonSyntaxAction;

	public XwikiActionContributor() {
		super();
		try {
			fContentAssistProposal = new RetargetTextEditorAction(
					XwikiEditorMessages.getResourceBundle(),
					"ContentAssistProposal."); //$NON-NLS-1$
			fContentAssistProposal
					.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
			fConvertActionRAW = new ConvertAction("XWIKI", "RAW");
			fConvertActionJSP = new ConvertAction("XWIKI", "JSP");
			fWikiCommonSyntaxAction = new WikiCommonSyntaxAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * @see IEditorActionBarContributor#init(IActionBars)
	 */
	@Override
	public void init(IActionBars bars) {
		super.init(bars);
		IMenuManager menuManager = bars.getMenuManager();
		IMenuManager editMenu = menuManager
				.findMenuUsingPath("org.xwiki.xeclipse.menu.Edit");
		IMenuManager convert = new MenuManager("Convert to Syntax",
				"org.xwiki.xeclipse.menu.Edit.convert");
		if (editMenu != null) {
			editMenu.add(new Separator());
			editMenu.add(fContentAssistProposal);
			editMenu.add(convert);
			convert.add(fConvertActionRAW);
			convert.add(fConvertActionJSP);
			convert.add(fWikiCommonSyntaxAction);
		}
	}

	private void doSetActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		ITextEditor editor = null;
		if (part instanceof ITextEditor)
			editor = (ITextEditor) part;
		fConvertActionRAW.setEditor(editor);
		fConvertActionJSP.setEditor(editor);
		fWikiCommonSyntaxAction.setEditor(editor);
		fContentAssistProposal.setAction(getAction(editor,
				"ContentAssistProposal")); //$NON-NLS-1$
	}

	/*
	 * @see IEditorActionBarContributor#setActiveEditor(IEditorPart)
	 */
	@Override
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		doSetActiveEditor(part);
	}

	/*
	 * @see IEditorActionBarContributor#dispose()
	 */
	@Override
	public void dispose() {
		doSetActiveEditor(null);
		super.dispose();
	}
}