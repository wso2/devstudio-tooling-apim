package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.ui.wizard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.model.API;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.utils.CompositeAPIUtils;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;

public class APIImportWizard extends WizardPage {

	private Text txtAPIStorePath;
	private Text txtAPIStoreURL;
	private Text username;
	private Text password;
	private String apiStorePath;
	private IProject selectedProject;
	private List<API> apiList;
	private Table table;
	private Label apiStore;
	//private Button fileSystem;	
	private static final String DIR_DOT_METADATA = ".metadata";
	private static final String DIR_CACHE = ".cache";
	private static final String CONNECTOR_STORE_URL = "https://localhost:9443";
	
	//private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);

	protected APIImportWizard(IStructuredSelection selection) {
		super("import");
		setTitle("Import APIs from API Manager store");
		setDescription("Import APIs from API Manager store");
		IProject project = getProject(selection);
		if (project != null) {
			setSelectedProject(project);
		}
		apiList = new ArrayList<>();
	}
	
	public APIImportWizard(IProject project1) {
		super("import");
		setTitle("Import APIs from API Manager store");
		setDescription("Import APIs from API Manager store");
		//IProject project = getProject(selection);
		if (project1 != null) {
			setSelectedProject(project1);
		}
		apiList = new ArrayList<>();
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		final Shell shell= new Shell(SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(2, false));
		/*fileSystem = new Button(container, SWT.RADIO);
		fileSystem.setText("API location");
		fileSystem.setSelection(true);

		txtAPIStorePath = new Text(container, SWT.BORDER);
		GridData gd_txtPath1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtPath1.widthHint = 300;
		txtAPIStorePath.setLayoutData(gd_txtPath1);
		txtAPIStorePath.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent evt) {
				setCloudConnectorPath(txtAPIStorePath.getText());
				txtAPIStorePath.setFocus();
				int charcount = txtAPIStorePath.getCharCount();
				txtAPIStorePath.setSelection(charcount);
				validate();
			}
		});
		if (apiStorePath != null) {
			txtAPIStorePath.setText(apiStorePath);
		} else {
			setPageComplete(false);
		}

		final Button btnBrowse1 = new Button(container, SWT.NONE);
		btnBrowse1.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(getShell());
				String fileName = fileDlg.open();
				if (fileName != null) {
					txtAPIStorePath.setText(fileName);
				}
				validate();
			}
		});
		btnBrowse1.setText("Browse..");*/

		apiStore = new Label(container, SWT.NONE);
		apiStore.setText("API Store location");
		txtAPIStoreURL = new Text(container, SWT.BORDER);
		GridData gd_txtPath = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtPath.widthHint = 300;
		txtAPIStoreURL.setLayoutData(gd_txtPath);
		txtAPIStoreURL.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent evt) {
				setCloudConnectorPath(txtAPIStoreURL.getText());
				txtAPIStoreURL.setFocus();
				int charcount = txtAPIStoreURL.getCharCount();
				txtAPIStoreURL.setSelection(charcount);
				setErrorMessage(null);
				//((APIImportWizard) getWizard()).getRemoveWizardPage().setPageComplete(true);
				setPageComplete(true);
				// validate();
			}
		});
		
		Label usrnm = new Label(container, SWT.NONE);
		usrnm.setSize(300,40);
		usrnm.setLocation(SWT.BEGINNING, SWT.CENTER);
		usrnm.setText("Username");
		
		username = new Text(container, SWT.BORDER);
		GridData gd_txtusrname = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtPath.widthHint = 300;
		username.setLayoutData(gd_txtusrname);
		usrnm.setLayoutData(gd_txtusrname);
		username.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent evt) {
				setCloudConnectorPath(username.getText());
				username.setFocus();
				int charcount = username.getCharCount();
				username.setSelection(charcount);
				setErrorMessage(null);
				//((APIImportWizard) getWizard()).getRemoveWizardPage().setPageComplete(true);
				setPageComplete(true);
				// validate();
			}
		});
		
		Label pwd = new Label(container, SWT.NONE);
		pwd.setSize(300,40);
		pwd.setLocation(SWT.BEGINNING, SWT.CENTER);
		pwd.setText("Password");
		password = new Text(container, SWT.BORDER);
		GridData gd_txtpw = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtPath.widthHint = 300;
		password.setLayoutData(gd_txtpw);
		pwd.setLayoutData(gd_txtusrname);
		password.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent evt) {
				setCloudConnectorPath(password.getText());
				password.setFocus();
				int charcount = password.getCharCount();
				password.setSelection(charcount);
				setErrorMessage(null);
				//((APIImportWizard) getWizard()).getRemoveWizardPage().setPageComplete(true);
				setPageComplete(true);
				// validate();
			}
		});
		if (apiStorePath != null) {
			txtAPIStoreURL.setText(apiStorePath);
		} else {
			// setPageComplete(false);
		}
		//txtAPIStoreURL.setEnabled(false);
		
		txtAPIStoreURL.setText(CONNECTOR_STORE_URL);
		txtAPIStoreURL.setEnabled(true);
		
		

		//txtAPIStorePath.setEnabled(false);
		
		final Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.setEnabled(false);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.verticalSpan = 7;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 400;
		gridData.widthHint = 250;

		table = new Table(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setEnabled(false);
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				setErrorMessage(null);
				//((APIImportWizard) getWizard()).getRemoveWizardPage().setPageComplete(true);
				setPageComplete(true);
			}
		});
		TableColumn tc1 = new TableColumn(table, SWT.LEFT);
		TableColumn tc2 = new TableColumn(table, SWT.CENTER);
		tc1.setText("Name");
		tc2.setText("Version");
		tc1.setWidth(390);
		tc2.setWidth(120);
		table.setHeaderVisible(true);
		table.setLayoutData(gridData);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				listAPIs();
				container.forceFocus();
			}
		});
		btnBrowse.setText("Connect");
		btnBrowse.setEnabled(true);
		table.setEnabled(true);
		/*fileSystem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				txtAPIStorePath.setEnabled(true);
				btnBrowse1.setEnabled(true);

				txtAPIStoreURL.setText("");
				txtAPIStoreURL.setEnabled(false);
				btnBrowse.setEnabled(false);
				table.setEnabled(false);
			}
		});

		apiStore.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				txtAPIStoreURL.setText(CONNECTOR_STORE_URL);
				txtAPIStoreURL.setEnabled(true);
				btnBrowse.setEnabled(true);
				table.setEnabled(true);

				txtAPIStorePath.setEnabled(false);
				//btnBrowse1.setEnabled(false);
			}
		});*/
	}

	public static IProject getProject(Object obj){
		if (obj == null) {
			return null;
		}
		if (obj instanceof IResource) {
			return ((IResource) obj).getProject();
		} else if (obj instanceof IStructuredSelection) {
			return getProject(((IStructuredSelection) obj).getFirstElement());
		}
		return null;
	}

	private void validate() {
		if ((getCloudConnectorPath() == null || getCloudConnectorPath().equals(""))) {
			setErrorMessage("Please specify a connector path");
			setPageComplete(false);
			return;
		}
		setErrorMessage(null);
		//((APIImportWizard) getWizard()).getRemoveWizardPage().setPageComplete(true);
		setPageComplete(true);
	}
		
	/*
	 * List available connectors
	 */
	private void listAPIs() {
		//project becomes null in the flow where API import happens in project creation flow
	
		if(selectedProject == null){
			CompositeApiProjectCreationWizard creationWizard = new CompositeApiProjectCreationWizard();
			creationWizard.performFinish();
			this.setSelectedProject(creationWizard.getCompositeAPIProject());
		}
		
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			progressService.runInUI(PlatformUI.getWorkbench().getProgressService(), new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {					
					//try {
						String iconCacheDirPath = getSelectedProject().getWorkspace().getRoot().getLocation().toOSString()
								+ File.separator + DIR_DOT_METADATA + File.separator + DIR_CACHE;
						File iconCacheDir = new File(iconCacheDirPath);
						if (!iconCacheDir.exists()) {
							iconCacheDir.mkdir();
						}
						int page = 1;
                        monitor.beginTask("Fetching list of APIs", 1000);
                        monitor.subTask("Searching APIs in store : page " + page);
                        
                        //Retrieving APIs from store
                        List<API> tmpList = CompositeAPIUtils.getAPIsFromStore(txtAPIStoreURL.getText(), username.getText(), password.getText());
                        
                        //List<Connector> tmpList = ConnectorStore.getConnectorInfo(txtAPIStoreURL.getText(), page);
						if (tmpList != null && !tmpList.isEmpty()) {
                            apiList.addAll(tmpList);
                            /*monitor.worked(25);
                            ++page;
                            monitor.subTask("Searching connectors in store : page " + page);
                            tmpList = ConnectorStore.getConnectorInfo(txtAPIStoreURL.getText(), page);*/
						}
						//int workUnit = (1000 - (25*page))/connectorList.size();
						for (API api : apiList) {
                            monitor.subTask("Fetching details of " + api);
                            //String imageLocation = null;
                            TableItem item = new TableItem(table, SWT.NONE);
                            /*imageLocation = txtAPIStoreURL.getText()
                                    + connector.getAttributes().getImages_thumbnail();
                            String[] segments = imageLocation.split("/");
                            String imageFileName = segments[segments.length - 1];
                            try {
                                String imageFilePath = iconCacheDir + File.separator + imageFileName;
                                File imageFile = new File(imageFilePath);
                                if (!imageFile.exists()) {
                                    // Download the thumbnail image if it is not there in the filesystem.
                                    downloadThumbnailImage(imageLocation, imageFilePath);
                                }
                                Image image = new Image(Display.getDefault(), imageFilePath);
                                Image scaled = new Image(Display.getDefault(), 55, 50);
                                GC gc = new GC(scaled);
                                gc.setAntialias(SWT.ON);
                                gc.setInterpolation(SWT.HIGH);
                                gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0,
                                        55, 50);
                                gc.dispose();
                                image.dispose();
                                item.setImage(scaled);
                            } catch (IOException e) {
                                //log.error("Error while downloading " + imageFileName, e);
                            }*/
                            item.setText(new String[] { api.getName(),
                                    api.getVersion() });
                            item.setData(api);
                            monitor.worked(10);
                        }
                        monitor.done();
					//} catch (KeyManagementException | NoSuchAlgorithmException | IOException e1) {
						//log.error("Error while listing connectors", e1);
						//IStatus editorStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e1.getMessage());
						//ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Error while listing connectors", e1.getMessage(), editorStatus);
                        //monitor.done();
					//}
				}
			}, getSelectedProject().getWorkspace().getRoot());
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			//log.error("Error while listing connectors", e);
		}
	}
	
	/*
	 * Download the thumbnail image from the provided URL.
	 */
	private void downloadThumbnailImage(String location, String file) throws IOException {
		URL url = new URL(location);
		InputStream in = new BufferedInputStream(url.openStream());
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

		for (int i; (i = in.read()) != -1;) {
			out.write(i);
		}
		in.close();
		out.close();
	}
	
	@Override
	public IWizardPage getNextPage() {
		return null;
	}

	public String getCloudConnectorPath() {
		return apiStorePath;
	}

	public void setCloudConnectorPath(String cloudConnectorPath) {
		this.apiStorePath = cloudConnectorPath;
	}

	public IProject getSelectedProject() {
		return selectedProject;
	}

	public void setSelectedProject(IProject selectedProject) {
		this.selectedProject = selectedProject;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	/*public Button getConnectorStore() {
		return apiStore;
	}

	public void setConnectorStore(Button connectorStore) {
		this.apiStore = connectorStore;
	}

	public Button getFileSystem() {
		return fileSystem;
	}

	public void setFileSystem(Button fileSystem) {
		this.fileSystem = fileSystem;
	}*/
	
	public List<API> getAPIList() {
		return apiList;
	}

	public void setConnectorList(List<API> apiList) {
		this.apiList = apiList;
	}
}
