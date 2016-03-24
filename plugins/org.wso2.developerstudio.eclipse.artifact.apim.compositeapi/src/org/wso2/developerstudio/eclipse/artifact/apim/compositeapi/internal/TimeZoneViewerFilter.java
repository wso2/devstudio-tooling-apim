package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.internal;

import java.util.TimeZone;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class TimeZoneViewerFilter extends ViewerFilter {

	private String pattern;

	public TimeZoneViewerFilter(String pattern) {
		this.pattern = pattern;
	}

	public boolean select(Viewer v, Object parent, Object element) {
		if (element instanceof TimeZone) {
			TimeZone zone = (TimeZone) element;
			return zone.getDisplayName().contains(pattern);
		} else {
			return true;
		}
	}

}
