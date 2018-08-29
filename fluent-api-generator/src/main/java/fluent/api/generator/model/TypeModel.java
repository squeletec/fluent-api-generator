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

package fluent.api.generator.model;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public final class TypeModel {

    private static final Map<String,String> PRIMITIVES = new HashMap<String, String>() {{
        put("boolean", "Boolean");
        put("byte", "Byte");
        put("short", "Short");
        put("int", "Integer");
        put("long", "Long");
        put("double", "Double");
    }};

    private final ModelFactory factory;
    private final TypeMirror typeMirror;
    private final TypeElement type;

    public TypeModel(ModelFactory factory, TypeMirror typeMirror, TypeElement type) {
        this.factory = factory;
        this.typeMirror = typeMirror;
        this.type = type;
    }

    public String wrapper() {
        return PRIMITIVES.getOrDefault(type.toString(), type.toString());
    }

    public String simpleName() {
        return type.getSimpleName().toString();
    }

    public String packageName() {
        return type == null ? "" : type.getEnclosingElement().toString();
    }

    public List<MethodModel> methods() {
        return type.getEnclosedElements().stream()
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(method -> method.getModifiers().contains(Modifier.PUBLIC))
                .filter(method -> !method.getModifiers().contains(Modifier.STATIC))
                .map(method -> factory.asMemberOf(type, method))
                .collect(toList());
    }

    public List<TypeModel> interfaces() {
        return type.getInterfaces().stream().map(factory::model).collect(toList());
    }

    public TypeModel superClass() {
        return factory.model(type.getSuperclass());
    }

    public List<TypeModel> parameters() {
        return type.getTypeParameters().stream().map(type1 -> factory.model(type1.asType())).collect(toList());
    }

    public boolean isMissing() {
        return typeMirror.getKind() == TypeKind.ERROR;
    }

    @Override
    public String toString() {
        return typeMirror.toString();
    }

    public boolean isPrimitive() {
        return typeMirror.getKind().isPrimitive() || type.getKind() == ElementKind.ENUM;
    }

    public boolean isSimple() {
        return isPrimitive() || "java.lang".equals(packageName());
    }

    public boolean isComplex() {
        return !isSimple();
    }

}
