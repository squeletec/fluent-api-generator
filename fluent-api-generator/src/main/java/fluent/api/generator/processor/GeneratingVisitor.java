/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2018, Ondrej Fischer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package fluent.api.generator.processor;

import fluent.api.generator.Templates;
import fluent.api.generator.model.ModelFactory;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.tools.StandardLocation.CLASS_PATH;
import static org.jtwig.JtwigModel.newModel;


/**
 * Generator of java code, which creates different models for different visited elements.
 * The annotation can be applied on method, constructor, but also class or interface.
 */
class GeneratingVisitor implements ElementVisitor<Void, TypeElement> {

    private static final Pattern PACKAGE = Pattern.compile("package\\s+([^;]+)\\s*;");
    private static final Pattern CLASS = Pattern.compile("(class|interface)\\s+(\\w+)");

    private final Filer filer;
    private final Set<String> generated = new HashSet<>();
    private final ModelFactory factory;
    private final EnvironmentConfiguration config = EnvironmentConfigurationBuilder.configuration()
            .render().withStrictMode(true)
            .and().build();

    GeneratingVisitor(Filer filer, ModelFactory factory) {
        this.filer = filer;
        this.factory = factory;
    }

    @Override
    public Void visit(Element e, TypeElement annotation) {
        return null;
    }

    @Override
    public Void visit(Element e) {
        return null;
    }

    @Override
    public Void visitPackage(PackageElement e, TypeElement annotation) {
        return null;
    }

    @Override
    public Void visitType(TypeElement e, TypeElement annotation) {
        return render(newModel().with("type", factory.type(e.asType())), e, annotation);
    }

    @Override
    public Void visitVariable(VariableElement e, TypeElement annotation) {
        return render(newModel().with("var", factory.variable(e)), e, annotation);
    }

    @Override
    public Void visitExecutable(ExecutableElement e, TypeElement annotation) {
        return render(newModel().with("method", factory.method(e)), e, annotation);
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement e, TypeElement annotation) {
        return null;
    }

    @Override
    public Void visitUnknown(Element e, TypeElement annotation) {
        return null;
    }

    private String readTemplate(String path) {
        if(path.startsWith("/")) {
            path = path.substring(1);
        }
        int split = path.lastIndexOf('/');
        String toPackage = path.substring(0, split).replace('/', '.');
        String toName = path.substring(split + 1);
        try {
            CharSequence charContent = filer.getResource(CLASS_PATH, toPackage, toName).getCharContent(true);
            return charContent.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Template: " + path + " not found on classpath.");
        }
    }

    private Void render(JtwigModel model, Element annotatedElement, TypeElement annotation) {
        annotation.getEnclosedElements().forEach(new DefaultValueVisitor(
                method -> model.with(method.getSimpleName().toString(), method.getDefaultValue().getValue())
        ));
        annotatedElement.getAnnotationMirrors().stream().filter(
                mirror -> mirror.getAnnotationType().asElement().equals(annotation)
        ).forEach(mirror -> mirror.getElementValues().forEach(
                (name, value) -> model.with(name.getSimpleName().toString(), value.getValue())
        ));
        for(String path : annotation.getAnnotation(Templates.class).value()) {
            String source = JtwigTemplate.inlineTemplate(readTemplate(path), config).render(model.with("templatePath", path));
            Matcher packageName = PACKAGE.matcher(source);
            Matcher className = CLASS.matcher(source);
            if(packageName.find() && className.find()) {
                String sourcePath = packageName.group(1) + "." + className.group(2);
                if(generated.add(sourcePath)) {
                    try(Writer output = filer.createSourceFile(sourcePath).openWriter()) {
                        output.append(source);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

}
