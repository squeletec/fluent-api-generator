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

package fluent.api.generator.model.impl;

import fluent.api.generator.model.TemplateModel;
import org.jtwig.JtwigModel;
import org.jtwig.environment.EnvironmentConfiguration;

import javax.annotation.processing.Filer;
import java.io.IOException;

import static javax.tools.StandardLocation.CLASS_PATH;
import static org.jtwig.JtwigModel.newModel;
import static org.jtwig.JtwigTemplate.inlineTemplate;
import static org.jtwig.environment.EnvironmentConfigurationBuilder.configuration;

public class JtwigTemplateModel implements TemplateModel {

    private final EnvironmentConfiguration config = configuration().render().withStrictMode(true).and().build();
    private final JtwigModel model = newModel();
    private final Filer filer;

    public JtwigTemplateModel(Filer filer) {
        this.filer = filer;
    }

    @Override
    public TemplateModel with(String name, Object value) {
        model.with(name, value);
        return this;
    }

    @Override
    public String render(String templatePath) {
        return inlineTemplate(readTemplate(templatePath), config).render(model.with("templatePath", templatePath));
    }

    private String readTemplate(String path) {
        if(path.startsWith("/")) {
            path = path.substring(1);
        }
        int split = path.lastIndexOf('/');
        String toPackage = path.substring(0, split).replace('/', '.');
        String toName = path.substring(split + 1);
        try {
            CharSequence charContent = filer.getResource(CLASS_PATH, toPackage, toName).getCharContent(true);
            return charContent.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Template: " + path + " not found on classpath.");
        }
    }


}
