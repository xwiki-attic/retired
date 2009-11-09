package org.xwiki.eclipse.ui.editors.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.xwiki.eclipse.ui.UIPlugin;

/**
 * Generate proposals for xwiki api
 * 
 * @author malaka
 * 
 */
public class XwikiAPIProcessor extends TemplateCompletionProcessor {

	private XwkiAPIObject contextType;

	public XwikiAPIProcessor(XwkiAPIObject contextType) {
		this.contextType = contextType;
	}

	public XwikiAPIProcessor() {

	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer,
			IRegion region) {
		return new TemplateContextType(
				"org.xwiki.eclipse.ui.editors.velocity.xwikiapi");
	}

	@Override
	protected Image getImage(Template template) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * select the proposal list according to the xwiki variable content type
	 */
	@Override
	protected Template[] getTemplates(String contextTypeId) {
		Template[] result = null;
		List<Template> list = new ArrayList<Template>();
		if (contextType != null) {
			switch (contextType) {
			case CONTEXT:
				list = UIPlugin.getDefault().getContextTemplates();
				result = new Template[list.size()];
				break;
			case DOC:
				list = UIPlugin.getDefault().getDocTemplates();
				result = new Template[list.size()];
				break;
			case REQUEST:
				list = UIPlugin.getDefault().getRequestTemplates();
				result = new Template[list.size()];
				break;
			case XWIKI:
				list = UIPlugin.getDefault().getXwikiTemplates();
				result = new Template[list.size()];
				break;
			}
			return list.toArray(result);
		} else {
			return new Template[0];

		}
	}

}
