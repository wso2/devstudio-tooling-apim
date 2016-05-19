package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Responsible for generating the "Composite API" perspective.
 */
public class CompositeAPIPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout perspectiveLayout) {
		defineAction(perspectiveLayout);
		defineLayout(perspectiveLayout);
	}

	public void defineAction(IPageLayout layout) {
		// layout.addShowViewShortcut(PROPERTIES_VIEW_ID);
		// layout.setFixed(true);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		//layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
	}

	public void defineLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		// Top left: Project Explorer
		IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.15f, editorArea);
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
		// topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft",
				IPageLayout.BOTTOM, 0.65f, "topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		// bottomLeft.addView(IPageLayout.ID_PROP_SHEET);

		IFolderLayout rightArea = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.65f, editorArea);
		rightArea.addView(IPageLayout.ID_PROP_SHEET);
	}
}
