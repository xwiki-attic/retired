package org.xwiki.eclipse.ui.handlers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.model.XWikiEclipseClassSummary;
import org.xwiki.eclipse.ui.utils.UIUtils;

/**
 * @author venkatesh
 * 
 */
public class ExportCustomClassHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@SuppressWarnings("unchecked")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		setWindow(HandlerUtil.getActiveWorkbenchWindow(event));
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		Set selectedObjects = UIUtils
				.getSelectedObjectsFromSelection(selection);
		for (Object selectedObject : selectedObjects)
			if (selectedObject instanceof DataManager) {
				final DataManager dataManager = (DataManager) selectedObject;
				run(dataManager);
			}
		return null;
	}

	private IWorkbenchWindow window;

	protected class Validator implements IInputValidator {

		public String isValid(String newText) {
			if (newText.toLowerCase().contains("class".subSequence(0, 5)))
				return "Should not contain class, XEclipse automatically appends it";
			if (newText.toLowerCase().contains(" ".subSequence(0, 1)))
				return "No spaces inside the name";
			return null;
		}

	}

	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(DataManager dataManager) {
		// TODO Makethis into a wizard..
		// TODO the implementation now, is that which reads a file, and parses
		// it...
		// possible further implementation can be, another dialog, with has,
		// this drop down list(of valid datatypes), and input boxes..
		String path = new FileDialog(window.getShell()).open();
		if (path == null)
			return;
		if (path.length()!=0)
			try {
				BufferedReader input = new BufferedReader(new FileReader(path));
				String line = "";
				int line_no = 1;
				Map<String, String> map = new HashMap<String, String>();
				while ((line = input.readLine()) != null) {
					line_no++;
					// TODO BUG below
					// if(line.matches("^%.*\\ .*")) //BUG HERE, why does the
					// first line does not match??
					// improper implementation below, should ideally be above
					// line of code
					if (line.contains("%".subSequence(0, 1))
							&& line.contains(" ".subSequence(0, 1))) {
						String[] split = line.split(" ");
						String type = split[split.length - 1];
						String[] split2 = split[0].split("%");
						String property = split2[split2.length - 1];
						map.put(property, type);
					} else {
						UIUtils
								.showMessageDialog(
										window.getShell(),
										SWT.ICON_ERROR,
										"Error",
										"Improper Syntax\nAt Line: "
												+ line_no
												+ "\nSyntax Expected: %<property> <datatype>");
						return;
					}
				}
				// TODO in NewObjectWizard, add option to add a new class
				// also...
				MessageBox box = new MessageBox(window.getShell(), SWT.YES
						| SWT.NO | SWT.ICON_QUESTION);
				box.setMessage("Add generated custom Class to "
						+ dataManager.getName() + "?");
				box.setText("Export?");
				int response = box.open();
				if (response == SWT.YES) {
					InputDialog InputDialog = new InputDialog(
							window.getShell(), "Name:",
							"Type in the Name of the Custom Class\n\n",
							"Custom", new Validator());
					int resp = InputDialog.open();
					if (resp == 0) // OK
					{
						String response1 = "XWiki."
								+ InputDialog.getValue().toLowerCase()
								+ "Class";
						try {
							List<XWikiEclipseClassSummary> classes;
							classes = dataManager.getClasses();
							for (XWikiEclipseClassSummary clas : classes) {
								String id = clas.getData().getId();
								if (response1.equals(id)) {
									UIUtils.showMessageDialog(
											window.getShell(), SWT.ICON_ERROR,
											"Error",
											"Another class of same name ("
													+ response1 + ") exists");
									return;
								}
							}
							// TODO xwiki.createClass(Map,name)
							// TODO add document sheet template??
							UIUtils.showMessageDialog(window.getShell(),
									SWT.ICON_INFORMATION, "Added", response1
											+ " has been added to "
											+ dataManager.getName());
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
				if (response == SWT.NO)
					return;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}