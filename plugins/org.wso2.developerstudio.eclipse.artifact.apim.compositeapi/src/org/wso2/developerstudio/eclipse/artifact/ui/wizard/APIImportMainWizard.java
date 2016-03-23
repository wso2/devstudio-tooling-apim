package org.wso2.developerstudio.eclipse.artifact.ui.wizard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

//import net.lingala.zip4j.core.ZipFile;
//import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
//import org.wso2.developerstudio.eclipse.esb.project.Activator;
//import org.wso2.developerstudio.eclipse.esb.project.connector.store.Connector;
//import org.wso2.developerstudio.eclipse.esb.project.control.graphicalproject.GMFPluginDetails;
//import org.wso2.developerstudio.eclipse.esb.project.control.graphicalproject.IUpdateGMFPlugin;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;
import org.wso2.developerstudio.eclipse.platform.ui.wizard.AbstractWSO2ProjectCreationWizard;

public class APIImportMainWizard extends AbstractWSO2ProjectCreationWizard {

	private static final int BUFFER_SIZE = 4096;
	private APIImportWizard importWizardPage;
	//private RemoveCloudConnectorWizardPage removeWizardPage;
	//private ImportRemoveSelectionWizardPage selectionPage;
	private static final String DIR_DOT_METADATA = ".metadata";
	private static final String DIR_CONNECTORS = ".Connectors";

