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

package fluent.api.generator.processor;

import javax.lang.model.element.*;
import java.util.function.Consumer;

public class DefaultValueVisitor implements Consumer<Element>, ElementVisitor<Void, Void> {

    private final Consumer<ExecutableElement> methodConsumer;

    public DefaultValueVisitor(Consumer<ExecutableElement> methodConsumer) {
        this.methodConsumer = methodConsumer;
    }

    @Override
    public void accept(Element element) {
        element.accept(this, null);
    }

    @Override
    public Void visit(Element e, Void aVoid) {
        return null;
    }

    @Override
    public Void visit(Element e) {
        return null;
    }

    @Override
    public Void visitPackage(PackageElement e, Void aVoid) {
        return null;
    }

    @Override
    public Void visitType(TypeElement e, Void aVoid) {
        return null;
    }

    @Override
    public Void visitVariable(VariableElement e, Void aVoid) {
        return null;
    }

    @Override
    public Void visitExecutable(ExecutableElement e, Void aVoid) {
        methodConsumer.accept(e);
        return null;
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement e, Void aVoid) {
        return null;
    }

    @Override
    public Void visitUnknown(Element e, Void aVoid) {
        return null;
    }
}
