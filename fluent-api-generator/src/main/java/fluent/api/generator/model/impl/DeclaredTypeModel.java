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

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.List;

import static java.util.stream.Collectors.toList;

class DeclaredTypeModel extends AbstractTypeModel {

    private final DeclaredType type;
    private final TypeElement element;
    private final ModelFactory factory;

    DeclaredTypeModel(DeclaredType type, ModelFactory factory) {
        this.type = type;
        this.element = (TypeElement) type.asElement();
        this.factory = factory;
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
        return element.getEnclosedElements().stream()
                .filter(element -> element.getModifiers().contains(Modifier.PUBLIC) && !element.getModifiers().contains(Modifier.STATIC) && element instanceof ExecutableElement).map(ExecutableElement.class::cast)
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
    public List<TypeModel> parameters() {
        return type.getTypeArguments().stream().map(factory::type).collect(toList());
    }

    @Override
    public boolean isSimple() {
        return element.getKind() == ElementKind.ENUM || "java.lang".equals(packageName());
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
