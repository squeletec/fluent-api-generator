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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class ModelFactoryImpl implements ModelFactory {

    private final Types types;

    public ModelFactoryImpl(Types types) {
        this.types = types;
    }

    @Override
    public MethodModel model(ExecutableElement methodElement) {
        return new MethodModel(this, methodElement);
    }

    @Override
    public TypeModel model(TypeMirror typeMirror) {
        return new TypeModel(this, typeMirror, (TypeElement) types.asElement(typeMirror));
    }

    @Override
    public VarModel model(VariableElement variableElement) {
        return new VarModel(this, variableElement);
    }

    private Element generalMemberOf(TypeElement declaring, Element member) {
        Element element = types.asElement(types.asMemberOf(types.getDeclaredType(declaring), member));
        return element == null ? member : element;
    }

    @Override
    public TypeModel asMemberOf(TypeElement declaring, TypeElement member) {
        return model(types.asMemberOf(types.getDeclaredType(declaring), member));
    }

    @Override
    public VarModel asMemberOf(TypeElement declaring, VariableElement member) {
        return model((VariableElement) generalMemberOf(declaring, member));
    }

    @Override
    public MethodModel asMemberOf(TypeElement declaring, ExecutableElement member) {
        return model((ExecutableElement) generalMemberOf(declaring, member));
    }
}
