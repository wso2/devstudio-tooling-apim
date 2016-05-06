package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.utils;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

public class CompositeApiYamlContentDescriptor implements IContentDescriber {

	@Override
	public int describe(InputStream arg0, IContentDescription arg1)
			throws IOException {
		// TODO Auto-generated method stub
		return VALID;
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
