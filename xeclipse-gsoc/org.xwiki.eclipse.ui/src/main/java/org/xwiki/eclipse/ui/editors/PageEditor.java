/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.xwiki.eclipse.ui.editors;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.xwiki.eclipse.core.CorePlugin;
import org.xwiki.eclipse.core.DataManager;
import org.xwiki.eclipse.core.XWikiEclipseException;
import org.xwiki.eclipse.core.model.XWikiEclipseObject;
import org.xwiki.eclipse.core.model.XWikiEclipsePage;
import org.xwiki.eclipse.core.notifications.CoreEvent;
import org.xwiki.eclipse.core.notifications.ICoreEventListener;
import org.xwiki.eclipse.core.notifications.NotificationManager;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;
import org.xwiki.eclipse.ui.dialogs.PageConflictDialog;
import org.xwiki.eclipse.ui.editors.parser.VeloReconcilingStrategy;
import org.xwiki.eclipse.ui.editors.parser.model.Node;
import org.xwiki.eclipse.ui.editors.parser.model.VeloObjectModel;

import org.xwiki.eclipse.ui.utils.UIUtils;
import org.xwiki.xmlrpc.model.XWikiPage;

public class PageEditor extends TextEditor implements ICoreEventListener, IEditorStatusLine
{
    public static final String ID = "org.xwiki.eclipse.ui.editors.PageEditor";

    private Form form;

    private XwikiContentOutlinePage outline_page;

    private boolean conflictDialogDisplayed;

    private EditConflictAction editConflictAction;

    private VelocityErrorAction velocityErrorAction;

    private VeloReconcilingStrategy fReconcilingStrategy;

    private VeloObjectModel veloObjectModel;

    private class EditConflictAction extends Action
    {
        public EditConflictAction()
        {
            super("org.xwiki.eclipse.ui.pageEditor.editConflict");
            setText("Edit conflict");
            setImageDescriptor(UIPlugin.getImageDescriptor(UIConstants.CONFLICT_ICON));

        }

        @Override
        public void run()
        {

            handleConflict();
        }
    }

    private class VelocityErrorAction extends Action
    {
        public VelocityErrorAction()
        {
            super("org.xwiki.eclipse.ui.pageEditor.velocityErrorAction");
            setText("velocity error");
            setImageDescriptor(UIPlugin.getImageDescriptor(UIConstants.CONFLICT_ICON));

        }

