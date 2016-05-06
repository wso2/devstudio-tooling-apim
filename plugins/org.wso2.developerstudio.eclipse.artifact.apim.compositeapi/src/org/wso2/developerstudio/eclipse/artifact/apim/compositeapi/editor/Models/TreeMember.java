package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models;

import java.util.ArrayList;
import java.util.List;

public class TreeMember extends Model {
	protected List resources;
	protected List apis;
	protected List uriTemplates;
	protected List treeMembers;
	
	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();
	
	public TreeMember() {
		resources = new ArrayList();
		apis = new ArrayList();
		uriTemplates = new ArrayList();
		treeMembers = new ArrayList();
	}
	
	private static class Adder implements IModelVisitor {
		public void visitResource(Resource resource, Object argument) {
			((TreeMember) argument).addResource(resource);
		}
		
		public void visitApi(API api, Object argument) {
			((TreeMember) argument).addApi(api);
		}
		
		public void visitUriTemplate(UriTemplate template, Object argument) {
			((TreeMember) argument).addUriTemplate(template);
		}
		
		public void visitTreeMember(TreeMember member, Object argument) {
			((TreeMember) argument).addTreeMember(member);
		}
	}

	private static class Remover implements IModelVisitor {
		public void visitResource(Resource resource, Object argument) {
			((TreeMember) argument).removeResource(resource);
		}
		
		public void visitApi(API api, Object argument) {
			((TreeMember) argument).removeApi(api);
		}
		
		public void visitUriTemplate(UriTemplate template, Object argument) {
			((TreeMember) argument).removeUriTemplate(template);
		}
		
		public void visitTreeMember(TreeMember member, Object argument) {
			((TreeMember) argument).removeTreeMember(member);
		}
	}
	
	public TreeMember(String name) {
		this();
		this.name = name;
	}
	
	public List getResources() {
		return resources;
	}
	
	public List getApis() {
		return apis;
	}
	
	public List getUriTemplates() {
		return uriTemplates;
	}
	
	public List getTreeMembers() {
		return treeMembers;
	}
	
	protected void addResource(Resource resource) {
		resources.add(resource);
		resource.parent = this;
		fireAdd(resource);
	}
	
	protected void addApi(API api) {
		apis.add(api);
		api.parent = this;
		fireAdd(api);
	}
	
	protected void addUriTemplate(UriTemplate template) {
		uriTemplates.add(template);
		template.parent = this;
		fireAdd(template);
	}
	
	protected void addTreeMember(TreeMember item) {
		treeMembers.add(item);
		item.parent = this;
		fireAdd(item);
	}
	
	public void remove(Model toRemove) {
		toRemove.accept(remover, this);
	}
	
	protected void removeResource(Resource resource) {
		resources.remove(resource);
		resource.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(resource);
	}
	
	protected void removeApi(API api) {
		apis.remove(api);
		api.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(api);
	}
	
	protected void removeUriTemplate(UriTemplate template) {
		uriTemplates.remove(template);
		template.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(template);
	}
	
	protected void removeTreeMember(TreeMember member) {
		treeMembers.remove(member);
		member.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(member);
	}

	public void add(Model toAdd) {
		toAdd.accept(adder, this);
	}

	/** Answer the total number of items the
	 * receiver contains. */
	public int size() {
		//How to get the entire size
		return getTreeMembers().size() + getResources().size() + getApis().size() + getTreeMembers().size();
	}
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitTreeMember(this, passAlongArgument);
	}

}


