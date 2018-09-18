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

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import java.util.HashMap;
import java.util.Map;

public class PrimitiveTypeModel extends AbstractTypeModel {

    private static final Map<TypeKind,String> PRIMITIVES = new HashMap<TypeKind, String>() {{
        put(TypeKind.BOOLEAN, "Boolean");
        put(TypeKind.BYTE, "Byte");
        put(TypeKind.SHORT, "Short");
        put(TypeKind.INT, "Integer");
        put(TypeKind.LONG, "Long");
        put(TypeKind.DOUBLE, "Double");
        put(TypeKind.FLOAT, "Float");
    }};


    private final PrimitiveType type;

    public PrimitiveTypeModel(PrimitiveType type) {
        this.type = type;
    }

    @Override
    public String wrapper() {
        return PRIMITIVES.get(type.getKind());
    }

    @Override
    public String simpleName() {
        return toString();
    }

    @Override
    public String packageName() {
        return "";
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