        @Override
        public void run()
        {
            Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0, "Veolocity error", null);
            ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Veolocity error", fReconcilingStrategy
                .getErrorMsg(), status);
        }
    }

    public PageEditor()
    {
        super();
        setDocumentProvider(new PageDocumentProvider(this));
        fReconcilingStrategy = new VeloReconcilingStrategy(this);
        veloObjectModel = new VeloObjectModel(this);
    }

    @Override
    public void dispose()
    {
        if (outline_page != null)
            outline_page.setInput(null);
        NotificationManager.getDefault().removeListener(this);
        super.dispose();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        super.init(site, input);
        setSourceViewerConfiguration(new XwikiSourceViewerConfiguration(((PageEditorInput) input).getPage(), this));
        NotificationManager.getDefault().addListener(this,
            new CoreEvent.Type[] {CoreEvent.Type.OBJECT_STORED, CoreEvent.Type.OBJECT_REMOVED});
    }

    public VeloReconcilingStrategy getReconcilingStrategy()
    {
        return fReconcilingStrategy;
    }

    @Override
    public void createPartControl(Composite parent)
    {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createForm(parent);
        toolkit.decorateFormHeading(form);
        editConflictAction = new EditConflictAction();
        velocityErrorAction = new VelocityErrorAction();

        GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(form.getBody());

        Composite editorComposite = new Composite(form.getBody(), SWT.NONE);
        editorComposite.setLayout(new FillLayout());
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(editorComposite);
        super.createPartControl(editorComposite);
        // super.createPartControl(parent);
        try {
            this.uninstallTextDragAndDrop(this.getSourceViewer());
            final StyledText control = this.getSourceViewer().getTextWidget();
            DragSource drag = new DragSource(control, DND.DROP_COPY | DND.DROP_MOVE);
            // get a reference to the workbench-part that is being dragged
            IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

            final IWorkbenchPart workbenchPartBeingDragged = workbenchWindow.getActivePage().getActivePart();

            Transfer[] transferTypes = null;
            if (workbenchPartBeingDragged instanceof IEditorPart)
                transferTypes = new Transfer[] {EditorInputTransfer.getInstance()};
            else
                transferTypes = new Transfer[] {TextTransfer.getInstance()};
            drag.setTransfer(transferTypes);

            drag.addDragListener(new DragSourceListener()
            {
                public void dragStart(DragSourceEvent dsEvent)
                {
                    System.out.println("drag started...");
                }

                public void dragSetData(DragSourceEvent dsEvent)
                {
                    System.out.println("inside set data..");
                    System.out.print(workbenchPartBeingDragged.getTitle() + " ");
                    System.out.println(workbenchPartBeingDragged.getClass().getCanonicalName() + " "
                        + workbenchPartBeingDragged.getClass().getSimpleName());

                    if (workbenchPartBeingDragged instanceof IEditorPart) {
                        String editorId = workbenchPartBeingDragged.getSite().getId();
                        IEditorInput editorInput = ((IEditorPart) workbenchPartBeingDragged).getEditorInput();
                        EditorInputTransfer.EditorInputData data =
                            EditorInputTransfer.createEditorInputData(editorId, editorInput);
                        dsEvent.data = new EditorInputTransfer.EditorInputData[] {data};
                        System.out.println("drag data has been set...");
                    } else if (workbenchPartBeingDragged instanceof IViewPart) {
                        try {
                            System.out.println("yahan hoon...");
                            String viewId = workbenchPartBeingDragged.getSite().getId();
                            // Why is this empty??
                            System.out.println("asdfasdf '" + control.getSelectionText() + "'");
                            dsEvent.data = viewId;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                public void dragFinished(DragSourceEvent dsEvent)
                {
                    System.out.println("drag finished...");
                }
            });
            DropTarget target = new DropTarget(control, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
            target.setTransfer(transferTypes);
            target.addDropListener(new DropTargetListener()
            {

                public void dragEnter(DropTargetEvent event)
                {
                    System.out.println("inside dragenter");

                }

                public void dragLeave(DropTargetEvent event)
                {
                    System.out.println("inside dragleave");

                }

                public void dragOperationChanged(DropTargetEvent event)
                {
                    System.out.println("inside dragoperationchanged");

                }

                public void dragOver(DropTargetEvent event)
                {
                    System.out.println("inside dragover");

                }

                public void drop(DropTargetEvent event)
                {
                    System.out.println("inside drop");

                }

                public void dropAccept(DropTargetEvent event)
                {
                    System.out.println("inside dropaccept");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateInfo();
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException
    {
        if (!(input instanceof PageEditorInput))
            throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Invalid input for editor"));

        PageEditorInput pageEditorInput = (PageEditorInput) input;

        ISourceViewer sourceViewer = getSourceViewer();
        if (sourceViewer != null) {
            int caretOffset = sourceViewer.getTextWidget().getCaretOffset();
            int topPixel = sourceViewer.getTextWidget().getTopPixel();
            super.doSetInput(pageEditorInput);
            sourceViewer.getTextWidget().setCaretOffset(caretOffset);
            sourceViewer.getTextWidget().setTopPixel(topPixel);

            if (!conflictDialogDisplayed)
                handleConflict();
        } else {
            super.doSetInput(pageEditorInput);

            if (pageEditorInput.getPage().getDataManager().isInConflict(pageEditorInput.getPage().getData().getId())) {
                UIUtils
                    .showMessageDialog(
                        getSite().getShell(),
                        "Page is still in conflict.",
                        "The page is still in conflict. In order to handle the conflict click on icon in the upper left corner of the title bar. You may continue to edit the page, changes will be saved locally until you decide to solve the conflict.");

                conflictDialogDisplayed = true;
            }
        }

        updateInfo();

        if (outline_page != null)
            outline_page.setInput(input);

    }

    private void updateInfo()
    {
        if (form != null) {
            PageEditorInput input = (PageEditorInput) getEditorInput();
            if (input != null) {
                XWikiEclipsePage page = input.getPage();

                int version = page.getData().getVersion();
                int minorVersion = page.getData().getMinorVersion();

                /* Compatibility with XWiki 1.3 */
                if (version > 65536) {
                    int temp = version;
                    version = temp >> 16;
                    minorVersion = temp & 0xFFFF;
                }

                form.setText(String.format("%s version %s.%s", page.getData().getId(), version, minorVersion));
            }

            if (input.getPage().getDataManager().isInConflict(input.getPage().getData().getId())) {
                boolean editConlictActionFound = false;
                for (IContributionItem contributionItem : form.getToolBarManager().getItems())
                    if (contributionItem instanceof ActionContributionItem) {
                        ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
                        if (actionContributionItem.getAction().equals(editConflictAction))
                            editConlictActionFound = true;
                    }

                if (!editConlictActionFound) {
                    form.getToolBarManager().add(editConflictAction);
                    form.updateToolBar();
                }

                form.setMessage("Page is in conflict.", IMessageProvider.WARNING);
            } else if (!fReconcilingStrategy.isErrorFree()) {
                boolean veloErrorActionFound = false;

                for (IContributionItem contributionItem : form.getToolBarManager().getItems())
                    if (contributionItem instanceof ActionContributionItem) {
                        ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
                        if (actionContributionItem.getAction().equals(velocityErrorAction))
                            veloErrorActionFound = true;
                    }

                if (!veloErrorActionFound) {

                    form.getToolBarManager().add(velocityErrorAction);
                    form.updateToolBar();
                }
            } else {
                form.getToolBarManager().removeAll();
                form.updateToolBar();
                form.setMessage(input.getPage().getDataManager().getName());
            }

        }
    }

    private void handleConflict()
    {
        PageEditorInput input = (PageEditorInput) getEditorInput();
        XWikiEclipsePage currentPage = input.getPage();
        DataManager dataManager = currentPage.getDataManager();

        if (dataManager.isInConflict(currentPage.getData().getId()))
            try {
                XWikiEclipsePage conflictingPage = dataManager.getConflictingPage(currentPage.getData().getId());

                XWikiEclipsePage conflictAncestorPage =
                    dataManager.getConflictAncestorPage(currentPage.getData().getId());

                PageConflictDialog compareDialog =
                    new PageConflictDialog(Display.getDefault().getActiveShell(), currentPage, conflictingPage,
                        conflictAncestorPage);
                int result = compareDialog.open();

                conflictDialogDisplayed = true;

                switch (result) {
                    case PageConflictDialog.ID_USE_LOCAL:
                        XWikiPage newPage = new XWikiPage(conflictingPage.getData().toRawMap());
                        newPage.setContent(currentPage.getData().getContent());
                        dataManager.clearConflictingStatus(newPage.getId());
                        setInput(new PageEditorInput(new XWikiEclipsePage(dataManager, newPage)));

                        /* Force the editor to be dirty */
                        getDocumentProvider().getDocument(getEditorInput()).set(
                            getDocumentProvider().getDocument(getEditorInput()).get());

                        conflictDialogDisplayed = false;
                        break;
                    case PageConflictDialog.ID_USE_REMOTE:
                        dataManager.clearConflictingStatus(conflictingPage.getData().getId());
                        setInput(new PageEditorInput(conflictingPage));

                        /* Force the editor to be dirty */
                        getDocumentProvider().getDocument(getEditorInput()).set(
                            getDocumentProvider().getDocument(getEditorInput()).get());

                        conflictDialogDisplayed = false;
                        break;
                    case PageConflictDialog.ID_MERGE:
                        newPage = new XWikiPage(conflictingPage.getData().toRawMap());
                        newPage.setContent(String.format(">>>>>>>LOCAL>>>>>>>>%s\n\n\n>>>>>>>REMOTE>>>>>>>>\n%s",
                            currentPage.getData().getContent(), conflictingPage.getData().getContent()));
                        dataManager.clearConflictingStatus(newPage.getId());
                        setInput(new PageEditorInput(new XWikiEclipsePage(dataManager, newPage)));

                        /* Force the editor to be dirty */
                        getDocumentProvider().getDocument(getEditorInput()).set(
                            getDocumentProvider().getDocument(getEditorInput()).get());

                        conflictDialogDisplayed = false;
                        break;
                    default:
                        break;
                }
            } catch (XWikiEclipseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    @Override
    public void setFocus()
    {
        PageEditorInput input = (PageEditorInput) getEditorInput();
        XWikiEclipsePage page = input.getPage();

        NotificationManager.getDefault().fireCoreEvent(CoreEvent.Type.PAGE_SELECTED, this, page);

        super.setFocus();
    }

    public void handleCoreEvent(CoreEvent event)
    {
        PageEditorInput input = (PageEditorInput) getEditorInput();
        XWikiEclipsePage page = input.getPage();
        String objectPageId = null;
        DataManager dataManager = (DataManager) event.getSource();

        switch (event.getType()) {
            case OBJECT_STORED:
                /*
                 * When objects are modified, the version number of the page is incremented. Here, we retrieve the
                 * current page. If the version numbers are not equal and the editor is not dirty then it means that an
                 * object of the page has been modified, but not the page content. So we basically update the page
                 * content. If the editor is dirty then we do not do nothing.
                 */
                XWikiEclipseObject object = (XWikiEclipseObject) event.getData();
                objectPageId = object.getData().getPageId();
                break;
            case OBJECT_REMOVED:
                objectPageId = (String) event.getData();
                break;
        }

        try {
            if (page.getDataManager().equals(dataManager) && page.getData().getId().equals(objectPageId))
                if (!isDirty()) {

                    XWikiEclipsePage newPage = page.getDataManager().getPage(page.getData().getId());

                    if (page.getData().getVersion() != newPage.getData().getVersion()) {

                        /*
                         * If we are here then the editor is not dirty and the page versions differ. So we update the
                         * page being edited.
                         */

                        ISourceViewer sourceViewer = getSourceViewer();
                        if (sourceViewer != null) {
                            int caretOffset = sourceViewer.getTextWidget().getCaretOffset();
                            int topPixel = sourceViewer.getTextWidget().getTopPixel();
                            super.doSetInput(new PageEditorInput(newPage));
                            sourceViewer.getTextWidget().setCaretOffset(caretOffset);
                            sourceViewer.getTextWidget().setTopPixel(topPixel);
                            updateInfo();
                        }

                    }
                }
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XWikiEclipseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#adjustHighlightRange(int, int)
     */
    @Override
    protected void adjustHighlightRange(int offset, int length)
    {
        ISourceViewer viewer = getSourceViewer();
        if (viewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
            extension.exposeModelRange(new Region(offset, length));
        }
    }

    // TODO How to implement this in practice?
    public void setMessage(boolean error, String message, Image image)
    {
        IStatusLineManager statusline = getStatusLineManager();
        if (!error)
            statusline.setMessage(image, message);
        if (error)
            statusline.setErrorMessage(image, message);
    }

    /*
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#createSourceViewer(org.eclipse .swt.widgets.Composite,
     * org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    @Override
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
    {

        fAnnotationAccess = createAnnotationAccess();
        fOverviewRuler = createOverviewRuler(getSharedColors());

        ISourceViewer viewer =
            new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
        // ensure decoration support has been created and configured.
        getSourceViewerDecorationSupport(viewer);

        // Word Wrap functionality, added for the betterment of "double enter"
        // functionality.
        viewer.getTextWidget().setWordWrap(true);

        return viewer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class required)
    {
        if (IContentOutlinePage.class.equals(required)) {
            if (outline_page == null) {
                outline_page = new XwikiContentOutlinePage(getDocumentProvider(), this);
                if (getEditorInput() != null)
                    outline_page.setInput(getEditorInput());
            }
            return outline_page;
        }
        return super.getAdapter(required);
    }

    /*
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#editorContextMenuAboutToShow
     * (org.eclipse.jface.action.IMenuManager)
     */
    @Override
    protected void editorContextMenuAboutToShow(IMenuManager menu)
    {
        super.editorContextMenuAboutToShow(menu);
        addAction(menu, "ContentAssistProposal"); //$NON-NLS-1$
        // addAction(menu, "ContentAssistTip"); //$NON-NLS-1$
    }

    @Override
    public void doRevertToSaved()
    {
        super.doRevertToSaved();
        if (outline_page != null)
            outline_page.update();
    }

    @Override
    public void doSave(IProgressMonitor monitor)
    {
        super.doSave(monitor);
        if (outline_page != null)
            outline_page.update();
    }

    @Override
    public void doSaveAs()
    {
        super.doSaveAs();
        if (outline_page != null)
            outline_page.update();
    }

    @Override
    protected void createActions()
    {
        super.createActions();

        IAction a =
            new TextOperationAction(XwikiEditorMessages.getResourceBundle(),
                "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS); //$NON-NLS-1$
        a.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", a); //$NON-NLS-1$

        // a= new TextOperationAction(XwikiEditorMessages.getResourceBundle(),
        // "ContentAssistTip.", this,
        // ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
        // //$NON-NLS-1$
        // a.setActionDefinitionId(ITextEditorActionDefinitionIds.
        // CONTENT_ASSIST_CONTEXT_INFORMATION);
        // setAction("ContentAssistTip", a); //$NON-NLS-1$
    }

    public IDocument getDocument()
    {
        ISourceViewer viewer = getSourceViewer();
        if (viewer != null) {
            return viewer.getDocument();
        }
        return null;
    }

    public Object[] getRootElements()
    {
        return fReconcilingStrategy.getRootElements();
    }

    public Node getRootNode()
    {
        return fReconcilingStrategy.getRootNode();
    }

    public Node getLastRootNode()
    {
        return fReconcilingStrategy.getLastRootNode();
    }

    public int getCursorLine()
    {
        int line = -1;

        ISourceViewer sourceViewer = getSourceViewer();
        if (sourceViewer != null) {
            StyledText styledText = sourceViewer.getTextWidget();
            int caret = widgetOffset2ModelOffset(sourceViewer, styledText.getCaretOffset());
            IDocument document = sourceViewer.getDocument();
            if (document != null) {
                try {
                    line = document.getLineOfOffset(caret) + 1;
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    public List getVariables(int aLine)
    {
        return veloObjectModel.getVariables(aLine);
    }

    public int getLine(int offset)
    {
        int line;
        try {
            line = getDocument().getLineOfOffset(offset) + 1;
        } catch (BadLocationException e) {
            line = -1;
        }
        return line;
    }
}
