/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2019, Ondrej Fischer
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

package fluent.api;

import fluent.api.generator.Templates;

import java.lang.annotation.*;

/**
 * Annotation, that triggers code generator to create a fluent builder for an existing factory method with many arguments.
 *
 * E.g. for method:
 *
 * public MyClass(String a, String b, int c, Object d) {
 *
 * }
 *
 * it will generate fluent API, which can be used like following:
 *
 * MyClass myClass = new MyClassBuilder().a("a").b("b").c(4).d(new Object()).build();
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Templates("/fluent/api/templates/sender/implementation.jtwig")
public @interface FluentSender {

    /**
     * Specify package name, where to create the generated class.
     * If not defined, it will be the same as the class with the annotated method/constructor.
     * @return Target package name.
     */
    String packageName() default "";

    /**
     * Specify the generated class name. If not specified, it will be auto-generated by the convention:
     * {Model type}{target method name}er
     *
     * E.g.
     *
     * MyClassSender
     *
     * @return Generated class name.
     */
    String className() default "";

    /**
     * Specify name of the terminal method (method which really does perform the call to the factory method).
     * By default the method name is "build()".
     * @return Terminal method name.
     */
    String methodName() default "";

    /**
     * Specify annotations, that should be added to terminal method.
     * @return Annotation for the terminal method.
     */
    Class<? extends Annotation>[] methodAnnotation() default {};

    /**
     * Specify factory method used to create instances of the fluent parameters builder instead of constructor.
     * @return Name of the factory method.
     */
    String factoryMethod() default "";

    /**
     * Specify prefixes of setter methods.
     * @return All prefixes, by which we can identify setter method.
     */
    String setterPattern() default "(set)(.+)";

    /**
     * Specify regex group, which should contain the name of the setter within the original method name.
     * @return Group in Java regex notation (e.g. $1, $2 etc.)
     */
    String setterNameGroup() default "$2";

}
