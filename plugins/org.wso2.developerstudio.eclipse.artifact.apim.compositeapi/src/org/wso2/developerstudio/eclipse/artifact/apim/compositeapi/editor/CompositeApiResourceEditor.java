package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.PlatformUI;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.internal.communication.AddResourceRequest;

public class CompositeApiResourceEditor extends ApplicationWindow{
	private Text text;
	private IEventBroker addAPIResourceEB;

	public CompositeApiResourceEditor() {
	    super(null);
	  }
	
	/*public static void main(String[] args) {
	    new CompositeApiResourceEditor().run();
	  }*/
	
	public void run() {
	    // Don't return from open() until window closes
	    setBlockOnOpen(true);

	    // Open the main window
	    open();
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
	    shell.setText("Add New Resources");
	    //shell.setSize(f, 400);
	  }
	
	@PostConstruct
	public Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());
		
		Label lblUriTemplate = new Label(composite, SWT.NONE);
		FormData fd_lblUriTemplate = new FormData();
		fd_lblUriTemplate.bottom = new FormAttachment(0, 62);
		fd_lblUriTemplate.top = new FormAttachment(0, 40);
		fd_lblUriTemplate.left = new FormAttachment(0, 26);
		fd_lblUriTemplate.right = new FormAttachment(0, 111);
		lblUriTemplate.setLayoutData(fd_lblUriTemplate);
		lblUriTemplate.setText("URI Template");
		
		text = new Text(composite, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(lblUriTemplate, 267, SWT.RIGHT);
		fd_text.top = new FormAttachment(0, 37);
		fd_text.left = new FormAttachment(lblUriTemplate, 32);
		text.setLayoutData(fd_text);
		
		Button btnGet = new Button(composite, SWT.CHECK);
		FormData fd_btnGet = new FormData();
		fd_btnGet.top = new FormAttachment(lblUriTemplate, 18);
		fd_btnGet.left = new FormAttachment(0, 22);
		btnGet.setLayoutData(fd_btnGet);
		btnGet.setText("GET");
		
		Button btnPost = new Button(composite, SWT.CHECK);
		btnPost.setText("POST");
		FormData fd_btnPost = new FormData();
		fd_btnPost.top = new FormAttachment(btnGet, 0, SWT.TOP);
		fd_btnPost.left = new FormAttachment(btnGet, 11);
		btnPost.setLayoutData(fd_btnPost);
		
		Button btnPut = new Button(composite, SWT.CHECK);
		btnPut.setText("PUT");
		FormData fd_btnPut = new FormData();
		fd_btnPut.top = new FormAttachment(btnGet, 0, SWT.TOP);
		fd_btnPut.left = new FormAttachment(text, 0, SWT.LEFT);
		btnPut.setLayoutData(fd_btnPut);
		
		Button btnDelete = new Button(composite, SWT.CHECK);
		btnDelete.setText("DELETE");
		FormData fd_btnDelete = new FormData();
		fd_btnDelete.top = new FormAttachment(btnGet, 0, SWT.TOP);
		fd_btnDelete.left = new FormAttachment(btnPut, 6);
		btnDelete.setLayoutData(fd_btnDelete);
		
		Button btnHead = new Button(composite, SWT.CHECK);
		btnHead.setText("HEAD");
		FormData fd_btnHead = new FormData();
		fd_btnHead.top = new FormAttachment(btnGet, 0, SWT.TOP);
		fd_btnHead.left = new FormAttachment(btnDelete, 6);
		btnHead.setLayoutData(fd_btnHead);
		
		Button btnOptions = new Button(composite, SWT.CHECK);
		btnOptions.setText("OPTIONS");
		FormData fd_btnOptions = new FormData();
		fd_btnOptions.top = new FormAttachment(btnGet, 0, SWT.TOP);
		fd_btnOptions.left = new FormAttachment(btnHead, 6);
		btnOptions.setLayoutData(fd_btnOptions);
		
		Button btnAddResource = new Button(composite, SWT.NONE);
		btnAddResource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String template = text.getText();
				List<String> verbs = new ArrayList<String>();
				
				if (btnGet.getSelection()){
					verbs.add("GET");
				}
				if (btnPost.getSelection()){
					verbs.add("POST");
				}
				if (btnPut.getSelection()){
					verbs.add("PUT");
				}
				if (btnDelete.getSelection()){
					verbs.add("DELETE");
				}
				if (btnHead.getSelection()){
					verbs.add("HEAD");
				}
				if (btnOptions.getSelection()){
					verbs.add("OPTIONS");
				}
				//call a method of other editor, send inputs of uri template and verbs
				close();
				if (addAPIResourceEB == null) {
					addAPIResourceEB = (IEventBroker) PlatformUI.getWorkbench().getService(IEventBroker.class);
                }
				addAPIResourceEB.send("newAPIResource",
                        new AddResourceRequest(template, verbs));
			}
		});
		FormData fd_btnAddResource = new FormData();
		fd_btnAddResource.top = new FormAttachment(btnHead, 40);
		fd_btnAddResource.right = new FormAttachment(100, -56);
		btnAddResource.setLayoutData(fd_btnAddResource);
		btnAddResource.setText("Add Resource");
		return composite;
		
	  }
	
	protected void connectListeners() {
		//Model selectedModel1, selectedModel2;
		
	}
}


