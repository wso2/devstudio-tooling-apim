package org.wso2.developerstudio.eclipse.artifact.model;

import java.io.File;

import org.wso2.developerstudio.eclipse.artifact.utils.CompositeApiConstants;
import org.wso2.developerstudio.eclipse.platform.core.exception.ObserverFailedException;
import org.wso2.developerstudio.eclipse.platform.core.project.model.ProjectDataModel;

public class CompositeApiModel extends ProjectDataModel {
	
	private File compositeApiProjectLocation;
	private String compositeApiProjectName;

	public String getCompositeApiProjectName() {
		return compositeApiProjectName;
	}

	public void setCompositeApiProjectName(String compositeApiProjectName) {
		this.compositeApiProjectName = compositeApiProjectName;
	}
	
	public void setCompositeApiProjectLocation(File compositeApiProjectLocation) {
		this.compositeApiProjectLocation=compositeApiProjectLocation;
	}
	
	public File getCompositeApiProjectLocation() {
		return compositeApiProjectLocation;
	}
	
	public Object getModelPropertyValue(String key) {
		Object modelPropertyValue = super.getModelPropertyValue(key);
		if (key.equals(CompositeApiConstants.WIZARD_OPTION_PROJECT_NAME)) {
			modelPropertyValue = getCompositeApiProjectName();
		}else if(key.equals(CompositeApiConstants.WIZARD_OPTION_PROJECT_LOCATION)){
			modelPropertyValue = getCompositeApiProjectLocation();
		}
		return modelPropertyValue;
	}
	
	public boolean setModelPropertyValue(String key, Object data)
			throws ObserverFailedException {
		boolean returnValue = super.setModelPropertyValue(key, data);
		if (key.equals(CompositeApiConstants.WIZARD_OPTION_PROJECT_LOCATION)) {
			setCompositeApiProjectLocation(new File(data.toString()));
		} else if (key.equals(CompositeApiConstants.WIZARD_OPTION_PROJECT_NAME)) {
			setCompositeApiProjectName(data.toString());
		}
		return returnValue;
	}

}
