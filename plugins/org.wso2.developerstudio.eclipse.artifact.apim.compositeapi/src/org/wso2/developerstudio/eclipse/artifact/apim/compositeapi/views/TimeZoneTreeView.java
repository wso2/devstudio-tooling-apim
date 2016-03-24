package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.views;

import java.net.URL;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.internal.TimeZoneComparator;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.internal.TimeZoneContentProvider;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.internal.TimeZoneLabelProvider;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.internal.TimeZoneSelectionListener;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.internal.TimeZoneViewerComparator;

public class TimeZoneTreeView extends ViewPart {
	private TreeViewer treeViewer;
	private TimeZoneSelectionListener selectionListener;

	public void dispose() {
		if (selectionListener != null) {
			getSite().getWorkbenchWindow().getSelectionService()
					.removeSelectionListener(selectionListener);
			selectionListener = null;
		}
		super.dispose();
	}

	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		ResourceManager rm = JFaceResources.getResources();
		LocalResourceManager lrm = new LocalResourceManager(rm, parent);

		ImageRegistry ir = new ImageRegistry(lrm);
		URL sample = getClass().getResource("/icons/sample.gif");
		ir.put("sample", ImageDescriptor.createFromURL(sample));

		FontRegistry fr = JFaceResources.getFontRegistry();

		treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(
				new TimeZoneLabelProvider(ir, fr)));
		treeViewer.setContentProvider(new TimeZoneContentProvider());
		treeViewer.setData("REVERSE", Boolean.TRUE);
		treeViewer.setComparator(new TimeZoneViewerComparator());
		treeViewer.setExpandPreCheckFilters(true);
		// treeViewer.setFilters(new ViewerFilter[] { new TimeZoneViewerFilter(
		// "GMT") });
		treeViewer.setInput(new Object[] { TimeZoneComparator.getTimeZones() });

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };

		// treeViewer.addDragSupport(operations, transferTypes,
		// new TreeViewDragListner(treeViewer));

		// treeViewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// Viewer viewer = event.getViewer();
		// Shell shell = viewer.getControl().getShell();
		// ISelection sel = viewer.getSelection();
		// Object selectedValue;
		// if (!(sel instanceof IStructuredSelection) || sel.isEmpty()) {
		// selectedValue = null;
		// } else {
		// selectedValue = ((IStructuredSelection) sel)
		// .getFirstElement();
		// if (selectedValue instanceof TimeZone) {
		// TimeZone timeZone = (TimeZone) selectedValue;
		// new TimeZoneDialog(shell, timeZone).open();
		// }
		// }
		// }
		//
		// });

		getSite().setSelectionProvider(treeViewer);

		selectionListener = new TimeZoneSelectionListener(treeViewer, getSite()
				.getPart());
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(selectionListener);

		hookContextMenu(treeViewer);

		// TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
		// | SWT.V_SCROLL);
		// int operations = DND.DROP_COPY | DND.DROP_MOVE;
		// Transfer[] transferTypes = new Transfer[] {
		// TextTransfer.getInstance() };
		// viewer.addDragSupport(operations, transferTypes, new MyDragListener(
		// viewer));
		// viewer.setContentProvider(new TableContentProvider());
		// viewer.setLabelProvider(new TableLabelProvider());
		// viewer.setInput(ContentProvider.INSTANCE.getModel());

	}

	private void hookContextMenu(Viewer viewer) {
		MenuManager manager = new MenuManager("#PopupMenu");
		Menu menu = manager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(manager, viewer);
	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}
}
