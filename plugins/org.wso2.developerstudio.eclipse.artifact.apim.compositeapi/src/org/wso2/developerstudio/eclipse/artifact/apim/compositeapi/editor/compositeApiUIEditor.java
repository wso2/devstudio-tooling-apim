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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Control;
import org.eclipse.core.resources.IMarker;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IGotoMarker;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.API;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.Model;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.Resource;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.TreeMember;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.UriTemplate;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.internal.communication.AddResourceRequest;

public class compositeApiUIEditor extends ApplicationWindow implements EventHandler{
	private IEventBroker addResourceEB;
	protected TreeViewer treeViewer, treeViewer_1;
	protected Text text;
	protected TreeMemberLabelProvider labelProvider;
	protected Model selectedM1,selectedM2;
	
	public compositeApiUIEditor() {
	    super(null);
	    addResourceEB = (IEventBroker) PlatformUI.getWorkbench().getService(IEventBroker.class);
	    addResourceEB.subscribe("newAPIResource", this);
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
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());
		
		
		
		treeViewer = new TreeViewer(composite, SWT.BORDER);
		treeViewer.setContentProvider(new TreeMemberContentProvider());
		treeViewer.setLabelProvider(new TreeMemberLabelProvider());
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();
		Tree tree = treeViewer.getTree();
		FormData fd_tree = new FormData();
		fd_tree.bottom = new FormAttachment(100, -43);
		fd_tree.left = new FormAttachment(0, 27);
		fd_tree.right = new FormAttachment(0, 172);
		tree.setLayoutData(fd_tree);
		
		treeViewer_1 = new TreeViewer(composite, SWT.BORDER);
		treeViewer_1.setContentProvider(new TreeMemberContentProvider());
		treeViewer_1.setLabelProvider(new TreeMemberLabelProvider());
		treeViewer_1.setInput(getInitalInput1());
		treeViewer_1.expandAll();
		Tree tree_1 = treeViewer_1.getTree();
		fd_tree.top = new FormAttachment(tree_1, 0, SWT.TOP);
		FormData fd_tree_1 = new FormData();
		fd_tree_1.bottom = new FormAttachment(100, -43);
		fd_tree_1.top = new FormAttachment(0, 14);
		tree_1.setLayoutData(fd_tree_1);
		
