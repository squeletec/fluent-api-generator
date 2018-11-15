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
import fluent.api.generator.model.TypeModel;
import fluent.api.generator.model.VarModel;

import javax.lang.model.type.DeclaredType;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public abstract class AbstractTypeModel implements TypeModel {

    @Override
    public abstract String toString();

    @Override
    public List<MethodModel> methods() {
        return emptyList();
    }

    @Override
    public List<MethodModel> declaredMethods() {
        return emptyList();
    }

    @Override
    public List<VarModel> fields() {
        return emptyList();
    }

    @Override
    public List<TypeModel> interfaces() {
        return emptyList();
    }

    @Override
    public TypeModel superClass() {
        return null;
    }

    @Override
    public String raw() {
        return toString();
    }

    @Override
    public List<TypeModel> parameters() {
        return emptyList();
    }

    @Override
    public List<TypeModel> parameterVariables() {
        return parameters().stream().filter(TypeModel::isTypeVariable).collect(toList());
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public String declaration() {
        return toString();
    }

    @Override
    public boolean isTypeVariable() {
        return false;
    }

    @Override
    public boolean hasDefaultConstructor() {
        return false;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isSubclassOf(DeclaredType parent) {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

}
