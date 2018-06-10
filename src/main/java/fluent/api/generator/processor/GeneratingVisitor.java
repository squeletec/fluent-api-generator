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
import org.jtwig.JtwigModel;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jtwig.JtwigModel.newModel;
import static org.jtwig.JtwigTemplate.classpathTemplate;


/**
 * Generator of java code, which creates different models for different visited elements.
 * The annotation can be applied on method, constructor, but also class or interface.
 */
class GeneratingVisitor implements ElementVisitor<Void, Void> {

    private static final Pattern PACKAGE = Pattern.compile("package\\s+([^;]+)\\s*;");
    private static final Pattern CLASS = Pattern.compile("(class|interface)\\s+(\\w+)");

    private final Filer filer;
    private final Set<String> generated = new HashSet<>();
    private final ModelFactory factory;

    GeneratingVisitor(Filer filer, Trees trees, ModelFactory factory) {
        this.filer = filer;
        this.factory = factory;
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
        return render(newModel().with("type", factory.model(e)), e);
    }

    @Override
    public Void visitVariable(VariableElement e, Void aVoid) {
        return null;
    }

    @Override
    public Void visitExecutable(ExecutableElement e, Void aVoid) {
        return render(newModel().with("method", factory.model(e)), e);
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement e, Void aVoid) {
        return null;
    }

    @Override
    public Void visitUnknown(Element e, Void aVoid) {
        return null;
    }

    private Void render(JtwigModel model, Element element) {
        for(String path : element.getAnnotation(Generate.class).value()) {
            String source = classpathTemplate(path).render(model.with("templatePath", path));
            Matcher packageName = PACKAGE.matcher(source);
            Matcher className = CLASS.matcher(source);
            if(packageName.find() && className.find()) {
                String sourcePath = packageName.group(1) + "." + className.group(2);
                if(generated.add(sourcePath)) {
                    try(Writer output = filer.createSourceFile(sourcePath).openWriter()) {
                        output.append(source);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

}
