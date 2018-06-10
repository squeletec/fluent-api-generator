/*
 * Copyright © 2018 Ondrej Fischer. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY [LICENSOR] "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package fluent.api.generator.model;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
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
    final TypeElement type;
    private final String packageName;

    public TypeModel(ModelFactory factory, TypeElement type, String packageName) {
        this.factory = factory;
        this.type = type;
        this.packageName = packageName;
    }

    public TypeModel(ModelFactory factory, TypeElement type) {
        this(factory, type, type.getEnclosingElement().toString());
    }

    public String wrapper() {
        return PRIMITIVES.getOrDefault(type.toString(), type.toString());
    }

    public String simpleName() {
        return type.getSimpleName().toString();
    }

    public String packageName() {
        return packageName;
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
        return type.asType().getKind() == TypeKind.ERROR;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    public boolean isPrimitive() {
        return type.getKind() == ElementKind.ENUM;
    }

    public boolean isSimple() {
        return isPrimitive() || "java.lang".equals(packageName());
    }

    public boolean isComplex() {
        return !isSimple();
    }

}
