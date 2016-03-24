package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.internal;

import java.util.Map;
import java.util.TimeZone;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class TimeZoneLabelProvider extends LabelProvider implements
		IStyledLabelProvider, IFontProvider {

	private final FontRegistry fr;
	private final ImageRegistry ir;

	public TimeZoneLabelProvider(ImageRegistry ir, FontRegistry fr) {
		this.ir = ir;
		this.fr = fr;
	}

	public Font getFont(Object element) {
		Font italic = fr.getItalic(JFaceResources.DEFAULT_FONT);
		return italic;
	}

	public String getText(Object element) {
		if (element instanceof Map) {
			return "Time Zones";
		} else if (element instanceof Map.Entry) {
			return ((Map.Entry) element).getKey().toString();
		} else if (element instanceof TimeZone) {
			return ((TimeZone) element).getID().split("/")[1];
		} else {
			return "Unknown type: " + element.getClass();
		}
	}

	public Image getImage(Object element) {
		if (element instanceof Map.Entry) {
			return ir.get("sample");
		} else if (element instanceof TimeZone) {
			return ir.get("sample");
		} else {
			return super.getImage(element);
		}
	}

	public StyledString getStyledText(Object element) {
		String text = getText(element);
		StyledString ss = new StyledString(text);
		if (element instanceof TimeZone) {
			int offset = -((TimeZone) element).getOffset(0);
			ss.append(" (" + offset / 3600000 + "h)",
					StyledString.DECORATIONS_STYLER);
		}
		return ss;
	}

}
