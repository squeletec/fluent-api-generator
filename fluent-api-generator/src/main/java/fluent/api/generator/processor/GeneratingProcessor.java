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

import fluent.api.generator.Templates;
import fluent.api.generator.model.ModelFactory;
import fluent.api.generator.model.ModelFactoryImpl;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static java.util.Objects.nonNull;

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
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GeneratingProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ModelFactory factory = new ModelFactoryImpl(processingEnv.getTypeUtils());
        GeneratingVisitor visitor = new GeneratingVisitor(processingEnv.getFiler(), factory);

        for(TypeElement annotation : annotations) {
            Templates templatesAnnotation = annotation.getAnnotation(Templates.class);
            if(nonNull(templatesAnnotation)) {
                roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> element.accept(visitor, annotation));
            }
        }
        return false;
    }

}
