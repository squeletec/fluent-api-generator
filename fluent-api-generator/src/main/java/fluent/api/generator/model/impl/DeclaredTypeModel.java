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

package fluent.api.generator.model.impl;

import fluent.api.generator.model.MethodModel;
import fluent.api.generator.model.ModelFactory;
import fluent.api.generator.model.TypeModel;
import fluent.api.generator.model.VarModel;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

import static java.util.stream.Collectors.toList;

class DeclaredTypeModel extends AbstractTypeModel {

    private final DeclaredType type;
    private final TypeElement element;
    private final ModelFactory factory;
    private final Elements elements;
    private final Types types;

    DeclaredTypeModel(DeclaredType type, ModelFactory factory, Elements elements, Types types) {
        this.type = type;
        this.element = (TypeElement) type.asElement();
        this.factory = factory;
        this.elements = elements;
        this.types = types;
    }

    @Override
    public String wrapper() {
        return toString();
    }

    @Override
    public String simpleName() {
        return element.getSimpleName().toString();
    }

    @Override
    public String packageName() {
        return element.getEnclosingElement().toString();
    }

    @Override
    public List<MethodModel> methods() {
        return elements.getAllMembers(element).stream()
                .filter(element -> element.getModifiers().contains(Modifier.PUBLIC) && !element.getModifiers().contains(Modifier.STATIC) && element instanceof ExecutableElement).map(ExecutableElement.class::cast)
                .map(method -> factory.asMemberOf(type, method))
                .collect(toList());
    }

    @Override
    public List<MethodModel> declaredMethods() {
        return element.getEnclosedElements().stream().filter(element -> (element.getKind() == ElementKind.METHOD)).map(ExecutableElement.class::cast)
                .map(method -> factory.asMemberOf(type, method)).collect(toList());
    }

    @Override
    public List<VarModel> fields() {
        return elements.getAllMembers(element).stream()
                .filter(element -> element.getModifiers().contains(Modifier.PUBLIC) && !element.getModifiers().contains(Modifier.STATIC) && element instanceof VariableElement).map(VariableElement.class::cast)
                .map(method -> factory.asMemberOf(type, method))
                .collect(toList());
    }

    @Override
    public List<TypeModel> interfaces() {
        return element.getInterfaces().stream().map(factory::type).collect(toList());
    }

    @Override
    public TypeModel superClass() {
        return factory.type(element.getSuperclass());
    }

    @Override
    public String raw() {
        return element.toString();
    }

    @Override
    public List<TypeModel> parameters() {
        return type.getTypeArguments().stream().map(factory::type).collect(toList());
    }

    @Override
    public boolean isSimple() {
        return isEnum() || "java.lang".equals(packageName());
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public boolean hasDefaultConstructor() {
        return elements.getAllMembers(element).stream().anyMatch(member -> member.getKind() == ElementKind.CONSTRUCTOR && member.getModifiers().contains(Modifier.PUBLIC) && ((ExecutableElement)member).getParameters().isEmpty());
    }

    @Override
    public boolean isPublic() {
        return element.getModifiers().contains(Modifier.PUBLIC);
    }

    @Override
    public boolean isSubclassOf(DeclaredType parent) {
        return types.isAssignable(type, parent);
    }

    @Override
    public boolean isEnum() {
        return element.getKind() == ElementKind.ENUM;
    }

}
