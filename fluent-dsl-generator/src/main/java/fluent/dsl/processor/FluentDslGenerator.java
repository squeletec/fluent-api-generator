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

package fluent.dsl.processor;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.PrintWriter;

import static java.util.stream.Collectors.joining;

public final class FluentDslGenerator {

    private static final String INDENTATION_LEVEL = "\t";
    private final PrintWriter writer;
    private final String indentation;

    private FluentDslGenerator(PrintWriter writer, String indentation) {
        this.writer = writer;
        this.indentation = indentation;
    }

    private FluentDslGenerator indent() {
        return new FluentDslGenerator(writer, indentation + INDENTATION_LEVEL);
    }

    private void generateReturnType(FluentDslModel model) {
        if(model.nonTerminal()) {
            writer.println();
            writer.println(indentation + "interface " + model.className() + " {");
            indent().generateContent(model);
            writer.println(indentation + "}");
        }
    }

    private void generateMethodImplementation(FluentDslModel model) {
        writer.println();
        writer.println(indentation + model.modifiers() + " " + model.returnType() + " " + model.methodName() + "(" + model.parameterType() + " " + model.parameterName() + ") {");
        indent().generateMethodBody(model);
        writer.println(indentation + "}");
    }

    private void generateAnonymousClass(FluentDslModel model) {
        writer.println(indentation + "return new " + model.className() + "() {");
        model.followers().forEach(indent()::generateMethodImplementation);
        writer.println(indentation + "};");
    }

    private void generateMethodBody(FluentDslModel model) {
        if(model.nonTerminal()) {
            generateAnonymousClass(model);
        } else {
            ExecutableElement method = model.getMethod();
            String methodName = (method.getModifiers().contains(Modifier.STATIC) ? method.getEnclosingElement().toString() : "impl") + "." + method.getSimpleName();
            if(!"void".equals(method.getReturnType().toString())) {
                methodName = "return " + methodName;
            }
            writer.println(indentation + methodName + "(" + method.getParameters().stream().map(Object::toString).collect(joining(", ")) + ");");
        }
    }

    private void generateMethod(FluentDslModel model) {
        writer.println(indentation + model.annotation() + " " + model.returnType() + " " + model.methodName() + "(" + model.parameterType() + " " + model.parameterName() + ");");
    }

    private void generateContent(FluentDslModel model) {
        model.followers().forEach(this::generateMethod);
        model.followers().forEach(this::generateReturnType);
    }

    private void generateRoot(FluentDslModel model) {
        writer.println("package " + model.packageName() + ";");
        writer.println();
        writer.println("import javax.annotation.Generated;");
        writer.println("import fluent.api.Start;");
        writer.println("import fluent.api.End;");
        writer.println();
        writer.println("@Generated(\"DSL class generated from " + model.returnType() + "\")");
        writer.println("public interface " + model.className() + " {");
        writer.println();
        indent().generateMethodImplementation(model);
        indent().generateContent(model);
        writer.println("}");
    }

    public static void generateFile(Filer filer, FluentDslModel model) {
        try(PrintWriter writer = new PrintWriter(filer.createSourceFile(model.fullyQualifiedClassName()).openWriter())) {
            new FluentDslGenerator(writer, "").generateRoot(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
