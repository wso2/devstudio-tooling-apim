package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.Resource;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.TreeMember;

public class TreeMemberLabelProvider extends LabelProvider {	
	private Map imageCache = new HashMap(11);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		Image icon = null;
		/*try{
		if (element instanceof TreeMember) {
			icon = new Image(null, new FileInputStream("icons/movingBox.gif"));
		} else if (element instanceof Resource) {
			icon = new Image(null, new FileInputStream("icons/book.gif"));
		} else {
			throw unknownElement(element);
		}
		} catch (IOException e){
			e.printStackTrace();
		}*/

		//obtain the cached image corresponding to the descriptor
		/*Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}*/
		return null;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof TreeMember) {
			if(((TreeMember)element).getName() == null) {
				return "Box";
			} else {
				return ((TreeMember)element).getName();
			}
		} else if (element instanceof Resource) {
			return ((Resource)element).getTitle();
		} else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

}

