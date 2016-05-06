package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.utils;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.Activator;
import org.wso2.developerstudio.eclipse.utils.template.TemplateUtil;

public class CompositeApiTemplateUtils extends TemplateUtil {

    private static CompositeApiTemplateUtils instance;

    protected Bundle getBundle() {
        return Platform.getBundle(Activator.PLUGIN_ID);
    }

    public static CompositeApiTemplateUtils getInstance() {
        if (instance == null) {
            instance = new CompositeApiTemplateUtils();
        }
        return instance;
    }

}
