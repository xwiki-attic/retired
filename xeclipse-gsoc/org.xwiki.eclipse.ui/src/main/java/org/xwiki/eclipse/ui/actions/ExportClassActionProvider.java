package org.xwiki.eclipse.ui.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.xwiki.eclipse.ui.UIConstants;

public class ExportClassActionProvider extends CommonActionProvider {
	private CommandContributionItem exportcustomclass;

	@Override
	public void init(final ICommonActionExtensionSite aSite) {
		CommandContributionItemParameter contributionItemParameter = new CommandContributionItemParameter(
				PlatformUI.getWorkbench(),
				UIConstants.EXPORT_CUSTOM_CLASS_COMMAND,
				UIConstants.EXPORT_CUSTOM_CLASS_COMMAND, 0);
		exportcustomclass = new CommandContributionItem(
				contributionItemParameter);
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS,
				exportcustomclass);
	}
}