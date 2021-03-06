/*
 * Copyright 2011 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.compiler.injection;

import grails.compiler.ast.AllArtefactClassInjector;
import grails.compiler.ast.AstTransformer;
import grails.util.PluginBuildSettings;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.SourceUnit;
import grails.plugins.GrailsPluginInfo;
import org.grails.build.plugins.GrailsPluginUtils;
import grails.plugins.metadata.GrailsPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Automatically annotates each class based on the plugin it originated from.
 *
 * @author Graeme Rocher
 * @since 2.0
 */
@AstTransformer
public class PluginAwareAstTransformer implements AllArtefactClassInjector {

    PluginBuildSettings pluginBuildSettings;

    public PluginAwareAstTransformer() {
        pluginBuildSettings = GrailsPluginUtils.getPluginBuildSettings();
    }

    public void performInjection(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        File sourcePath = new File(source.getName());
        try {
            String absolutePath = sourcePath.getCanonicalPath();
            if (pluginBuildSettings == null) {
                return;
            }

            GrailsPluginInfo info = pluginBuildSettings.getPluginInfoForSource(absolutePath);
            if (info == null) {
                return;
            }

            final ClassNode annotation = new ClassNode(GrailsPlugin.class);
            final List<?> list = classNode.getAnnotations(annotation);
            if (!list.isEmpty()) {
                return;
            }

            if (classNode.isAnnotationDefinition()) {
                return;
            }

            final AnnotationNode annotationNode = new AnnotationNode(annotation);
            annotationNode.addMember(grails.plugins.GrailsPlugin.NAME,
                    new ConstantExpression(info.getName()));
            annotationNode.addMember(grails.plugins.GrailsPlugin.VERSION,
                    new ConstantExpression(info.getVersion()));
            annotationNode.setRuntimeRetention(true);
            annotationNode.setClassRetention(true);

            classNode.addAnnotation(annotationNode);
        }
        catch (IOException e) {
            // ignore
        }
    }

    public void performInjection(SourceUnit source, ClassNode classNode) {
        performInjection(source, null, classNode);
    }

    @Override
    public void performInjectionOnAnnotatedClass(SourceUnit source, ClassNode classNode) {
        performInjection(source, null, classNode);
    }

    public boolean shouldInject(URL url) {
        return true;
    }
}
