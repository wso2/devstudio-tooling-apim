package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor;

import java.io.StringWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class CompositeApiMultipageEditor extends MultiPageEditorPart {
	
	//public static final String ID = "compositeapi.editor.CompositeApiMultipageEditorID";

    /** The text editor used in page 0. */
    private compositeApiUIEditor editor;

    /** The index of the page containing the text editor */
    private int editorIndex = 0;

    /** The font chosen in page 1. */
    private Font font;

    /** The text widget used in page 2. */
    private StyledText text;

    /**
     * Creates a multi-page editor example.
     */
    public CompositeApiMultipageEditor() {
        super();
    }

    /**
     * Creates page 0 of the multi-page editor,
     * which contains a text editor.
     */
    void createPage0() {
            editor = new compositeApiUIEditor();
            setPageText(editorIndex, "Design"); //$NON-NLS-1$  
    }

    /**
     * Creates page 1 of the multi-page editor,
     * which allows you to change the font used in page 2.
     */
    void createPage1() {

        Composite composite = new Composite(getContainer(), SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        layout.numColumns = 2;

        Button fontButton = new Button(composite, SWT.NONE);
        GridData gd = new GridData(GridData.BEGINNING);
        gd.horizontalSpan = 2;
        fontButton.setLayoutData(gd);
        fontButton.setText("ChangeFont"); //$NON-NLS-1$

        fontButton.addSelectionListener(new SelectionAdapter() {
            @Override
			public void widgetSelected(SelectionEvent event) {
                setFont();
            }
        });

        int index = addPage(composite);
        setPageText(index, "Source"); //$NON-NLS-1$
    }


    /**
     * Creates the pages of the multi-page editor.
     */
    @Override
	protected void createPages() {
        createPage0();
        createPage1();
    }

    /**
     * Saves the multi-page editor's document.
     */
    @Override
	public void doSave(IProgressMonitor monitor) {
        getEditor(0).doSave(monitor);
    }

    /**
     * Saves the multi-page editor's document as another file.
     * Also updates the text for page 0's tab, and updates this multi-page editor's input
     * to correspond to the nested editor's.
     */
    @Override
	public void doSaveAs() {
        IEditorPart editor = getEditor(0);
        editor.doSaveAs();
        setPageText(0, editor.getTitle());
        setInput(editor.getEditorInput());
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method
     * checks that the input is an instance of <code>IFileEditorInput</code>.
     */
    @Override
	public void init(IEditorSite site, IEditorInput editorInput)
            throws PartInitException {
        /*if (!(editorInput instanceof IFileEditorInput))
            throw new PartInitException(MessageUtil.getString("InvalidInput")); *///$NON-NLS-1$
        super.init(site, editorInput);
    }

    @Override
	public boolean isSaveAsAllowed() {
        return true;
    }

    /**
     * Calculates the contents of page 2 when the it is activated.
     */
    @Override
	protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
    }

    /**
     * Sets the font related data to be applied to the text in page 2.
     */
    void setFont() {
        FontDialog fontDialog = new FontDialog(getSite().getShell());
        fontDialog.setFontList(text.getFont().getFontData());
        FontData fontData = fontDialog.open();
        if (fontData != null) {
            if (font != null)
                font.dispose();
            font = new Font(text.getDisplay(), fontData);
            text.setFont(font);
        }
    }

}