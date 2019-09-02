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

import fluent.dsl.Keyword;

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

public final class NodeModel {

    public static final String Actions = "Actions";
    public static final String Verifications = "Verifications";

    private final String packageName;
    private final String className;
    private final String methodName;
    private final String parameterName;
    private final String parameterType;
    private ExecutableElement method;
    private final Map<Object, NodeModel> followers = new LinkedHashMap<>();

    public NodeModel(String packageName, String className, String methodName, String parameterName, String parameterType, ExecutableElement method) {
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.method = method;
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

    public Collection<NodeModel> followers() {
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

    public static NodeModel parameterNode(VariableElement element) {
        String variableName = element.getSimpleName().toString();
        String capName = cap(variableName);
        String returnType = element.asType().toString();
        return new NodeModel(
                "",
                capName + returnType.substring(returnType.lastIndexOf('.') + 1) + Integer.toHexString(element.hashCode()),
                uncap(element.getAnnotationMirrors().stream().filter(a -> nonNull(a.getAnnotationType().asElement().getAnnotation(Keyword.class))).map(a-> cap(a.getAnnotationType().asElement().getSimpleName().toString())).collect(Collectors.joining()) + capName),
                variableName,
                returnType,
                null
        );
    }

    public static NodeModel classNode(Element element, String suffix) {
        String className = element.getSimpleName().toString();
        return new NodeModel(
                element.getEnclosingElement().toString(),
                className + suffix,
                className + suffix,
                className + suffix,
                className,
                null
        );
    }

    public static NodeModel keywordNode(AnnotationMirror annotationMirror) {
        Element annotation = annotationMirror.getAnnotationType().asElement();
        Keyword keyword = annotation.getAnnotation(Keyword.class);
        String methodName = keyword.value().length() > 0 ? keyword.value() : annotation.getSimpleName().toString();
        return new NodeModel(
                "",
                methodName + Integer.toHexString(annotation.hashCode()),
                methodName,
                "",
                "",
                null
        );
    }

    public static NodeModel groupModel(String group) {
        return new NodeModel(
                "",
                group,
                group,
                group,
                group,
                null
        );
    }

    public static NodeModel createBddModel(Element element) {
        NodeModel model = classNode(element, "Bdd");
        model.followers.put(Actions, groupModel(Actions));
        model.followers.put(Verifications, groupModel(Verifications));
        for(ExecutableElement method : methodsIn(element.getEnclosedElements())) {
            NodeModel node = model.followers.get(method.getSimpleName().toString().contains("verification") ? Verifications : Actions);
            for (VariableElement parameter : method.getParameters()) {
                node = node.followers.computeIfAbsent(parameter, key -> parameterNode(parameter));
            }
            node.method = method;
        }
        return model;
    }

    public static NodeModel createDirectDslModel(Element element) {
        NodeModel model = classNode(element, "Dsl");
        for(ExecutableElement method : methodsIn(element.getEnclosedElements())) {
            NodeModel node = model;
            for (VariableElement parameter : method.getParameters()) {
                node = node.followers.computeIfAbsent(parameter, key -> parameterNode(parameter));
            }
            node.method = method;
        }
        return model;
    }

    public NodeModel follower(String actions) {
        return followers.get(actions);
    }

}
