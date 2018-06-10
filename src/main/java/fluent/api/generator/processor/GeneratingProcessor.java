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

package fluent.api.generator.processor;

import com.sun.source.util.Trees;
import fluent.api.generator.Generate;
import fluent.api.generator.model.ModelFactory;
import fluent.api.generator.model.ModelFactoryImpl;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Template driven Java code generator, which generates code based on the annotated element.
 *
 * E.g. a method annotated with annotation @Generate(template) gets a model fully describing the method
 * with its return type, arguments and annotations, so they can be easily accessed and used in the JTwig
 * template language.
 *
 * That allows to generate various derived Java classes. Main motivation for this meets to generate fluent
 * builders, senders, verifiers, argument builders, etc.
 */
@SupportedAnnotationTypes("fluent.api.generator.Generate")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GeneratingProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ModelFactory factory = new ModelFactoryImpl(processingEnv.getTypeUtils());
        GeneratingVisitor visitor = new GeneratingVisitor(processingEnv.getFiler(), Trees.instance(processingEnv), factory);
        roundEnv.getElementsAnnotatedWith(Generate.class).forEach(element -> element.accept(visitor, null));
        return false;
    }

}
