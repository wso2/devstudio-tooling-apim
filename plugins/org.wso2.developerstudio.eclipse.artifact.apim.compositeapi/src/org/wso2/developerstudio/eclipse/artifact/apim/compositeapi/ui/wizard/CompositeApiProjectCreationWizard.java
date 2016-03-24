package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.ui.wizard;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.model.CompositeApiModel;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.utils.CompositeApiConstants;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.utils.CompositeApiImageUtils;
import org.wso2.developerstudio.eclipse.maven.util.MavenUtils;
import org.wso2.developerstudio.eclipse.platform.ui.wizard.AbstractWSO2ProjectCreationWizard;
import org.wso2.developerstudio.eclipse.utils.project.ProjectUtils;

public class CompositeApiProjectCreationWizard extends
		AbstractWSO2ProjectCreationWizard {
	private CompositeApiModel compositeApiModel;
	private IProject analyticsProject;
	private File pomfile;

	// private Map<File,AnalyticsEntryTypes> artifactList = new
	// HashMap<File,AnalyticsEntryTypes>();//TODO: add as enum

	public CompositeApiProjectCreationWizard() {
		this.compositeApiModel = new CompositeApiModel();
		setModel(this.compositeApiModel);
		setWindowTitle(CompositeApiConstants.WIZARD_WINDOW_TITLE);
		setDefaultPageImageDescriptor(CompositeApiImageUtils.getInstance()
				.getImageDescriptor("new-mediator-wizard.png"));
	}

	public boolean performFinish() {

		try {
			analyticsProject = createNewProject();
			pomfile = analyticsProject.getFile("pom.xml").getLocation()
					.toFile();
			createPOM(pomfile, "pom");
			ProjectUtils.addNatureToProject(analyticsProject, false,
					CompositeApiConstants.PROJECT_NATURE);
			MavenUtils.updateWithMavenEclipsePlugin(pomfile, new String[] {},
					new String[] { CompositeApiConstants.PROJECT_NATURE });

			// Creating the metadata file artifact.xml while creating the
			// Analytics project.
			// It will be hidden and users won't be able to see it via Eclipse.
			// AnalyticsProjectArtifactCreator artifact=new
			// AnalyticsProjectArtifactCreator();
			IFile aritifactFile = analyticsProject.getFile("artifact.xml");
			// artifact.setArtifactFile(aritifactFile.getLocation().toFile());
			// artifact.toFile();
			getModel().addToWorkingSet(analyticsProject);

			// Refresh the project to show the changes. But still won't see the
			// newly created project.
			analyticsProject.refreshLocal(IResource.DEPTH_INFINITE,
					new NullProgressMonitor());
			refreshDistProjects();

			// Setting the created file to be hidden so that users won't see it.
			if (aritifactFile.exists()) {
				aritifactFile.setHidden(true);
			}
			String groupId = getMavenGroupId(pomfile);
			if (compositeApiModel.getSelectedOption().equals(
					CompositeApiConstants.WIZARD_OPTION_NEW_PROJECT)) {
				analyticsProject.refreshLocal(IResource.DEPTH_INFINITE,
						new NullProgressMonitor());
				refreshDistProjects();
			} else if (compositeApiModel.getSelectedOption().equals(
					CompositeApiConstants.WIZARD_OPTION_IMPORT_PROJECT)) {
				// TODO : import composite api
				/*
				 * Map<File,AnalyticsEntryTypes> selectedArtifactList = new
				 * HashMap<File,AnalyticsEntryTypes>(); selectedArtifactList =
				 * AnalyticsProjectUtils
				 * .deploymentServerContentProcessing(compositeApiModel
				 * .getAnalyticsProjectLocation().getPath());
				 * AnalyticsProjectUtils
				 * .createAnalyticsArtifacts(selectedArtifactList
				 * ,analyticsProject,pomfile,artifactList,groupId);
				 * analyticsProject.refreshLocal(IResource.DEPTH_INFINITE, new
				 * NullProgressMonitor()); refreshDistProjects(); if
				 * (!artifactList.isEmpty()) { if
				 * (MessageDialog.openQuestion(getShell(),
				 * "Open file(s) in the Editor",
				 * "Do you like to open the file(s) in Developer Studio?")) {
				 * for (File artifactFromList : artifactList.keySet()) {
				 * super.openEditor(artifactFromList); } } }
				 */
			}
			try {
				PlatformUI
						.getWorkbench()
						.showPerspective(
								"org.wso2.developerstudio.eclipse.artifact.apim.compositeapi",
								PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow());
			} catch (WorkbenchException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			MessageDialog.openError(getShell(),
					"Error while creating the project", e.getMessage());
			return false;

		}
		return true;
	}

	/*
	 * public AnalyticsModel getAnalyticsProjectModel() { return
	 * compositeApiModel; }
	 * 
	 * public void setCepProjectModel(AnalyticsModel cepProjectModel) {
	 * this.compositeApiModel = cepProjectModel; }
	 */

	public IResource getCreatedResource() {
		return analyticsProject;
	}

}
