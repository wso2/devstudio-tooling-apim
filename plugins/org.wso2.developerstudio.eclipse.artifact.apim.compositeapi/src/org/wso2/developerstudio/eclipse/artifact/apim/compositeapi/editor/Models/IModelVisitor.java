package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models;

public interface IModelVisitor {
	public void visitTreeMember(TreeMember member, Object passAlongArgument);
	public void visitApi(API api, Object passAlongArgument);
	public void visitUriTemplate(UriTemplate template, Object passAlongArgument);
	public void visitResource(Resource resource, Object passAlongArgument);
}