package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.internal.communication;

import java.util.List;

public class AddResourceRequest {
	private String uriTemplate;
	public String getUriTemplate() {
		return uriTemplate;
	}

	public void setUriTemplate(String uriTemplate) {
		this.uriTemplate = uriTemplate;
	}

	public List<String> getVerbs() {
		return verbs;
	}

	public void setVerbs(List<String> verbs) {
		this.verbs = verbs;
	}

	private List<String> verbs;
	
	public AddResourceRequest (String template, List<String> httpVerbs){
		uriTemplate = template;
		verbs = httpVerbs;
	}

}
