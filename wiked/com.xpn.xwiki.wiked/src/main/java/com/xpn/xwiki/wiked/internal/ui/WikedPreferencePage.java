
package com.xpn.xwiki.wiked.internal.ui;

import java.util.Arrays;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.repository.IRepositoryManager;
import com.xpn.xwiki.wiked.repository.RepositoryDescriptor;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>,
 * we can use the field support built into JFace that allows us to create a
 * page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */
public class WikedPreferencePage extends PreferencePage 
    implements IWorkbenchPreferencePage {

	private TableViewer typeTable;
	private Label detail;
	
    public void init(IWorkbench workbench) {
    }

    /**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public Control createContents(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Available repositories");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gridData);
		
		Table table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 100;
		table.setLayoutData(gridData);
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(15));
		tableLayout.addColumnData(new ColumnWeightData(25));
		tableLayout.addColumnData(new ColumnWeightData(60));
		table.setLayout(tableLayout);
		typeTable = new TableViewer(table);
		typeTable.setLabelProvider(new TypeLabelProvider());
		typeTable.setContentProvider(new ContentProvider());
		TableColumn c1 = new TableColumn(table, SWT.LEFT);
		TableColumn c2 = new TableColumn(table, SWT.LEFT);
		TableColumn c3 = new TableColumn(table, SWT.LEFT);
		c1.setText("Type");
		c2.setText("Name");
		c3.setText("Description");
		typeTable.setInput(WikedPlugin.getInstance().getRepositoryManager());
		typeTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				detail.setText(createDetail(event));
			}
		});

		this.detail = new Label(parent, SWT.NONE);
		this.detail.setText("");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 40;
		this.detail.setLayoutData(gridData);

		return parent;
	}

	private String createDetail(SelectionChangedEvent event) {
		if (event.getSelection().isEmpty() == false && 
			event.getSelection() instanceof IStructuredSelection) {
			RepositoryDescriptor desc = (RepositoryDescriptor)
				((IStructuredSelection)event.getSelection()).getFirstElement();
			StringBuffer buff = new StringBuffer();
			IConfigurationElement element = desc.getConfigurationElement();
			buff.append("Declaring namespace: ").append(
				element.getDeclaringExtension().getNamespace()).append('\n');
			buff.append("Factory: ").append(
				element.getAttribute("factory"));
			return buff.toString();
		}
			
		return null;
	}

	private static class ContentProvider implements IStructuredContentProvider {
		
		private RepositoryDescriptor[] descriptors;
		
		public Object[] getElements(Object inputElement) {
			return (descriptors != null) ? descriptors : new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof IRepositoryManager) {
				IRepositoryManager mgr = (IRepositoryManager)newInput;
		    	String[] types = mgr.getRepositoryTypes();
				Arrays.sort(types);
				this.descriptors = new RepositoryDescriptor[types.length];
		    	for (int i = 0; i < types.length; i++) {
					String type = types[i];
					this.descriptors[i] = mgr.getRepositoryDescriptor(type);
				}
			} else {
				this.descriptors = null;
			}
		}
	}
	
	private static class TypeLabelProvider 
		extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			RepositoryDescriptor desc = (RepositoryDescriptor)element;
			switch (columnIndex) {
				default: return desc.getName();
				case 0: return desc.getType();
				case 1: return desc.getName();
				case 2: return desc.getDescription();
			}
		}
	}
}