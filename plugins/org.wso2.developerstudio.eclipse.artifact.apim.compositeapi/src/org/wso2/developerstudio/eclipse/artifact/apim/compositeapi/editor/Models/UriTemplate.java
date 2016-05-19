package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models;

public class UriTemplate extends Model {
	
	public UriTemplate(String template) {
		super(template, null, null);
	}
	
	
	
	
	
	
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitUriTemplate(this, passAlongArgument);
	}

}
