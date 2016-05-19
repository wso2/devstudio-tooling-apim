package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.event.EventHandler;

import javax.annotation.PostConstruct;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Control;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.internal.Workbench;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.Activator;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.API;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.Model;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.Resource;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.TreeMember;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.UriTemplate;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.internal.communication.AddResourceRequest;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.internal.communication.ImportStoreApiRequest;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.utils.CompositeApiSwaggerParser;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.ui.wizard.APIImportMainWizard;
import org.eclipse.swt.widgets.Label;

public class compositeApiUIEditor extends ApplicationWindow implements EventHandler {
	private IEventBroker addResourceEB;
	protected TreeViewer treeViewer, treeViewer_1;
	protected Text text;
	protected TreeMemberLabelProvider labelProvider;
	protected GridData gridForTreeView;
	protected Model selectedM1,selectedM2;
	private IProject currentProject;
	IResource[] importedAPIs;
	private Composite composite1;
	private static final String PROJECT_EXPLORER_PARTID = "org.eclipse.ui.navigator.ProjectExplorer";
	
	public compositeApiUIEditor(IProject project) {
	    super(null);
	    currentProject = project;
	    addResourceEB = (IEventBroker) PlatformUI.getWorkbench().getService(IEventBroker.class);
	    addResourceEB.subscribe("newAPIResource", this);
	    addResourceEB.subscribe("importStoreApi", this);
	  }
	
	/*public static void main(String[] args) {
	    new compositeApiUIEditor().run();
	  }*/
	
	public void run() {
	    // Don't return from open() until window closes
	    setBlockOnOpen(true);

	    // Open the main window
	    open();

	    // Dispose the display
	    Display.getCurrent().dispose();
	  }

	  /**
	   * Configures the shell
	   * 
	   * @param shell
	   *            the shell
	   */
	  protected void configureShell(Shell shell) {
	    super.configureShell(shell);

	    // Set the title bar text and the size
	    shell.setText("UI Editor");
	    //shell.setSize(f, 400);
	  }

	protected TreeMember root;
	
	@PostConstruct
	public Control createContents(Composite parent) {
		composite1 = parent;
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		
		Link changeWorkspaceSettingsLink = createLink(composite, "Import APIs from store");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		
		treeViewer = new TreeViewer(composite, SWT.BORDER);
		treeViewer.setContentProvider(new TreeMemberContentProvider());
		treeViewer.setLabelProvider(new TreeMemberLabelProvider());
		Tree tree = treeViewer.getTree();
		gridForTreeView = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 8);
		tree.setLayoutData(gridForTreeView);
		if (isAPIsAvailableforComposing()){
			treeViewer.setInput(getInitalInput(parent));
			treeViewer.expandAll();
		} else {
			treeViewer.setInput(getNoApiInitalInput());
		}
		
		
		