	//private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	public APIImportMainWizard() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			if (selection != null) {
				//selectionPage = new ImportRemoveSelectionWizardPage(selection);
				importWizardPage = new APIImportWizard(selection);
				//removeWizardPage = new RemoveCloudConnectorWizardPage(selection);
				setWindowTitle("Import APIs");
			}
		}
	}

	/**
	 * Adding wizard pages
	 */
	public void addPages() {
		//addPage(selectionPage);
		addPage(importWizardPage);
		getPage("jkhjk");
		//addPage(removeWizardPage);
	}

	/**
	 * Importing connector zip file to Developer Studio either from fileSystem or connector store.
	 */
	public boolean performFinish() {
		if (importWizardPage.equals(getContainer().getCurrentPage())) {
			if (importWizardPage.getConnectorStore().getSelection()) {
				return performFinishStore();
			} else if (importWizardPage.getFileSystem().getSelection()) {
				return performFinishFileSystem();
			}
		} 
		/*else if (removeWizardPage.equals(getContainer().getCurrentPage())) {
			return performFinishRemove();
		}*/
		return false;
	}

	/**
	 * This method will download the connector zip file and extract it to the relevant location when user has selected
	 * import from connector store option.
	 */
    private boolean performFinishStore() {
        final List<String> selectedConnectors = new ArrayList<>();
        for (TableItem tableItem : importWizardPage.getTable().getItems()) {
            if (tableItem.getChecked()) {
                selectedConnectors.add((String) tableItem.getData());
            }
        }
        Job downloadJob = new Job("Downloading Connectors") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                int noOfConnectors = selectedConnectors.size();
                int count = 1;
                monitor.beginTask("Downloading connector", noOfConnectors);
                for (String connector : selectedConnectors) {
                    /*monitor.subTask(count + " of " + noOfConnectors + " : "
                            + connector.getAttributes().getOverview_name() + " connector");
                    String downloadLink = connector.getAttributes().getOverview_downloadlink();
                    downloadConnectorAndUpdateProjects(downloadLink);
                    monitor.worked(1);*/
                    count++;
                }
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        downloadJob.schedule();
        return true;
    }
	

	/**
	 * This method will extract connector zip file to the relevant location when user has selected import from file
	 * system option.
	 */
	private boolean performFinishFileSystem() {
		String source = importWizardPage.getCloudConnectorPath();
		try {
			String parentDirectoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()
					+ File.separator + DIR_DOT_METADATA + File.separator + DIR_CONNECTORS;
			File parentDirectory = new File(parentDirectoryPath);
			if (!parentDirectory.exists()) {
				parentDirectory.mkdir();
			}
			File file = new File(source);
			FileUtils.copyFileToDirectory(file, parentDirectory);

			//updateProjects(source);
		//} //catch (ZipException e) {
			//log.error("Error while extracting the connector zip : " + source, e);
		//} catch (CoreException e) {
			//log.error("Cannot refresh the project", e);
		} catch (IOException e) {
			//log.error("Error while copying the connector zip : " + source, e);
		}
		return true;
	}

	/**
	 * This method will remove selected connectors from the file system.
	 */
	/*private boolean performFinishRemove() {
		for (TableItem tableItem : removeWizardPage.getTable().getItems()) {
			if (tableItem.getChecked()) {
				String filePath = ((org.wso2.developerstudio.eclipse.esb.project.ui.wizard.Connector) tableItem
						.getData()).getConnectorFilePath();
				try {
					FileUtils.deleteDirectory(new File(filePath));
				} catch (IOException e) {
					log.error("Error while deleting the connector : " + filePath, e);
				}
			}
		}
		try {
			IUpdateGMFPlugin updateGMFPlugin = GMFPluginDetails.getiUpdateGMFPlugin();
			if (updateGMFPlugin != null) {
				updateGMFPlugin.updateOpenedEditors();
			}
			// Refresh the project.
			removeWizardPage.getSelectedProject().refreshLocal(IResource.DEPTH_INFINITE, null);
			return true;
		} catch (CoreException e) {
			log.error("Error while refreshing projects after deleting connectors ", e);
		}
		return false;
	}*/

	/*private void updateProjects(String source) throws ZipException, CoreException {
		ZipFile zipFile = new ZipFile(source);
		String[] segments = source.split(Pattern.quote(File.separator));
		String zipFileName = segments[segments.length - 1].split(".zip")[0];
		String parentDirectoryPath = storeWizardPage.getSelectedProject().getWorkspace().getRoot().getLocation()
				.toOSString()
				+ File.separator + DIR_DOT_METADATA + File.separator + DIR_CONNECTORS;
		File parentDirectory = new File(parentDirectoryPath);
		if (!parentDirectory.exists()) {
			parentDirectory.mkdir();
		}
		String zipDestination = parentDirectoryPath + File.separator + zipFileName;
		zipFile.getFile();
		zipFile.extractAll(zipDestination);
		IUpdateGMFPlugin updateGMFPlugin = GMFPluginDetails.getiUpdateGMFPlugin();
		if (updateGMFPlugin != null) {
			updateGMFPlugin.updateOpenedEditors();
		}
		/*
		 * Refresh the project.
		 */
		//storeWizardPage.getSelectedProject().refreshLocal(IResource.DEPTH_INFINITE, null);
	//}

	private boolean downloadConnectorAndUpdateProjects(String downloadLink) {
		String zipDestination = null;
		try {
			URL url = new URL(downloadLink);
			String[] segments = downloadLink.split("/");
			String zipFileName = segments[segments.length - 1];
			String parentDirectoryPath = importWizardPage.getSelectedProject().getWorkspace().getRoot().getLocation()
					.toOSString()
					+ File.separator + DIR_DOT_METADATA + File.separator + DIR_CONNECTORS;
			File parentDirectory = new File(parentDirectoryPath);
			if (!parentDirectory.exists()) {
				parentDirectory.mkdir();
			}
			zipDestination = parentDirectoryPath + File.separator + zipFileName;
			InputStream is = url.openStream();
			File targetFile = new File(zipDestination);
			targetFile.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = is.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			is.close();
			//updateProjects(zipDestination);
			return true;
		//} catch (ZipException e) {
			//log.error("Error while extracting the connector zip : " + zipDestination, e);
		//} catch (CoreException e) {
			//log.error("Cannot refresh the project", e);
		} catch (MalformedURLException malformedURLException) {
			//log.error("Malformed connector URL provided : " + downloadLink, malformedURLException);
		} catch (IOException e) {
			//log.error("Error while downloading connector : " + downloadLink, e);
		}
		return false;
	}

	public APIImportWizard getStoreWizardPage() {
		return importWizardPage;
	}

	public void setStoreWizardPage(APIImportWizard storeWizardPage) {
		this.importWizardPage = storeWizardPage;
	}

	/*public RemoveCloudConnectorWizardPage getRemoveWizardPage() {
		return removeWizardPage;
	}

	public void setRemoveWizardPage(RemoveCloudConnectorWizardPage removeWizardPage) {
		this.removeWizardPage = removeWizardPage;
	}*/

	@Override
	public IResource getCreatedResource() {
		return null;
	}
}

