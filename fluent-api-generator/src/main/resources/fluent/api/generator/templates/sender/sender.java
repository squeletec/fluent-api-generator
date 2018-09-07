{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set className = (className == "") ? concat(method.declaringClass.simpleName, (method.isConstructor) ? "" : capitalize(method.name), capitalize(methodName), "er") : className %}
{% set modelVar = method.parameters[0] %}
package {{ packageName }};
import javax.annotation.Generated;

@Generated("Generated code using {{ templatePath }}")
public class {{ className }} {

{% for parameter in method.parameters %}
    private final {{ parameter.type }} {{ parameter.name }};
{% endfor %}
{% if method.isConstructor or method.isStatic %}
    public {{ className }}({% for parameter in method.parameters %}{% if loop.first %}{% else %}, {% endif %}{{parameter.type}} {{parameter.name}}{% endfor %}) {
{% else %}
    private final {{ method.declaringClass }} factory;

    public {{ className }}({{ method.declaringClass }} factory{% for parameter in method.parameters %}, {{parameter.type}} {{parameter.name}}{% endfor %}) {
        this.factory = factory;
{% endif %}
{% for parameter in method.parameters %}
        this.{{parameter.name}} = {{parameter.name}};{% endfor %}
    }
{% for setter in modelVar.type.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    public {{ className }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value) {
        {{ modelVar.name }}.{{ setter.name }}(value);
        return this;
    }
{% endif %}{% endfor %}
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
