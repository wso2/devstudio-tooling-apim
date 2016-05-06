package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models;

public interface IDeltaListener {
	public void add(DeltaEvent event);
	public void remove(DeltaEvent event);
}
