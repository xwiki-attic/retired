package org.xwiki.eclipse.ui.editors.scanners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.xwiki.eclipse.ui.editors.scanners.detectors.DirectiveWordDetector;
import org.xwiki.eclipse.ui.editors.scanners.rules.XwikiTextStylesRule;


/**
 * @author venkatesh
 */

public class DocumentPartitionScanner extends RuleBasedPartitionScanner
{
    // Partitions related to xwiki syntax
    public static final String XWIKI_CODE = "xwiki_code";

    public static final String XWIKI_PRE = "xwiki_pre";

    public static final String XWIKI_TABLE = "xwiki_table";

    public static final String XWIKI_DL = "xwiki_dl";

    public static final String XWIKI_HEADING = "xwiki_heading";

    public static final String XWIKI_LIST = "xwiki_list";

    public static final String XWIKI_MACRO = "xwiki_macro";

    public static final String XWIKI_START_FLOTING_MACRO = "xwiki_start_floting_macro";

    public static final String XWIKI_END_FLOTING_MACRO = "xwiki_end_floting_macro";

    public static final String XWIKI_TEXT_STYLES = "xwiki_text_styles";

    public static final String XWIKI_LINK = "xwiki_link";

    // Partitions related to velocity syntax
    public final static String VELO_INTERPOLATED_STRING = "velo_parsed_string";

    public final static String VELO_NON_INTERPOLATED_STRING = "velo_unparsed_string";

    public final static String VELO_SINGLE_LINE_COMMENT = "velo_singleline_comment";

    public final static String VELO_MULTI_LINE_COMMENT = "velo_multiline_comment";

    public final static String VELO_DOC_COMMENT = "velo_doc_comment";

    public final static String[] XWIKI_PARTITION_TYPES =
        new String[] {XWIKI_CODE, XWIKI_PRE, XWIKI_TABLE, XWIKI_DL, XWIKI_HEADING, XWIKI_LIST, XWIKI_MACRO,
        XWIKI_TEXT_STYLES, XWIKI_LINK, XWIKI_START_FLOTING_MACRO, VELO_INTERPOLATED_STRING, VELO_NON_INTERPOLATED_STRING,
        VELO_SINGLE_LINE_COMMENT, VELO_MULTI_LINE_COMMENT, VELO_DOC_COMMENT};

    public DocumentPartitionScanner()
    {
        super();
        // Xwiki related tokens
        IToken xwikiCodeToken = new Token(XWIKI_CODE);
        IToken xwikiPreToken = new Token(XWIKI_PRE);
        IToken xwikiTableToken = new Token(XWIKI_TABLE);
        IToken xwikiDlToken = new Token(XWIKI_DL);
        IToken xwikiMacroToken = new Token(XWIKI_MACRO);
        IToken xwikiTextStylesToken = new Token(XWIKI_TEXT_STYLES);
        IToken xwikiLinkToken = new Token(XWIKI_LINK);
        IToken xwikiStartFoldingMacro = new Token(XWIKI_START_FLOTING_MACRO);
        
        // Velocity related tokens       
        IToken veloDocCommentToken = new Token(VELO_DOC_COMMENT);
        IToken veloSingleLineCommentToken = new Token(VELO_SINGLE_LINE_COMMENT);
        IToken veloMultiLineCommentToken = new Token(VELO_MULTI_LINE_COMMENT);
        IToken veloInterpolatedStringToken = new Token(VELO_INTERPOLATED_STRING);
        IToken veloNonInterpolatedStringToken = new Token(VELO_NON_INTERPOLATED_STRING);

        List<IRule> rules = new ArrayList<IRule>();
        
        // code partition
        rules.add(new MultiLineRule("{code}", "{code}", xwikiCodeToken, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
        // pre partition
        rules.add(new MultiLineRule("{pre}", "{/pre}", xwikiPreToken, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
        // table partition
        rules.add(new MultiLineRule("{table}", "{table}", xwikiTableToken, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
        // dl partitions
        rules.add(new MultiLineRule("<dl>", "</dl>", xwikiDlToken, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$

        // links
        rules.add(new SingleLineRule("[", "]", xwikiLinkToken, '\\', true));
        rules.add(new SingleLineRule("{link:", "}", xwikiLinkToken, '\\', true));

        // Macro partition
        rules.add(new SingleLineRule("#info(", ")", xwikiMacroToken, '\\', true));
        rules.add(new SingleLineRule("#warning(", ")", xwikiMacroToken, '\\', true));
        rules.add(new SingleLineRule("#error(", ")", xwikiMacroToken, '\\', true));
        rules.add(new SingleLineRule("#floatingbox(", ")", xwikiMacroToken, '\\', true));
        rules.add(new SingleLineRule("#startfloatingbox()", "", xwikiStartFoldingMacro, '\\', true));
        rules.add(new SingleLineRule("#endfloatingbox()", "", xwikiStartFoldingMacro, '\\', true));

        // Text Styles
        rules.add(new XwikiTextStylesRule("*", "*", xwikiTextStylesToken, '\\', false, true)); //$NON-NLS-1$//$NON-NLS-2$
        rules.add(new XwikiTextStylesRule("~~", "~~", xwikiTextStylesToken, '\\', false,true)); //$NON-NLS-1$//$NON-NLS-2$
        rules.add(new XwikiTextStylesRule("__", "__", xwikiTextStylesToken, '\\', false,true)); //$NON-NLS-1$//$NON-NLS-2$
        rules.add(new XwikiTextStylesRule("--", "--", xwikiTextStylesToken, '\\', false,true)); //$NON-NLS-1$//$NON-NLS-2$
        
        // velocity related partitions
        
        // Rule for single line comments
        rules.add(new EndOfLineRule("##", veloSingleLineCommentToken));
        // Rule for strings which can be interpolated
        rules.add(new SingleLineRule("\"", "\"", veloInterpolatedStringToken, '\\'));
        // Rule for strings constants.
        rules.add(new SingleLineRule("'", "'", veloNonInterpolatedStringToken, '\\'));
        // Rules for multi-line comments and doc comments
        rules.add(new MultiLineRule("#**", "*#", veloDocCommentToken));
        rules.add(new MultiLineRule("#*", "*#", veloMultiLineCommentToken));


        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        
        setPredicateRules(result);
    }
}
