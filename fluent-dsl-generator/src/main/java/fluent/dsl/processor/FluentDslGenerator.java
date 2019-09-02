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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static java.util.stream.Collectors.joining;

public final class FluentDslGenerator {

    private static final String INDENT = "\t";
    private final PrintWriter writer;

    private FluentDslGenerator(PrintWriter writer) {
        this.writer = writer;
    }

    private void generate(String prefix, NodeModel model) {
        model.followers().forEach(methodModel -> this.generateReturnType(prefix, methodModel));
        model.followers().forEach(methodModel -> this.generateMethod(prefix, methodModel));
    }

    private void generateReturnType(String prefix, NodeModel model) {
        if(model.nonTerminal()) {
            writer.println();
            writer.println(prefix + "interface " + model.className() + " {");
            generate(prefix + INDENT, model);
            writer.println();
            writer.println(prefix + "}");
        }
    }

    private void generateMethodImplementation(String prefix, NodeModel model) {
        writer.println();
        writer.println(prefix + "public " + model.returnType() + " " + model.methodName() + "(" + model.parameterType() + " " + model.parameterName() + ") {");
        generateMethodBody(prefix + INDENT, model);
        writer.println(prefix + "}");
    }

    private void generateAnonymousClass(String prefix, NodeModel model, String suffix) {
        writer.println("new " + model.className() + "() {");
        model.followers().forEach(methodModel -> generateMethodImplementation(prefix + INDENT, methodModel));
        writer.println();
        writer.println(prefix + "}" + suffix);

    }

    private void generateMethodBody(String prefix, NodeModel model) {
        if(model.nonTerminal()) {
            writer.println();
            writer.print(prefix + "return ");
            generateAnonymousClass(prefix, model, ";");
        } else {
            writer.println(prefix + "impl." + model.getMethod().getSimpleName() + "(" + model.getMethod().getParameters().stream().map(Object::toString).collect(joining(", ")) + ");");
        }
    }

    private void generateMethod(String prefix, NodeModel model) {
        writer.println();
        writer.println(prefix + model.returnType() + " " + model.methodName() + "(" + model.parameterType() + " arg);");
    }

    public static void generateBdd(Filer filer, NodeModel model) {
        try(Writer writer = filer.createSourceFile(model.fullyQualifiedClassName()).openWriter()) {
            PrintWriter printWriter = new PrintWriter(writer);
            FluentDslGenerator generator = new FluentDslGenerator(printWriter);
            printWriter.println("package " + model.packageName() + ";");
            printWriter.println();
            printWriter.println("public final class " + model.className() + " {");
            printWriter.println();
            printWriter.println(INDENT + "public static final class Dsl extends fluent.dsl.Bdd<Actions, Verifications> {");
            printWriter.println(INDENT + INDENT + "private Dsl(Actions actions, Verifications verifications) {super(actions, verifications);}");
            printWriter.println(INDENT + "}");
            printWriter.println();
            printWriter.println(INDENT + "public static Dsl dsl(" + model.parameterType() + " impl) {");
            printWriter.println(INDENT + INDENT + "return new Dsl(");
            printWriter.print(INDENT + INDENT + INDENT);
            generator.generateAnonymousClass(INDENT + INDENT + INDENT, model.follower(NodeModel.Actions), ",");
            printWriter.print(INDENT + INDENT + INDENT);
            generator.generateAnonymousClass(INDENT + INDENT + INDENT, model.follower(NodeModel.Verifications), "");
            printWriter.println(INDENT + INDENT + ");");
            printWriter.println(INDENT + "}");
            model.followers().forEach(methodModel -> generator.generateReturnType(INDENT, methodModel));
            printWriter.println();
            printWriter.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateDirectDsl(Filer filer, NodeModel model) {
        try(Writer writer = filer.createSourceFile(model.fullyQualifiedClassName()).openWriter()) {
            PrintWriter printWriter = new PrintWriter(writer);
            FluentDslGenerator generator = new FluentDslGenerator(printWriter);
            printWriter.println("package " + model.packageName() + ";");
            printWriter.println();
            printWriter.println("public interface " + model.className() + " {");
            printWriter.println();
            printWriter.println(INDENT + "public static " + model.className() + " dsl(" + model.parameterType() + " impl) {");
            printWriter.print(INDENT + INDENT + "return ");
            generator.generateAnonymousClass(INDENT + INDENT, model, ";");
            printWriter.println(INDENT + "}");
            printWriter.println();
            generator.generate(INDENT, model);
            printWriter.println();
            printWriter.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
