/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2019, Ondrej Fischer
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

package fluent.dsl.processor;

import fluent.dsl.*;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static javax.lang.model.util.ElementFilter.methodsIn;

public final class FluentDslModel {

    private final String packageName;
    private final String className;
    private final String methodName;
    private final String parameterName;
    private final String parameterType;
    private final String modifiers;
    private ExecutableElement method;
    private final Map<String, FluentDslModel> followers = new LinkedHashMap<>();

    public FluentDslModel(String packageName, String className, String methodName, String parameterName, String parameterType, String modifiers) {
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.modifiers = modifiers;
    }

    public String fullyQualifiedClassName() {
        return packageName() + "." + className();
    }

    public String packageName() {
        return packageName;
    }
    public String className() {
        return className;
    }
    public String annotation() {
        return nonTerminal() ? "@Start(\"Fluent sentence must be finished.\")" : "@End";
    }
    public String returnType() {
        return nonTerminal() ? className : method.getReturnType().toString();
    }
    public String methodName() {
        return methodName;
    }
    public String parameterName() {
        return parameterName;
    }
    public String parameterType() {
        return parameterType;
    }

    public Collection<FluentDslModel> followers() {
        return followers.values();
    }

    public boolean nonTerminal() {
        return method == null;
    }

    public ExecutableElement getMethod() {
        return method;
    }

    private static String cap(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String uncap(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static FluentDslModel parameterNode(String className, String methodName, String returnType, String variableName) {
        return new FluentDslModel("", className, methodName, variableName, returnType, "public");
    }

    public static FluentDslModel classNode(Element element) {
        Dsl dsl = element.getAnnotation(Dsl.class);
        String packageName = dsl.packageName().isEmpty() ? element.getEnclosingElement().toString() : dsl.packageName();
        String typeName = element.getSimpleName().toString();
        String className = dsl.className().isEmpty() ? typeName + "Dsl" : dsl.className();
        String methodName = dsl.factoryMethod();
        return new FluentDslModel(packageName, className, methodName, "impl", typeName, "static");
    }

    public static FluentDslModel keywordNode(Element annotation) {
        String methodName = annotation.getSimpleName().toString();
        return new FluentDslModel("", cap(methodName), methodName, "", "", "public");
    }

    public String modifiers() {
        return modifiers;
    }

    public static FluentDslModel model(Element element) {
        FluentDslModel model = classNode(element);
        for(ExecutableElement method : methodsIn(element.getEnclosedElements())) {
            FluentDslModel node = model;
            for (VariableElement parameter : method.getParameters()) {
                String returnType = parameter.asType().toString();
                String rawType = returnType.split("<")[0];
                String variableName = parameter.toString();
                String methodName = variableName;
                String className = cap(methodName) + rawType.substring(rawType.lastIndexOf('.') + 1);
                for(AnnotationMirror annotationMirror : parameter.getAnnotationMirrors()) {
                    Element annotation = annotationMirror.getAnnotationType().asElement();
                    if(nonNull(annotation.getAnnotation(Keyword.class))) {
                        node = node.followers.computeIfAbsent(annotation.getSimpleName().toString(), key -> keywordNode(annotation));
                    }
                    if(nonNull(annotation.getAnnotation(Prefix.class))) {
                        methodName = annotation.getSimpleName() + cap(methodName);
                    }
                    if(nonNull(annotation.getAnnotation(Name.class))) {
                        methodName = annotation.getSimpleName().toString();
                    }
                }
                String finalMethodName = methodName;
                node = node.followers.computeIfAbsent(className, key -> parameterNode(className, finalMethodName, returnType, variableName));
            }
            for(AnnotationMirror annotationMirror : method.getAnnotationMirrors()) {
                Element annotation = annotationMirror.getAnnotationType().asElement();
                if(nonNull(annotation.getAnnotation(Suffix.class))) {
                    node = node.followers.computeIfAbsent(annotation.getSimpleName().toString(), key -> keywordNode(annotation));
                }
            }
            node.method = method;
        }
        return model;
    }
}
