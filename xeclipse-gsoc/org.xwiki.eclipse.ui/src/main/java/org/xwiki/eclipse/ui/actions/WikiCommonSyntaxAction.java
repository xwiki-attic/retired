package org.xwiki.eclipse.ui.actions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.XWikiEclipseException;
import org.xwiki.eclipse.core.model.XWikiEclipseObject;
import org.xwiki.eclipse.core.model.XWikiEclipseObjectProperty;
import org.xwiki.eclipse.core.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.core.model.XWikiEclipsePage;
import org.xwiki.eclipse.ui.editors.PageEditorInput;
import org.xwiki.eclipse.ui.editors.XwikiEditorMessages;

/**
 * @author venkatesh
 * 
 */
public class WikiCommonSyntaxAction extends TextEditorAction {

	public WikiCommonSyntaxAction() {
		super(XwikiEditorMessages.getResourceBundle(), null, null);
		setText("WikiCommonSyntax Document of Objects");
		setEnabled(true);
	}

	@Override
	public void run() {
		if (getTextEditor().getEditorInput() instanceof PageEditorInput) {
			FileDialog dialog = new FileDialog(getTextEditor().getSite()
					.getWorkbenchWindow().getShell());
			dialog.setOverwrite(true);
			String path = dialog.open();
			if (path == null)
				return;
			if (path.length() != 0) {
				XWikiEclipsePage page = ((PageEditorInput) getTextEditor()
						.getEditorInput()).getPage();
				DataManager datamanager = page.getDataManager();
				try {
					FileOutputStream output = new FileOutputStream(path, true);
					List<XWikiEclipseObjectSummary> list = datamanager
							.getObjects(page.getData().getId());
					List<XWikiEclipseObjectProperty> list_properties;
					for (XWikiEclipseObjectSummary item : list) {
						XWikiEclipseObject object = datamanager.getObject(page
								.getData().getId(), item.getData()
								.getClassName(), item.getData().getId());
						list_properties = object.getProperties();
						String value;
						for (XWikiEclipseObjectProperty property : list_properties) {
							try {
								value = object.getProperty(property.getName())
										.getValue().toString();
							} catch (NullPointerException e) {
								// NullValue, skipping.
								continue;
							}
							if (!(value.toLowerCase().equals("null") || (value.length() == 0)))
								output.write("%".concat(property.getName())
										.concat(" ").concat(value).concat("\n")
										.getBytes());
						}
					}
					output.close();
				} catch (XWikiEclipseException e) {
					System.out
							.println("XWikiEclipseException in WikiCommonSyntaxAction");
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					System.out
							.println("FileNotFound Exception in WikiCommonSyntaxAction");
				} catch (Exception e) {
					e.printStackTrace();
				}
				// The code below prints out the properties of objects..
				/*
				 * List<XWikiEclipseClassSummary> classes; try { classes =
				 * datamanager.getClasses(); for(XWikiEclipseClassSummary clas :
				 * classes) { String id = clas.getData().getId();
				 * XWikiEclipseClass temp = datamanager.getClass(id);
				 * System.out.println(id); Set properties =
				 * temp.getData().getProperties(); for(int i = 0;
				 * i<properties.size();i++) {
				 * System.out.println("properties.toArray:
				 * "+properties.toArray()[i].toString());
				 * System.out.println("getPropertyClass
				 * "+temp.getData().getPropertyClass
				 * (properties.toArray()[i].toString())); } } } catch
				 * (XWikiEclipseException e) { e.printStackTrace(); }
				 */
			}
		}
	}
}