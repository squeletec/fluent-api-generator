{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set className = (className == "") ? concat(method.declaringClass.simpleName, (method.isConstructor) ? "" : capitalize(method.name), capitalize(methodName), "er") : className %}
package {{ packageName }}.impl;
import javax.annotation.Generated;
import {{ packageName }}.{{ className }};

@Generated("Generated code using {{ templatePath }}")
public class {{ className }}Impl implements {{ className }} {

{% for parameter in method.parameters %}
    private {{ parameter.type }} {{ parameter.name }};
{% endfor %}
{% if method.isConstructor or method.isStatic %}{% else %}
    private final {{ method.declaringClass }} factory;

    public {{ className }}Impl({{ method.declaringClass }} factory) {
        this.factory = factory;
    }
{% endif %}
{% for parameter in method.parameters %}
    @Override
    public {{ className }} {{ parameter.name }}({{ parameter.type }} value) {
        this.{{ parameter.name }} = value;
        return this;
    }
{% endfor %}
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
