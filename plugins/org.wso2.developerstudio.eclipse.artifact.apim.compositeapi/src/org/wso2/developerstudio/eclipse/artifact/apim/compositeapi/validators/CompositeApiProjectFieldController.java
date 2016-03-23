package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.validators;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.utils.CompositeApiConstants;
import org.wso2.developerstudio.eclipse.platform.core.exception.FieldValidationException;
import org.wso2.developerstudio.eclipse.platform.core.model.AbstractFieldController;
import org.wso2.developerstudio.eclipse.platform.core.project.model.ProjectDataModel;
import org.wso2.developerstudio.eclipse.platform.ui.validator.CommonFieldValidator;

public class CompositeApiProjectFieldController extends AbstractFieldController {
	
	private final static Pattern ADDITIONAL_FOLDERS_PATTERN = Pattern.compile("([\\/\\\\]repository[\\/\\\\]deployment[\\/\\\\]server)$");
	
	public void validate(String modelProperty, Object value, ProjectDataModel model)
	        throws FieldValidationException {
		if (modelProperty.equals(CompositeApiConstants.WIZARD_OPTION_PROJECT_NAME)) {
			CommonFieldValidator.validateProjectField(value);
		}else if (modelProperty.equals(CompositeApiConstants.WIZARD_OPTION_PROJECT_LOCATION)){
			if (value == null) {
				throw new FieldValidationException("Specified folder location is invalid");
			}
			String name = value.toString();
			Matcher additionlFolderMatcher = ADDITIONAL_FOLDERS_PATTERN.matcher(name);
			if (name.trim().equals("")||!additionlFolderMatcher.find()) {
				throw new FieldValidationException("Specified folder location is invalid");
			} else{
				File folderLocation = (File) value;
				if (!folderLocation.exists()||!folderLocation.isDirectory()) {
					throw new FieldValidationException("Specified folder doesn't exist");
				}	
			}
		}
	}
}