		Button button = new Button(composite, SWT.NONE);
		fd_tree_1.left = new FormAttachment(button, 51);
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(0, 147);
		fd_button.right = new FormAttachment(100, -365);
		button.setLayoutData(fd_button);
		button.setText(">>");
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		fd_tree_1.right = new FormAttachment(100, -172);
		FormData fd_btnNewButton = new FormData();
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("Move up");
		btnNewButton.addListener(SWT.Selection, new Listener() { 
			   @Override 
			   public void handleEvent(Event event) { 
			    performMoveOperation(-1); 
			   } 
			  }); 
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		fd_btnNewButton.bottom = new FormAttachment(btnNewButton_1, -20);
		fd_btnNewButton.left = new FormAttachment(btnNewButton_1, 0, SWT.LEFT);
		FormData fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.left = new FormAttachment(0, 503);
		fd_btnNewButton_1.top = new FormAttachment(0, 180);
		btnNewButton_1.setLayoutData(fd_btnNewButton_1);
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
				treeViewer_1.getTree().setRedraw(false);
				for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
					Model model = (Model) iterator.next();
					TreeMember parent = model.getParent();
					parent.remove(model);
				}
				treeViewer_1.getTree().setRedraw(true);
			}
		});
		FormData fd_btnNewButton_2 = new FormData();
		fd_btnNewButton_2.top = new FormAttachment(btnNewButton_1, 28);
		fd_btnNewButton_2.left = new FormAttachment(btnNewButton, 0, SWT.LEFT);
		btnNewButton_2.setLayoutData(fd_btnNewButton_2);
		btnNewButton_2.setText("Remove");
		
		Button btnAddResource = new Button(composite, SWT.NONE);
		btnAddResource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new CompositeApiResourceEditor().run();
			}
		});
		btnAddResource.setText("Add Resource");
		FormData fd_btnAddResource = new FormData();
		fd_btnAddResource.bottom = new FormAttachment(btnNewButton, -31);
		fd_btnAddResource.right = new FormAttachment(100, -26);
		btnAddResource.setLayoutData(fd_btnAddResource);
		
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
		        	   treeViewer_1.refresh();
		        	   treeViewer_1.expandAll();
		        	  
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

	public TreeMember getInitalInput() {
		root = new TreeMember();
		TreeMember api1 = new TreeMember("test1");
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
		temp3.add(new Resource("POST /test3", "", ""));
		
		return root;
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
		  treeViewer_1.refresh();
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
		  treeViewer_1.refresh();
	  } 
	   
	  /* 
	   * Fetch the indicies (into the "currentMembers" member) of the selected items. 
	   * For example, if the user selects the 2nd and 4th items in the list, 
	   * our array should be [2, 4]. To do this, we need to convert the ITreeSelection 
	   * that Eclipse gives us into a sorted array of indicies. 
	   */ 
	  /*ArrayList<Integer> selectedTopLevelIndicies = new ArrayList<Integer>(); 
	  ArrayList<Model> selectedMembers = new ArrayList<Model>(); 
	  Iterator<Object> iter = selection.iterator(); 
	  int i = 0; 
	  while (iter.hasNext()) { 
	   Object element = iter.next(); 
	   if (element instanceof Model) { 
		   Model member = (Model)element; 
	    if (member.level == 0) { 
	     selectedTopLevelIndicies.add(member.seq); 
	     i++; 
	    } 
	    selectedMembers.add(member); 
	   } 
	  } 
	   
	   convert from ArrayList<Integer> to sorted Integer[]  
	  Integer selectedIndicies[] = new Integer[i]; 
	  selectedTopLevelIndicies.toArray(selectedIndicies); 
	  Arrays.sort(selectedIndicies); 
	   
	   
	   * if our first item is at 0, and we're moving up, or our last item is at listSize -1,  
	   * and we're moving down, there's nothing to do. We can't move beyond the bounds of 
	   * the list. 
	    
	  int currentMemberSize = currentMembers.size(); 
	  if (((direction == -1) && (selectedIndicies[0] == 0)) || 
	   ((direction == 1) && (selectedIndicies[selectedIndicies.length - 1] == (currentMemberSize - 1)))) { 
	   return; 
	  } 
	 
	  
	   * The direction in which we're moving the files will dictate the order in which 
	   * we must traverse the list of files (if we go the wrong direction, list items 
	   * will "leapfrog" their neighbours, even if their neighbours are also moving. 
	    
	  int firstIndex, lastIndex; 
	  if (direction == 1) { 
	   firstIndex = selectedIndicies.length - 1; 
	   lastIndex = -1; 
	  } else { 
	   firstIndex = 0; 
	   lastIndex = selectedIndicies.length; 
	  } 
	   
	  
	   * Now we actually modify the content of "currentMembers". Starting at position 
	   * 'firstIndex', and decrementing by 'direction' until we reach 'lastIndex'. 
	    
	  int pos = firstIndex; 
	  while (pos != lastIndex) { 
	   int index = selectedIndicies[pos]; 
	   int id = currentMembers.get(index); 
	    
	    shuffle the item along  
	   currentMembers.remove(index); 
	   currentMembers.add(index + direction, id); 
	    
	    move to next selected file  
	   pos -= direction; 
	  } 
	   
	   redraw the tree with the modified content  
	  populateList(filesList); 
	   
	   
	   * Reset the selection so that the same elements are selected in the new tree. Naturaly 
	   * their sequence numbers have now changed. 
	    
	  for (TreeMember member: selectedMembers) { 
	   member.seq += direction; 
	  } 
	  StructuredSelection newSelection = new StructuredSelection(selectedMembers); 
	  filesList.setSelection(newSelection, true); */
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
  	   treeViewer_1.expandAll(); 
		 
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
	         	   treeViewer_1.expandAll();  
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
	}
	  
}
