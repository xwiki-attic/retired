package org.xwiki.eclipse.plugin;

import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.xwiki.eclipse.plugin.code.CodeScanner;
import org.xwiki.eclipse.plugin.dl.DlScanner;
import org.xwiki.eclipse.plugin.pre.PreScanner;
import org.xwiki.eclipse.plugin.table.TableScanner;
import org.xwiki.eclipse.plugin.util.ColorProvider;
import org.xwiki.eclipse.plugin.xwiki.XwikiCodeScanner;

/**
 * @author venkatesh
 *
 */

public class XwikiEditorExamplePlugin extends AbstractUIPlugin {

	public final static String XWIKI_PARTITIONING= "__xwiki_partitioning";   //$NON-NLS-1$
	private static XwikiEditorExamplePlugin fgInstance;
	private XwikiPartitionScanner fPartitionScanner;
	private ColorProvider fColorProvider;
	private XwikiCodeScanner fCodeScanner;
	private CodeScanner fCodeCodeScanner;
	private PreScanner fPreScanner;
	private TableScanner fTableScanner;
	private DlScanner fDlScanner;

	public XwikiEditorExamplePlugin() {
		fgInstance= this;
	}

	public static XwikiEditorExamplePlugin getDefault() {
		return fgInstance;
	}

	public XwikiPartitionScanner getXwikiPartitionScanner() {
		if (fPartitionScanner == null)
			fPartitionScanner= new XwikiPartitionScanner();
		return fPartitionScanner;
	}

	public RuleBasedScanner getXwikiCodeScanner() {
		if (fCodeScanner == null)
			fCodeScanner= new XwikiCodeScanner(getXwikiColorProvider(), this.getWorkbench().getDisplay());
		return fCodeScanner;
	}

	public ColorProvider getXwikiColorProvider() {
		if (fColorProvider == null)
			fColorProvider= new ColorProvider();
		return fColorProvider;
	}

	public RuleBasedScanner getCodeScanner() {
		if (fCodeCodeScanner == null)
			fCodeCodeScanner= new CodeScanner(fColorProvider, this.getWorkbench().getDisplay());
		return fCodeCodeScanner;
	}	 

	public RuleBasedScanner getPreScanner() {
		if (fPreScanner == null)
			fPreScanner= new PreScanner(fColorProvider, this.getWorkbench().getDisplay());
		return fPreScanner;
	}
	public RuleBasedScanner getDlScanner() {
		if (fDlScanner == null)
			fDlScanner= new DlScanner(fColorProvider);
		return fDlScanner;
	}
	public RuleBasedScanner getTableScanner() {
		if (fTableScanner == null)
			fTableScanner= new TableScanner(fColorProvider);
		return fTableScanner;
	}	 
}