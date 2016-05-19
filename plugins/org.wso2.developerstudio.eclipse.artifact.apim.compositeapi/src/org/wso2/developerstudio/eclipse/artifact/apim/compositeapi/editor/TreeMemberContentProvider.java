package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor;

import java.util.Iterator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.DeltaEvent;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.IDeltaListener;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.Model;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.TreeMember;

public class TreeMemberContentProvider implements ITreeContentProvider, IDeltaListener {
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	
	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	* Notifies this content provider that the given viewer's input
	* has been switched to a different element.
	* <p>
	* A typical use for this method is registering the content provider as a listener
	* to changes on the new input (using model-specific means), and deregistering the viewer 
	* from the old input. In response to these change notifications, the content provider
	* propagates the changes to the viewer.
	* </p>
	*
	* @param viewer the viewer
	* @param oldInput the old input element, or <code>null</code> if the viewer
	*   did not previously have an input
	* @param newInput the new input element, or <code>null</code> if the viewer
	*   does not have an input
	*/
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null) {
			removeListenerFrom((TreeMember)oldInput);
		}
		if(newInput != null) {
			addListenerTo((TreeMember)newInput);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child box of the given box. */
	protected void removeListenerFrom(TreeMember box) {
		box.removeListener(this);
		for (Iterator iterator = box.getTreeMembers().iterator(); iterator.hasNext();) {
			TreeMember aBox = (TreeMember) iterator.next();
			removeListenerFrom(aBox);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child box of the given box. */
	protected void addListenerTo(TreeMember box) {
		box.addListener(this);
		for (Iterator iterator = box.getTreeMembers().iterator(); iterator.hasNext();) {
			TreeMember aBox = (TreeMember) iterator.next();
			addListenerTo(aBox);
		}
	}
	
	
	

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof TreeMember) {
			TreeMember box = (TreeMember)parentElement;
			return concat(box.getTreeMembers().toArray(), 
				box.getResources().toArray());
		}
		return EMPTY_ARRAY;
	}
	
	protected Object[] concat(Object[] object, Object[] more) {
		Object[] both = new Object[object.length + more.length];
		System.arraycopy(object, 0, both, 0, object.length);
		System.arraycopy(more, 0, both, object.length, more.length);		
		return both;
	}

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		if(element instanceof Model) {
			return ((Model)element).getParent();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {
		Object movingBox = ((Model)event.receiver()).getParent();
		viewer.refresh(movingBox, false);
	}

	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) {
		add(event);
	}

	

}

