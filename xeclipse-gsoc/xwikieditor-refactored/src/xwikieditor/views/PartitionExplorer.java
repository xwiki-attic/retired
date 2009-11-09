package xwikieditor.views;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.xwiki.eclipse.editors.PageEditor;

public class PartitionExplorer extends ViewPart implements ISelectionListener
{
    private Label editorLabel;

    private TreeViewer partitionTreeViewer;

    private PageEditor currentPageEditor;

    public PartitionExplorer()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void dispose()
    {
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
        super.dispose();
    }

    @Override
    public void init(IViewSite site) throws PartInitException
    {
        super.init(site);
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
    }

    @Override
    public void createPartControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(composite);

        editorLabel = new Label(composite, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(editorLabel);
        editorLabel.setText(" ");

        Button refreshButton = new Button(composite, SWT.PUSH);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(refreshButton);
        refreshButton.setText("Refresh");
        refreshButton.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub

            }

            public void widgetSelected(SelectionEvent e)
            {
                partitionTreeViewer.refresh();
            }
        });

        partitionTreeViewer = new TreeViewer(composite, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(partitionTreeViewer.getControl());
        partitionTreeViewer.setContentProvider(new DocumentPartitionContentProvider());
        partitionTreeViewer.setLabelProvider(new PartitionLabelProvider());
        partitionTreeViewer.addPostSelectionChangedListener(new ISelectionChangedListener()
        {

            public void selectionChanged(SelectionChangedEvent event)
            {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                ITypedRegion partition = (ITypedRegion) selection.getFirstElement();
                System.out.format("Selection changed: %s\n", partition);

                if (currentPageEditor != null) {
                    if (partition != null) {
                        currentPageEditor.setSelectionRange(partition.getOffset(), partition.getLength());
                    }
                }
            }

        });
    }

    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub

    }

    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
        if (part == currentPageEditor) {
            return;
        }

        if (part instanceof PageEditor) {
            PageEditor pageEditor = (PageEditor) part;
            currentPageEditor = (PageEditor) part;
            editorLabel.setText(pageEditor.getTitle());
            partitionTreeViewer.setInput(pageEditor.getDocumentProvider().getDocument(pageEditor.getEditorInput()));
        } else {
            currentPageEditor = null;
            editorLabel.setText("Not an XWiki editor");
            partitionTreeViewer.setInput(null);
        }

    }

}
