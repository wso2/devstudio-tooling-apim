package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.internal;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TimeZoneContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Map) {
			return ((Map) parentElement).entrySet().toArray();
		} else if (parentElement instanceof Map.Entry) {
			return getChildren(((Map.Entry) parentElement).getValue());
		} else if (parentElement instanceof Collection) {
			return ((Collection) parentElement).toArray();
		} else {
			return new Object[0];
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		} else {
			return new Object[0];
		}
	}

	@Override
	public Object getParent(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		if (element instanceof Map) {
			return !((Map) element).isEmpty();
		} else if (element instanceof Map.Entry) {
			return hasChildren(((Map.Entry) element).getValue());
		} else if (element instanceof Collection) {
			return !((Collection) element).isEmpty();
		} else {
			return false;
		}

	}

}
