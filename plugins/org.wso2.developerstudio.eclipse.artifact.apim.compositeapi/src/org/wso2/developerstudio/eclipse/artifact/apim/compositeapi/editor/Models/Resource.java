package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models;

public class Resource extends Model {
	
	public Resource(String name, String parameters, String responses) {
		super(name, parameters, responses);
	}
	
	
	
	
	
	
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitResource(this, passAlongArgument);
	}

}
