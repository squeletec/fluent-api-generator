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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;


/**
 * Model of a method, that will be passed to the template, and can be used to drive generation of
 * derived java classes.
 */
public final class MethodModel {

    private static final Set<String> packages = new HashSet<String>() {{
        add("java.lang");
    }};

    private final ModelFactory factory;
    private final ExecutableElement methodSymbol;

    public MethodModel(ModelFactory factory, ExecutableElement methodSymbol) {
        this.factory = factory;
        this.methodSymbol = methodSymbol;
    }

    /**
     * Get name of the method (simple string).
     * @return Name of the method.
     */
    public String name() {
        return methodSymbol.getSimpleName().toString();
    }

    /**
     * Get type model representing the return type.
     * @return TypeModel
     */
    public TypeModel type() {
        return factory.model(methodSymbol.getReturnType());
    }

    /**
     * Get list of parameter (variable) models.
     * @return List of parameter (variable) models.
     */
    public List<VarModel> parameters() {
        return methodSymbol.getParameters().stream().map(var -> new VarModel(factory, var)).collect(toList());
    }

    public TypeModel declaringClass() {
        return factory.model(methodSymbol.getEnclosingElement().asType());
    }

    public boolean isConstructor() {
        return methodSymbol.getKind() == ElementKind.CONSTRUCTOR;
    }

    @Override
    public String toString() {
        return name();
    }

    public boolean isSimple(TypeModel model) {
        return true;
    }

    public boolean isComplex(TypeModel typeModel) {
        return !isSimple(typeModel);
    }

}
