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

import com.sun.source.util.Trees;
import fluent.api.generator.TypeFilter;
import fluent.api.generator.Templates;
import fluent.api.generator.model.ModelFactory;
import fluent.api.generator.model.impl.ModelTypeFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

/**
 * Template driven Java code generator, which generates code based on the annotated element.
 *
 * E.g. a method annotated with annotation @Templates(template) gets a model fully describing the method
 * with its return type, arguments and annotations, so they can be easily accessed and used in the JTwig
 * template language.
 *
 * That allows to generate various derived Java classes. Main motivation for this meets to generate fluent
 * builders, senders, verifiers, argument builders, etc.
 */
@SupportedAnnotationTypes("*")
public class GeneratingProcessor extends AbstractProcessor {

    private static final Predicate<Element> defaultFilter = element -> true;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ProcessingEnvironment processingEnv = jbUnwrap(this.processingEnv);
        Filer filer = processingEnv.getFiler();
        ModelFactory factory = new ModelTypeFactory(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), filer);
        ParameterScanner parameterScanner = new ParameterScanner(Trees.instance(processingEnv), factory);

        for(TypeElement annotation : annotations) {
            List<Element> templateAnnotations = concat(
                    Stream.of(annotation),
                    annotation.getAnnotationMirrors().stream().map(m -> m.getAnnotationType().asElement())
            ).filter(a -> nonNull(a.getAnnotation(Templates.class))).collect(Collectors.toList());
            if(!templateAnnotations.isEmpty()) {
                roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> templateAnnotations.forEach(templateAnnotation -> {
                    if(element.getKind().equals(ElementKind.ANNOTATION_TYPE)) {
                        Target templateTargets = templateAnnotation.getAnnotation(Target.class);
                        if(templateTargets != null) {
                            Target elementTargets = element.getAnnotation(Target.class);
                            Set<ElementType> collect = Stream.of(templateTargets.value()).collect(toSet());
                            if(elementTargets == null || !collect.containsAll(Stream.of(elementTargets.value()).collect(toSet()))) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid use of annotation: " + annotation + "! When applied on annotation, it must restrict usage to " + templateTargets + ". But annotation " + element + " allows " + (elementTargets == null ? "any target." : elementTargets), element);
                            }
                        }
                    } else {
                        Predicate<Element> predicate = filter(templateAnnotation.getAnnotation(TypeFilter.class));
                        if (predicate.test(element)) {
                            GeneratingVisitor visitor = new GeneratingVisitor(filer, factory, parameterScanner, annotation == templateAnnotation ? element : annotation);
                            element.accept(visitor, (TypeElement) templateAnnotation);
                        } else {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid use of annotation: " + annotation + "! It can be applied only on " + predicate, element);
                        }
                    }
                }));
            }
        }
        return false;
    }

    private ProcessingEnvironment jbUnwrap(ProcessingEnvironment wrappedEnv) {
        ProcessingEnvironment unwrapped = null;
        try {
            final Class<?> apiWrappers = wrappedEnv.getClass().getClassLoader().loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            unwrapped = (ProcessingEnvironment) unwrapMethod.invoke(null, ProcessingEnvironment.class, wrappedEnv);
        }
        catch (Throwable ignored) {}
        return unwrapped != null? unwrapped : wrappedEnv;
    }

    private Predicate<Element> filter(TypeFilter filter) {
        return isNull(filter) ? defaultFilter : new TypeFilterPredicate(filter.value());
    }

    private static final class TypeFilterPredicate implements Predicate<Element> {
        private final String pattern;

        private TypeFilterPredicate(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean test(Element element) {
            return element.asType().toString().matches(pattern);
        }

        @Override
        public String toString() {
            return "type matching: " + pattern;
        }
    }

}
