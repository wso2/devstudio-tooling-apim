package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models;

public class API extends Model {
	
	public API(String title, String version, String description) {
		super(title, version, description);
	}
	
	
	
	
	
	
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitApi(this, passAlongArgument);
	}

}