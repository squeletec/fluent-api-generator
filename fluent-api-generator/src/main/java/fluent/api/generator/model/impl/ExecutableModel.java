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

import fluent.api.generator.model.*;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ExecutableType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class ExecutableModel implements MethodModel {

    private final ExecutableElement element;
    private final ExecutableType type;
    private final ModelFactory factory;

    public ExecutableModel(ExecutableElement element, ExecutableType type, ModelFactory factory) {
        this.element = element;
        this.type = type;
        this.factory = factory;
    }

    @Override
    public String name() {
        return element.getSimpleName().toString();
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public TypeModel type() {
        return factory.type(type.getReturnType());
    }

    @Override
    public String propertyName() {
        return TypeUtils.propertyName(name());
    }

    @Override
    public List<VarModel> parameters() {
        List<VarModel> result = new ArrayList<>(type.getParameterTypes().size());
        for(int i = 0; i < type.getParameterTypes().size(); i++) {
            result.add(factory.variable(element.getParameters().get(i), type.getParameterTypes().get(i)));
        }
        return result;
    }

    @Override
    public List<TypeModel> typeVariables() {
        return type.getTypeVariables().stream().map(factory::type).collect(Collectors.toList());
    }

    @Override
    public TypeModel declaringClass() {
        return factory.type(element.getEnclosingElement().asType());
    }

    @Override
    public boolean isConstructor() {
        return element.getKind() == ElementKind.CONSTRUCTOR;
    }

    @Override
    public boolean isStatic() {
        return element.getModifiers().contains(Modifier.STATIC);
    }

    @Override
    public boolean isPublic() {
        return element.getModifiers().contains(Modifier.PUBLIC);
    }

    @Override
    public boolean isFinal() {
        return element.getModifiers().contains(Modifier.FINAL);
    }

    @Override
    public Map<String, Map<String, Object>> annotations() {
        return element.getAnnotationMirrors().stream().collect(toMap(
                a -> a.getAnnotationType().asElement().getSimpleName().toString(),
                a -> a.getElementValues().entrySet().stream().collect(toMap(
                        e -> e.getKey().getSimpleName().toString(),
                        e -> factory.annotationValue(e.getValue())
                ))
        ));
    }
}
