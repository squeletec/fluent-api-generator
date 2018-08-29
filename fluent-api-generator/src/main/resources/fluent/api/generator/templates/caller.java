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

package {{ method.declaringClass.packageName }};
{% set className = (method.isConstructor) ? (method.declaringClass.simpleName) : (method.name) %}
import javax.annotation.Generated;

@Generated("Generated code using {{ templatePath }}")
public final class {{ className }}Caller {

{% for parameter in method.parameters %}
    private {{ parameter.type }} {{ parameter.name }};
{% endfor %}
{% if method.isConstructor or method.isStatic %}{% else %}
    private final {{ method.declaringClass }} factory;

    public {{ className }}Caller({{ method.declaringClass }} factory) {
        this.factory = factory;
    }
{% endif %}
{% for parameter in method.parameters %}
    public {{ className }}Caller {{ parameter.name }}({{ parameter.type }} value) {
        this.{{ parameter.name }} = value;
        return this;
    }
{% endfor %}{% if method.isConstructor %}
    public void call() {
        new {{ method.declaringClass }}({% for parameter in method.parameters %}{% if loop.first %}{% else %}, {% endif %}{{parameter.name}}{% endfor %});
    }
{% elseif method.isStatic %}
    public void call() {
        {{ method.declaringClass }}.{{ method.name }}({% for parameter in method.parameters %}{% if loop.first %}{% else %}, {% endif %}{{parameter.name}}{% endfor %});
    }
{% else %}
    public void call() {
        factory.{{ method.name }}({% for parameter in method.parameters %}{% if loop.first %}{% else %}, {% endif %}{{parameter.name}}{% endfor %});
    }
{% endif %}
}