		Button button = new Button(composite, SWT.NONE);
		button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 8));
		button.setText(">>");
		
		treeViewer_1 = new TreeViewer(composite, SWT.BORDER);
		treeViewer_1.setContentProvider(new TreeMemberContentProvider());
		treeViewer_1.setLabelProvider(new TreeMemberLabelProvider());
		treeViewer_1.setInput(getInitalInput1());
		treeViewer_1.expandAll();
		Tree tree_1 = treeViewer_1.getTree();
		tree_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 8));
		
		Button btnAddResource = new Button(composite, SWT.NONE);
		btnAddResource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new CompositeApiResourceEditor().run();
			}
		});
		btnAddResource.setText("Add Resource");
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setText("Move up");
		btnNewButton.addListener(SWT.Selection, new Listener() { 
			   @Override 
			   public void handleEvent(Event event) { 
			    performMoveOperation(-1); 
			   } 
			  }); 
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.setText("Move down");
		btnNewButton_1.addListener(SWT.Selection, new Listener() { 
			   @Override 
			   public void handleEvent(Event event) { 
			    performMoveOperation(1); 
			   } 
			  }); 
		
		Button btnNewButton_2 = new Button(composite, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (treeViewer_1.getSelection().isEmpty()) {
					return;
				}
				IStructuredSelection selection = (IStructuredSelection) treeViewer_1.getSelection();
				/* Tell the tree to not redraw until we finish
				 * removing all the selected children. */
			
				for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
					Model model = (Model) iterator.next();
					TreeMember parent = model.getParent();
					parent.remove(model);
				}
				refresh(treeViewer_1);
			}
		});
		btnNewButton_2.setText("Remove");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		//Link changeWorkspaceSettingsLink = createLink(composite, "Import APIs from store"); 
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		connectListeners();
		
		button.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		        switch (e.type) {
		        case SWT.Selection:
		          if (selectedM1 != null){
		        	  
		        	  TreeMember receivingItem;
		        	   if (treeViewer_1.getSelection().isEmpty()) {
		        		   receivingItem = root;
		        	   } else {
		        	       IStructuredSelection selection = (IStructuredSelection) treeViewer_1.getSelection();
		        	       Model selectedDomainObject = (Model) selection.getFirstElement();
		        	       if (!(selectedDomainObject instanceof TreeMember)) {
		        	    	   receivingItem = selectedDomainObject.getParent();
		        	       } else {
		        	    	   receivingItem = (TreeMember) selectedDomainObject;
		        	       }
		        	   }
		        	   receivingItem.add(selectedM1);
		        	   refresh(treeViewer_1);
		        	  
		        	 // treeViewer_1.add(selectedM2, selectedM1);
		        	  //treeViewer_1.refresh();
		        	  //treeViewer_1.getComparator().
		          }
		          break;
		        }
		      }
		    });
		return composite;
		
	  }

	private TreeMember getNoApiInitalInput() {
		root = new TreeMember();
		root.add(new TreeMember("No APIs imported"));
		return root;
		
	}

	public TreeMember getInitalInput(Composite composite) {
		root = new TreeMember();
		//loop for all imported swaggers
		//api[1] is swagger.getbasepath()
		
		// check if there are apis in Primary APIs folder
		// if yes call below to add them
		//else show import API wizard link in the ui
		for (IResource api : importedAPIs) {
			String apiLocation = api.getLocation().toString();
			root.add(CompositeApiSwaggerParser.parseApiTreefromSwagger(apiLocation));
		}
		
			
		
		
		
		
		/*TreeMember api1 = new TreeMember("test1");
		TreeMember api2 = new TreeMember("test2");
		TreeMember temp1 = new TreeMember("/test1");
		TreeMember temp2 = new TreeMember("/test2");
		TreeMember temp3 = new TreeMember("/test3");
		
		
		root.add(api1);
		root.add(api2);
		
		api1.add(temp1);
		api1.add(temp2);
		api2.add(temp3);
		
		temp1.add(new Resource("GET /test1", "", ""));
		temp1.add(new Resource("POST /test1", "", ""));
		temp1.add(new Resource("HEAD /test1", "", ""));
		temp2.add(new Resource("PUT /test2", "", ""));
		temp2.add(new Resource("DELETE /test2", "", ""));
		temp3.add(new Resource("GET /test3", "", ""));
		temp3.add(new Resource("POST /test3", "", ""));*/
		
		return root;
	}
	
	private boolean isAPIsAvailableforComposing() {   
		try {
			importedAPIs = currentProject.getFolder("src").getFolder("main").getFolder("Primary APIs").members();
			if (importedAPIs.length != 0) {
				return true;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	public TreeMember getInitalInput1() {
		root = new TreeMember();
		TreeMember compAPI = new TreeMember("CompAPI");
		root.add(compAPI);		
		return root;
	}
	
	protected void connectListeners() {
		//Model selectedModel1, selectedModel2;
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// if the selection is empty clear the label
				/*if(event.getSelection().isEmpty()) {
					text.setText("");
					return;
				}*/
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					StringBuffer toShow = new StringBuffer();
					
					selectedM1 = (Model) selection.getFirstElement();
					
					/*for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
						Object domain = (Model) iterator.next();
						String value = labelProvider.getText(domain);
						toShow.append(value);
						toShow.append(", ");
					}
					// remove the trailing comma space pair
					if(toShow.length() > 0) {
						toShow.setLength(toShow.length() - 2);
					}
					text.setText(toShow.toString());*/
				}
			}
		});
		
		treeViewer_1.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// if the selection is empty clear the label
				/*if(event.getSelection().isEmpty()) {
					text.setText("");
					return;
				}*/
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					//StringBuffer toShow = new StringBuffer();
					
					selectedM2 = (Model) selection.getFirstElement();
					
					/*for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
						Object domain = (Model) iterator.next();
						String value = labelProvider.getText(domain);
						toShow.append(value);
						toShow.append(", ");
					}
					// remove the trailing comma space pair
					if(toShow.length() > 0) {
						toShow.setLength(toShow.length() - 2);
					}
					text.setText(toShow.toString());*/
				}
			}
		});
		
		
	}
	/**
	  * Move the selected list items further up or down the list. 
	  *  
	  * @param direction -1 to move the selected items upwards, or 1 to move downwards. 
	  */ 
	 private void performMoveOperation(int direction) { 
		 
		 //another one
		 //http://svn.wso2.org/repos/wso2/tags/tools/ide/eclipse/carbon-studio/1.0.1.rc2/esb/native/org.wso2.carbonstudio.eclipse.esb/src/org/wso2/carbonstudio/eclipse/esb/editors/SequenceEditor.java
	   
	  if ((direction != 1) && (direction != -1)) { 
	   return; 
	  } 
	     
	  /* if nothing's selected, there's nothing to do */ 
	  ITreeSelection selection = (ITreeSelection) treeViewer_1.getSelection(); 
	  if (selection.size() == 0) { 
	   return; 
	  } 
	  
	  //move up action
	  if (direction == -1) {
		  IStructuredSelection moveupSelection = (IStructuredSelection) treeViewer_1
					.getSelection();
		  Model selectedObject = (Model)selection.getFirstElement();
		  int itemPosition = 0;
		  TreeMember selectedItemParent = selectedObject.getParent();
		  List<Model> selectedItemParentsChildren = selectedItemParent.getResources();
		  int currentItemPosition = selectedItemParentsChildren.indexOf(selectedObject);
		  Collections.swap(selectedItemParentsChildren, currentItemPosition, currentItemPosition - 1);
		  refresh(treeViewer_1);
	  } 
	  
	//move down action
	  if (direction == 1) {
		  IStructuredSelection moveupSelection = (IStructuredSelection) treeViewer_1
					.getSelection();
		  Model selectedObject = (Model)selection.getFirstElement();
		  int itemPosition = 0;
		  TreeMember selectedItemParent = selectedObject.getParent();
		  List<Model> selectedItemParentsChildren = selectedItemParent.getResources();
		  int currentItemPosition = selectedItemParentsChildren.indexOf(selectedObject);
		  Collections.swap(selectedItemParentsChildren, currentItemPosition, currentItemPosition + 1);
		  refresh(treeViewer_1);
	  } 
	   
	 } 
	 
	 /**
	  * Add new resource to composite API
	  * @param uriTemplate
	  * @param verbs
	  */
	 public void addResource(String uriTemplate, List<String> verbs) {
		 TreeMember receivingItem;
		 TreeMember temp1 = new TreeMember(uriTemplate);
		 for (String verb : verbs) {
			 temp1.add(new Resource(verb + " " + uriTemplate, "", "")); 
		 }
		  
  	   if (treeViewer_1.getSelection().isEmpty()) {
  		   receivingItem = root;
  	   } else {
  	       IStructuredSelection selection = (IStructuredSelection) treeViewer_1.getSelection();
  	       Model selectedDomainObject = (Model) selection.getFirstElement();
  	       if (!(selectedDomainObject instanceof TreeMember)) {
  	    	   receivingItem = selectedDomainObject.getParent();
  	       } else {
  	    	   receivingItem = (TreeMember) selectedDomainObject;
  	       }
  	   }
  	   receivingItem.add(temp1);
  	 refresh(treeViewer_1); 
		 
	 }
	 
	 private Link createLink(Composite composite, String text) { 
	        Link link= new Link(composite, SWT.NONE); 
	        link.setFont(composite.getFont()); 
	        link.setText("<A>" + text + "</A>");  //$NON-NLS-1$//$NON-NLS-2$ 
	        link.addSelectionListener(new SelectionListener() { 
	            public void widgetSelected(SelectionEvent e) { 
	                openLink(); 
	            } 
	            public void widgetDefaultSelected(SelectionEvent e) { 
	                openLink(); 
	            } 
	        }); 
	        return link; 
	    } 
	 
	    private void openLink() { 
	    	APIImportMainWizard wizard = new APIImportMainWizard();
			WizardDialog exportWizardDialog = new WizardDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), wizard);
			exportWizardDialog.open();
	    } 

	@Override
	public void handleEvent(org.osgi.service.event.Event brokerEvent) {
		Object eventObject = brokerEvent.getProperty("org.eclipse.e4.data");
		 try {
	            if (eventObject instanceof AddResourceRequest) {
	            	AddResourceRequest request = (AddResourceRequest)eventObject;
	            	TreeMember receivingItem;
	       		 TreeMember temp1 = new TreeMember(request.getUriTemplate());
	       		 for (String verb : request.getVerbs()) {
	       			 temp1.add(new TreeMember(verb + " " + request.getUriTemplate())); 
	       		 }
	       		  
	         	   if (treeViewer_1.getSelection().isEmpty()) {
	         		   receivingItem = root;
	         	   } else {
	         	       IStructuredSelection selection = (IStructuredSelection) treeViewer_1.getSelection();
	         	       Model selectedDomainObject = (Model) selection.getFirstElement();
	         	       if (!(selectedDomainObject instanceof TreeMember)) {
	         	    	   receivingItem = selectedDomainObject.getParent();
	         	       } else {
	         	    	   receivingItem = (TreeMember) selectedDomainObject;
	         	       }
	         	   }
	         	   receivingItem.add(temp1);
	         	  refresh(treeViewer_1);  
	            } else if (eventObject instanceof ImportStoreApiRequest) {
	            	createContents(composite1);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
	}
	
	private void refresh(TreeViewer treeViewer) {
		treeViewer.getTree().setRedraw(false);
		try {
			treeViewer.refresh();
			treeViewer.expandAll();
		} finally {
			treeViewer.getTree().setRedraw(true);
		}
	}
	  
}
