package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.StringInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editor.Models.TreeMember;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.editors.CompositeAPISwaggerEditor;
import org.wso2.developerstudio.eclipse.utils.file.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.swagger.parser.util.SwaggerDeserializationResult;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

public class CompositeApiSwaggerGenerator {
	
	public static void addNewResource (String template, List<String> httpVerbs) {
		IProject currentProject = getSelectedProject();
		IFile currentSwaggerFile = currentProject.getFolder("src").getFolder("main").getFile(currentProject.getName() + ".yaml");
		
		
		SwaggerDeserializationResult swaggerDeserialized = new SwaggerParser().readWithInfo(currentSwaggerFile.getLocation().toString(), null, true);
		Swagger swagger = swaggerDeserialized.getSwagger();
		
		Map<String, Path> pathMap = null;
		if (swagger.getPaths() == null) {
			pathMap = new HashMap<String, Path>();
		} else {
			pathMap = swagger.getPaths();
		}
		
		String newTemplate = template;
		Path newPath = new Path();
		
		for (String httpVerb : httpVerbs) {
			Operation op = new Operation();
			newPath.set(StringUtils.lowerCase(httpVerb), op);	
		}
		pathMap.put(newTemplate, newPath);
		
		swagger.setPaths(pathMap);
		
		updateSwaggerFile (currentSwaggerFile, swagger);
				
	}

	private static void updateSwaggerFile(IFile currentSwaggerFile, Swagger swagger) {
		File destFile = new File(getSelectedProject().getFolder("src").getFolder("main").getLocation().toFile(), currentSwaggerFile.getName());
		try {
			String prettyJson = Yaml.pretty().writeValueAsString(swagger);
			FileUtils.writeContent(destFile, prettyJson);
			
			//IResource updateSwaggeFile = ResourcesPlugin.getWorkspace().getRoot().getFile(currentSwaggerFile.getLocation());
			//updateSwaggeFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			InputStream swaggerContentStream = new ByteArrayInputStream(prettyJson.getBytes("UTF-8"));
			page.getActiveEditor().getEditorInput().getAdapter(IFile.class).setContents(swaggerContentStream, IResource.FORCE, new NullProgressMonitor());
			
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
        
		
	}

	private static IProject getSelectedProject() {
		IProject selectedProject = null;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		selectedProject = page.getActiveEditor().getEditorInput().getAdapter(IFile.class).getProject();
		
		return selectedProject;
	}

}
