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

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import fluent.api.generator.Parameter;
import fluent.api.generator.model.ModelFactory;
import fluent.api.generator.model.TemplateModel;

import javax.lang.model.element.*;

import static java.util.Objects.nonNull;

public class ParameterScanner extends TreePathScanner<Void, TemplateModel> {

    private final Trees trees;
    private final ModelFactory factory;

    public ParameterScanner(Trees trees, ModelFactory factory) {
        this.trees = trees;
        this.factory = factory;
    }

    public void scan(Element e, TemplateModel model) {
        scan(trees.getPath(e), model);
    }

    private void tryAddParameter(Element element, TemplateModel model) {
        element.getAnnotationMirrors().forEach(annotation -> {
            Element annotationType = annotation.getAnnotationType().asElement();
            Parameter parameter = annotationType.getAnnotation(Parameter.class);
            if(nonNull(parameter)) {
                element.accept(new ElementVisitor<Void, String>() {
                    @Override public Void visit(Element e, String s) {
                        return null;
                    }

                    @Override public Void visit(Element e) {
                        return null;
                    }

                    @Override public Void visitPackage(PackageElement e, String s) {
                        return null;
                    }

                    @Override public Void visitType(TypeElement e, String s) {
                        model.with(s, factory.type(e.asType()));
                        return null;
                    }

                    @Override public Void visitVariable(VariableElement e, String s) {
                        model.with(s, factory.variable(e));
                        return null;
                    }

                    @Override public Void visitExecutable(ExecutableElement e, String s) {
                        model.with(s, factory.method(e));
                        return null;
                    }

                    @Override public Void visitTypeParameter(TypeParameterElement e, String s) {
                        return null;
                    }

                    @Override public Void visitUnknown(Element e, String s) {
                        return null;
                    }
                }, annotationType.getSimpleName().toString());
            }

        });
    }

    @Override
    public Void visitMethod(MethodTree tree, TemplateModel templateModel) {
        tryAddParameter(trees.getElement(getCurrentPath()), templateModel);
        return super.visitMethod(tree, templateModel);
    }

    @Override
    public Void visitAnnotatedType(AnnotatedTypeTree tree, TemplateModel templateModel) {
        tryAddParameter(trees.getElement(getCurrentPath()), templateModel);
        return super.visitAnnotatedType(tree, templateModel);
    }

    @Override
    public Void visitVariable(VariableTree tree, TemplateModel templateModel) {
        tryAddParameter(trees.getElement(getCurrentPath()), templateModel);
        return super.visitVariable(tree, templateModel);
    }

    @Override
    public Void visitClass(ClassTree classTree, TemplateModel templateModel) {
        tryAddParameter(trees.getElement(getCurrentPath()), templateModel);
        return super.visitClass(classTree, templateModel);
    }
}
