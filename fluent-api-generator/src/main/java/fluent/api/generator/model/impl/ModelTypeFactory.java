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
import fluent.api.generator.processor.DefaultValueVisitor;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ModelTypeFactory implements ModelFactory {

    private final Types types;
    private final Elements elements;
    private final Filer filer;

    public ModelTypeFactory(Types types, Elements elements, Filer filer) {
        this.types = types;
        this.elements = elements;
        this.filer = filer;
    }

    @Override
    public TypeModel asMemberOf(TypeElement declaring, TypeElement member) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public VarModel asMemberOf(DeclaredType declaring, VariableElement member) {
        return variable(member, types.asMemberOf(declaring, member));
    }

    @Override
    public MethodModel asMemberOf(DeclaredType declaring, ExecutableElement member) {
        return method(member, (ExecutableType) types.asMemberOf(declaring, member));
    }

    @Override
    public MethodModel method(ExecutableElement executableElement, ExecutableType type) {
        return new ExecutableModel(executableElement, type, this);
    }

    @Override
    public VarModel variable(VariableElement variableElement, TypeMirror type) {
        return new VarModelImpl(variableElement, type, this);
    }

    @Override
    public TypeModel type(TypeMirror typeMirror) {
        return typeMirror.accept(new TypeVisitor<TypeModel, ModelFactory>() {
            @Override
            public TypeModel visit(TypeMirror type, ModelFactory factory) {
                return null;
            }

            @Override
            public TypeModel visit(TypeMirror type) {
                return null;
            }

            @Override
            public TypeModel visitPrimitive(PrimitiveType type, ModelFactory factory) {
                return new PrimitiveTypeModel(type);
            }

            @Override
            public TypeModel visitNull(NullType t, ModelFactory factory) {
                return null;
            }

            @Override
            public TypeModel visitArray(ArrayType type, ModelFactory factory) {
                return new ArrayTypeModel(type, factory);
            }

            @Override
            public TypeModel visitDeclared(DeclaredType type, ModelFactory factory) {
                return new DeclaredTypeModel(type, factory, elements, types);
            }

            @Override
            public TypeModel visitError(ErrorType type, ModelFactory factory) {
                return new ErrorTypeModel(type);
            }

            @Override
            public TypeModel visitTypeVariable(TypeVariable type, ModelFactory factory) {
                return new TypeVariableModel(type);
            }

            @Override
            public TypeModel visitWildcard(WildcardType t, ModelFactory factory) {
                return null;
            }

            @Override
            public TypeModel visitExecutable(ExecutableType t, ModelFactory factory) {
                return null;
            }

            @Override
            public TypeModel visitNoType(NoType type, ModelFactory factory) {
                return new NoTypeModel(type);
            }

            @Override
            public TypeModel visitUnknown(TypeMirror t, ModelFactory factory) {
                return null;
            }

            @Override
            public TypeModel visitUnion(UnionType t, ModelFactory factory) {
                return null;
            }

            @Override
            public TypeModel visitIntersection(IntersectionType t, ModelFactory factory) {
                return null;
            }
        }, this);
    }

    @Override
    public TemplateModel model() {
        return new JtwigTemplateModel(filer);
    }

    @Override
    public Object annotationValue(AnnotationValue value) {
        return new AnnotationValueVisitor<Object, Void>() {
            @Override
            public Object visit(AnnotationValue av, Void aVoid) {
                return av.accept(this, aVoid);
            }

            @Override
            public Object visit(AnnotationValue av) {
                return av.accept(this, null);
            }

            @Override
            public Object visitBoolean(boolean b, Void aVoid) {
                return b;
            }

            @Override
            public Object visitByte(byte b, Void aVoid) {
                return b;
            }

            @Override
            public Object visitChar(char c, Void aVoid) {
                return c;
            }

            @Override
            public Object visitDouble(double d, Void aVoid) {
                return d;
            }

            @Override
            public Object visitFloat(float f, Void aVoid) {
                return f;
            }

            @Override
            public Object visitInt(int i, Void aVoid) {
                return i;
            }

            @Override
            public Object visitLong(long i, Void aVoid) {
                return i;
            }

            @Override
            public Object visitShort(short s, Void aVoid) {
                return s;
            }

            @Override
            public Object visitString(String s, Void aVoid) {
                return s;
            }

            @Override
            public Object visitType(TypeMirror t, Void aVoid) {
                return type(t);
            }

            @Override
            public Object visitEnumConstant(VariableElement c, Void aVoid) {
                return variable(c);
            }

            @Override
            public Object visitAnnotation(AnnotationMirror a, Void aVoid) {
                return null;
            }

            @Override
            public Object visitArray(List<? extends AnnotationValue> vals, Void aVoid) {
                return vals.stream().map(a -> annotationValue(a)).collect(toList());
            }

            @Override
            public Object visitUnknown(AnnotationValue av, Void aVoid) {
                return null;
            }
        }.visit(value);
    }

    @Override
    public Map<String, Object> annotationValues(AnnotationMirror a) {
        Map<String, Object> values = new HashMap<>();
        a.getAnnotationType().asElement().getEnclosedElements().forEach(new DefaultValueVisitor(
                method -> {
                    if(nonNull(method.getDefaultValue())) {
                        values.put(method.getSimpleName().toString(), annotationValue(method.getDefaultValue()));
                    }
                }
        ));
        values.putAll(a.getElementValues().entrySet().stream().collect(toMap(
                e -> e.getKey().getSimpleName().toString(),
                e -> annotationValue(e.getValue())
        )));
        return values;
    }

    @Override
    public Map<String, Map<String, Object>> annotations(Element element) {
        return element.getAnnotationMirrors().stream().collect(toMap(
                a -> a.getAnnotationType().asElement().getSimpleName().toString(),
                this::annotationValues
        ));
    }

}
