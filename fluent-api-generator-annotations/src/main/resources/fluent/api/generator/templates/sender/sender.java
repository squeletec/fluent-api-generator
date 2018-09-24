{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set modelVar = method.parameters[modelArgument] %}
{% set className = (className == "") ? concat(modelVar.type.simpleName, capitalize(methodName), "er") : className %}
{% set classParameters = empty(modelVar.type.parameterVariables) ? "" : concat("<", join(modelVar.type.parameterVariables, ", "), ">") %}
package {{ packageName }};
import javax.annotation.Generated;
import fluent.api.End;

@Generated("Generated code using {{ templatePath }}")
public class {{ className }}{{ classParameters }} {
{% for parameter in method.parameters %}
    private final {{ parameter.type }} {{ parameter.name }};
{% endfor %}
{% if method.isConstructor or method.isStatic %}
    public {{ className }}({% for parameter in method.parameters %}{% if loop.first %}{% else %}, {% endif %}{{parameter.type}} {{parameter.name}}{% endfor %}) {
{% else %}
    private final {{ method.declaringClass }} factory;

    public {{ className }}({{ method.declaringClass }} factory{% for parameter in method.parameters %}, {{parameter.type}} {{parameter.name}}{% endfor %}) {
        this.factory = factory;
{% endif %}{% for parameter in method.parameters %}
        this.{{parameter.name}} = {{parameter.name}};{% endfor %}
    }
{% for setter in modelVar.type.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    public {{ className }}{{ classParameters }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value) {
        this.{{ modelVar.name }}.{{ setter.name }}(value);
        return this;
    }
{% endif %}{% endfor %}
    @End
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
