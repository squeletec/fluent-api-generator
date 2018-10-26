{% set method = var.method %}
{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set className = (className == "") ? concat(var.type.simpleName, capitalize(methodName), "er") : className %}
{% set classParameters = empty(var.type.parameterVariables) ? "" : concat("<", join(var.type.parameterVariables, ", "), ">") %}
package {{ packageName }}.impl;
import javax.annotation.Generated;
import {{ packageName }}.{{ className }};

@Generated("Generated code using {{ templatePath }}")
public class {{ className }}Impl{% if not empty(var.type.parameterVariables) %}<{% for t in var.type.parameterVariables %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %}
    implements {{ className }}{{ classParameters }} {

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
{% for setter in var.type.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    @Override
    public {{ className }}{{ classParameters }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value) {
        this.{{ var.name }}.{{ setter.name }}(value);
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
        %}({{ join(method.parameters, ", ") }});
    }
}
