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

{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set className = (className == "") ? concat(method.declaringClass.simpleName, (method.isConstructor) ? "" : capitalize(method.name), capitalize(methodName), "er") : className %}
{% set modelVar = method.parameters[0] %}
package {{ packageName }}.impl;
import javax.annotation.Generated;
import {{ packageName }}.{{ className }};

@Generated("Generated code using {{ templatePath }}")
public class {{ className }}Impl implements {{ className }} {

{% for parameter in method.parameters %}
    private final {{ parameter.type }} {{ parameter.name }};
{% endfor %}
{% if method.isConstructor or method.isStatic %}
    public {{ className }}Impl({% for parameter in method.parameters %}{% if loop.first %}{% else %}, {% endif %}{{parameter.type}} {{parameter.name}}{% endfor %}) {
{% else %}
    private final {{ method.declaringClass }} factory;

    public {{ className }}Impl({{ method.declaringClass }} factory{% for parameter in method.parameters %}, {{parameter.type}} {{parameter.name}}{% endfor %}) {
        this.factory = factory;
{% endif %}
{% for parameter in method.parameters %}
        this.{{parameter.name}} = {{parameter.name}};{% endfor %}
    }
{% for setter in modelVar.type.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    @Override
    public {{ className }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value) {
        {{ modelVar.name }}.{{ setter.name }}(value);
        return this;
    }
{% endif %}{% endfor %}
    @Override
    public {{ productType }} {{ methodName }}() {
        {% if productType != "void" %}return {% endif %}{%
        if method.isConstructor
            %}new {{ method.declaringClass }}{%
        elseif method.isStatic
            %}{{ method.declaringClass }}.{{ method.name }}{%
        else
            %}factory.{{ method.name }}{%
        endif
        %}({% for parameter in method.parameters %}{% if loop.first %}{% else %}, {% endif %}{{parameter.name}}{% endfor %});
    }
}
