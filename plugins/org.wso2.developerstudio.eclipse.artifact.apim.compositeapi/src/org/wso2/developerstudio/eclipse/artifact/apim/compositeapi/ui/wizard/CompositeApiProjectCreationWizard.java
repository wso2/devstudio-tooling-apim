package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.jface.wizard.IWizardPage;
import org.wso2.developerstudio.eclipse.gmf.esb.ArtifactType;
import org.wso2.developerstudio.eclipse.maven.util.MavenUtils;
import org.wso2.developerstudio.eclipse.platform.ui.editor.Openable;
import org.wso2.developerstudio.eclipse.platform.ui.startup.ESBGraphicalEditor;
import org.wso2.developerstudio.eclipse.platform.ui.wizard.AbstractWSO2ProjectCreationWizard;
import org.wso2.developerstudio.eclipse.utils.file.FileUtils;
import org.wso2.developerstudio.eclipse.utils.project.ProjectUtils;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.model.CompositeApiModel;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.utils.CompositeApiConstants;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.utils.CompositeApiImageUtils;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.utils.CompositeApiTemplateUtils;
import org.wso2.developerstudio.eclipse.maven.util.MavenUtils;
import org.wso2.developerstudio.eclipse.platform.ui.wizard.AbstractWSO2ProjectCreationWizard;
import org.wso2.developerstudio.eclipse.utils.project.ProjectUtils;

public class CompositeApiProjectCreationWizard extends
		AbstractWSO2ProjectCreationWizard {
	private CompositeApiModel compositeApiModel;
	private IProject compositeAPIProject;
	private File pomfile;
	private IFile swaggerFile;
	//private File swaggerFile;
	private List<File> fileList = new ArrayList<File>();

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
			compositeAPIProject = createNewProject();
			pomfile = compositeAPIProject.getFile("pom.xml").getLocation().toFile();
			createPOM(pomfile,"pom");
			ProjectUtils.addNatureToProject(compositeAPIProject,false,CompositeApiConstants.PROJECT_NATURE);
			MavenUtils.updateWithMavenEclipsePlugin(pomfile,new String[] { },new String[] {CompositeApiConstants.PROJECT_NATURE});
			
			//Add definition files to the project
			addAPIDefinitionstoProject("api_definition.yaml", compositeApiModel.getCompositeApiProjectName() + ".yaml");
			addAPIDefinitionstoProject("composite_api.iflow",compositeApiModel.getCompositeApiProjectName() + ".iflow");
			
			swaggerFile = compositeAPIProject.getFolder("src").getFolder("main").getFile(new Path(compositeApiModel.getCompositeApiProjectName() + ".yaml"));
			//Creating the metadata file artifact.xml while creating the Analytics project. 
			//It will be hidden and users won't be able to see it via Eclipse.
			//AnalyticsProjectArtifactCreator artifact=new AnalyticsProjectArtifactCreator();
			IFile aritifactFile = compositeAPIProject.getFile("artifact.xml");
			//artifact.setArtifactFile(aritifactFile.getLocation().toFile());
			//artifact.toFile();
			getModel().addToWorkingSet(compositeAPIProject);
			
			//Refresh the project to show the changes. But still won't see the newly created project.
			compositeAPIProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			refreshDistProjects();

			// Setting the created file to be hidden so that users won't see it.
			if (aritifactFile.exists()) {
				aritifactFile.setHidden(true);
			}
			String groupId = getMavenGroupId(pomfile);

			if (compositeApiModel.getSelectedOption().equals(CompositeApiConstants.WIZARD_OPTION_NEW_PROJECT)) {				
				compositeAPIProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				refreshDistProjects();
				
			}else if(compositeApiModel.getSelectedOption().equals(CompositeApiConstants.WIZARD_OPTION_IMPORT_PROJECT)){
				//TODO : import composite api
				/*Map<File,AnalyticsEntryTypes> selectedArtifactList = new HashMap<File,AnalyticsEntryTypes>();
				selectedArtifactList = AnalyticsProjectUtils.deploymentServerContentProcessing(compositeApiModel.getAnalyticsProjectLocation().getPath());
				AnalyticsProjectUtils.createAnalyticsArtifacts(selectedArtifactList,compositeAPIProject,pomfile,artifactList,groupId);
				compositeAPIProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
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
			

			//Open swagger file in the editor
			if (fileList.size() > 0) {
				openEditor(fileList.get(0));
			}
		} catch (Exception e) {
			MessageDialog.openError(getShell(),
					"Error while creating the project", e.getMessage());
			return false;

		}
		return true;
	}
	
	public CompositeApiModel getCompositeApiModel() {
		return compositeApiModel;
	}

	public void setCompositeApiModel(CompositeApiModel compositeApiModel) {
		this.compositeApiModel = compositeApiModel;
	}

	public IProject getCompositeAPIProject() {
		return compositeAPIProject;
	}

	public void setCompositeAPIProject(IProject compositeAPIProject) {
		this.compositeAPIProject = compositeAPIProject;
	}

	public IResource getCreatedResource() {
		return compositeAPIProject;
	}
	
	private void addAPIDefinitionstoProject(String templateName, String fileName){
		File compositeApiTemplateFile;
		File destFile = new File(compositeAPIProject.getFolder("src").getFolder("main").getLocation().toFile(),
                fileName);
        try {
        	 compositeApiTemplateFile = new CompositeApiTemplateUtils().getResourceFile("templates" + File.separator
                         + templateName);
             
             String templateContent = FileUtils.getContentAsString(compositeApiTemplateFile);
             FileUtils.createFile(destFile, templateContent);
             fileList.add(destFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openEditor(File file) {
		try {
			refreshDistProjects();
			/*IFile dbsFile =
			                ResourcesPlugin.getWorkspace().getRoot()
			                               .getFileForLocation(Path.fromOSString(file.getAbsolutePath()));*/
			/*
			 * IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow(
			 * ).getActivePage(),dbsFile);
			 */
			
			String path = swaggerFile.getParent().getFullPath() + "/";
			String source = FileUtils.getContentAsString(file);
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().
			        getEditorRegistry().getDefaultEditor(file.getName());
			page.openEditor(new FileEditorInput(swaggerFile), desc.getId());
			/*Openable openable = ESBGraphicalEditor.getOpenable();
			String type = ArtifactType.API.getLiteral();
			openable.editorOpen(file.getName(), type, path, source);*/
		} catch (Exception e) {
			//log.error("Cannot open the editor", e);
		}
	}

}
