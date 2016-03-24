package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.internal;

import java.util.TimeZone;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class TimeZoneViewerComparator extends ViewerComparator {

	public int compare(Viewer viewer, Object o1, Object o2) {
		int compare;
		if (o1 instanceof TimeZone && o2 instanceof TimeZone) {
			compare = ((TimeZone) o2).getOffset(System.currentTimeMillis())
					- ((TimeZone) o1).getOffset(System.currentTimeMillis());
		} else {
			compare = o1.toString().compareTo(o2.toString());
		}
		boolean reverse = Boolean.parseBoolean(String.valueOf(viewer
				.getData("REVERSE")));
		return reverse ? -compare : compare;
	}

}
