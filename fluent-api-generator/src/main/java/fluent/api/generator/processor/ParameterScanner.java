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

import com.sun.source.tree.*;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import fluent.api.generator.Parameter;
import fluent.api.generator.model.ModelFactory;
import fluent.api.generator.model.TemplateModel;

import javax.lang.model.element.*;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class ParameterScanner extends TreePathScanner<Void, TemplateModel> {

    private final Trees trees;
    private final ModelFactory factory;

    public ParameterScanner(Trees trees, ModelFactory factory) {
        this.trees = trees;
        this.factory = factory;
    }

    public void scan(Element e, TemplateModel model) {
        TreePath path = trees.getPath(e);
        if(nonNull(path)) {
            scan(path, model);
        }
    }

    private void tryAddParameter(Element annotatedElement, Stream<Element> annotationTypes, TemplateModel model) {
        annotationTypes.forEach(annotationType -> {
            Parameter parameter = annotationType.getAnnotation(Parameter.class);
            if(nonNull(parameter)) {
                annotatedElement.accept(new ElementVisitor<Void, String>() {
                    private Void add(String s, Object o) {
                        model.with(s, o);
                        return null;
                    }
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
                        return add(s, factory.type(e.asType()));
                    }
                    @Override public Void visitVariable(VariableElement e, String s) {
                        return add(s, factory.variable(e));
                    }
                    @Override public Void visitExecutable(ExecutableElement e, String s) {
                        return add(s, factory.method(e));
                    }
                    @Override public Void visitTypeParameter(TypeParameterElement e, String s) {
                        return add(s, factory.type(e.asType()));
                    }
                    @Override public Void visitUnknown(Element e, String s) {
                        return null;
                    }
                }, annotationType.getSimpleName().toString());
            }
        });
    }

    private void tryAddParameter(Element element, TemplateModel model) {
        if(nonNull(element)) {
            tryAddParameter(element, element.getAnnotationMirrors().stream().map(annotation -> annotation.getAnnotationType().asElement()), model);
        }
    }

    private Element asElement(Tree tree) {
        return trees.getElement(trees.getPath(getCurrentPath().getCompilationUnit(), tree));
    }

    @Override
    public Void visitMethod(MethodTree tree, TemplateModel templateModel) {
        tryAddParameter(trees.getElement(getCurrentPath()), templateModel);
        return super.visitMethod(tree, templateModel);
    }

    @Override
    public Void visitAnnotatedType(AnnotatedTypeTree tree, TemplateModel templateModel) {
        Stream<Element> annotationTypes = tree.getAnnotations().stream().map(annotation -> asElement(annotation.getAnnotationType()));
        tree.getUnderlyingType().accept(new SimpleTreeVisitor<Void, Void>() {
            @Override public Void visitWildcard(WildcardTree wildcardTree, Void aVoid) {
                tryAddParameter(asElement(wildcardTree.getBound()), annotationTypes, templateModel);
                return null;
            }
            @Override protected Void defaultAction(Tree tree, Void aVoid) {
                tryAddParameter(asElement(tree), annotationTypes, templateModel);
                return null;
            }
        }, null);
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

    @Override
    public Void visitTypeParameter(TypeParameterTree typeParameterTree, TemplateModel templateModel) {
        tryAddParameter(trees.getElement(getCurrentPath()), templateModel);
        return super.visitTypeParameter(typeParameterTree, templateModel);
    }
}
