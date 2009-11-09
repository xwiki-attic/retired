package org.xwiki.eclipse.ui.editors.scanners;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.xwiki.eclipse.ui.editors.util.ColorProvider;

/**
 * @author venkatesh
 */

public class EditorScanners extends AbstractUIPlugin {

	public final static String XWIKI_PARTITIONING = "__xwiki_partitioning"; //$NON-NLS-1$

	// Without the 'new XwikiEditorExamplePlugin()', produced null pointer error
	private static EditorScanners fgInstance = new EditorScanners();

	private DocumentPartitionScanner fDocumentPartitionScanner;

	private ColorProvider fColorProvider;

	// private XwikiCodeScanner fXwikiCodeScanner;

	private CodeScanner fXwikiCodeScanner;

	private PreScanner fXwikiPreScanner;

	private TableScanner fXwikiTableScanner;

	private HeadingScanner fXwikiHeadingScanner;

	private ListScanner fXwikiListScanner;

	private MacroScanner fXwikiMacroScanner;

	private DlScanner fXwikiDlScanner;

	private TextStylesScanner fXwikiTextStylesScanner;

	private LinkScanner fXwikiLinkScanner;

	private XwikiDefaultCodeScanner fXwikiVeloCodeScanner;

	private VelocityStringScanner fVeloStringScanner;

	public EditorScanners() {
		fgInstance = this;
	}

	public static EditorScanners getDefault() {

		return fgInstance;
	}

	public XwikiDefaultCodeScanner getDefaultCodeScanner() {
		if (fXwikiVeloCodeScanner == null)
			fXwikiVeloCodeScanner = new XwikiDefaultCodeScanner(
					getXwikiColorProvider());
		return fXwikiVeloCodeScanner;
	}

	public DocumentPartitionScanner getDocumentPartitionScanner() {
		if (fDocumentPartitionScanner == null)
			fDocumentPartitionScanner = new DocumentPartitionScanner();
		return fDocumentPartitionScanner;
	}

	public ColorProvider getXwikiColorProvider() {
		if (fColorProvider == null)
			fColorProvider = new ColorProvider();
		return fColorProvider;
	}

	public RuleBasedScanner getXwikiCodeScanner() {
		if (fXwikiCodeScanner == null)
			fXwikiCodeScanner = new CodeScanner(fColorProvider, getWorkbench()
					.getDisplay());
		return fXwikiCodeScanner;
	}

	public RuleBasedScanner getXwikiPreScanner() {
		if (fXwikiPreScanner == null)
			fXwikiPreScanner = new PreScanner(fColorProvider, getWorkbench()
					.getDisplay());
		return fXwikiPreScanner;
	}

	public RuleBasedScanner getXwikiDlScanner() {
		if (fXwikiDlScanner == null)
			fXwikiDlScanner = new DlScanner(fColorProvider);
		return fXwikiDlScanner;
	}

	public RuleBasedScanner getXwikiTableScanner() {
		if (fXwikiTableScanner == null)
			fXwikiTableScanner = new TableScanner(fColorProvider);
		return fXwikiTableScanner;
	}

	public RuleBasedScanner getXwikiHeadingScanner() {
		if (fXwikiHeadingScanner == null)
			fXwikiHeadingScanner = new HeadingScanner(fColorProvider);
		fXwikiHeadingScanner.setDefaultReturnToken(new Token(new TextAttribute(
				fColorProvider.getColor(ColorProvider.XWIKI_HEADING))));
		return fXwikiHeadingScanner;
	}

	public RuleBasedScanner getXwikiListScanner() {
		if (fXwikiListScanner == null)
			fXwikiListScanner = new ListScanner(fColorProvider);
		fXwikiListScanner.setDefaultReturnToken(new Token(new TextAttribute(
				fColorProvider.getColor(ColorProvider.XWIKI_LIST))));
		return fXwikiListScanner;
	}

	public RuleBasedScanner getXwikiTextStylesScanner() {
		if (fXwikiTextStylesScanner == null)
			fXwikiTextStylesScanner = new TextStylesScanner(fColorProvider);
		fXwikiTextStylesScanner.setDefaultReturnToken(new Token(new TextAttribute(
				fColorProvider.getColor(ColorProvider.VELO_STRING))));
		return fXwikiTextStylesScanner;
	}

	public RuleBasedScanner getXwikiLinkScanner() {
		if (fXwikiLinkScanner == null)
			fXwikiLinkScanner = new LinkScanner(fColorProvider);
		return fXwikiLinkScanner;
	}

	public RuleBasedScanner getMacroScanner() {
		if (fXwikiMacroScanner == null)
			fXwikiMacroScanner = new MacroScanner(fColorProvider);
		fXwikiMacroScanner.setDefaultReturnToken(new Token(new TextAttribute(
				fColorProvider.getColor(ColorProvider.XWIKI_MACRO))));
		return fXwikiMacroScanner;
	}

	// Velocity related scanners
	public VelocityStringScanner getVelosityStringScanner() {

		if (fVeloStringScanner == null)
			fVeloStringScanner = new VelocityStringScanner(
					getXwikiColorProvider());
		return fVeloStringScanner;
	}

}
