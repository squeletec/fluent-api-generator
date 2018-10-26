{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set methodName = (methodName == "") ? ((method.isConstructor) ? "build" : method.name) : methodName %}
{% set classSuffix = concat(capitalize(methodName), "er").replaceFirst("teer", "tor").replaceFirst("eer", "er") %}
{% set className = (className == "") ? concat(method.declaringClass.simpleName, classSuffix) : className %}
{% set typeParameterList = (method.isConstructor) ? productType.parameters : ((method.isStatic) ? method.typeVariables : merge(method.declaringClass.parameters, method.typeVariables)) %}
{% set classParameters = (empty(typeParameterList)) ? "" : concat("<", join(typeParameterList, ", "), ">") %}
package {{ packageName }};
import javax.annotation.Generated;
import fluent.api.End;

@Generated("Generated code using {{ templatePath }}")
public class {{ className }}{% if not empty(typeParameterList) %}<{% for t in typeParameterList %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %} {

{% for parameter in method.parameters %}
    private {{ parameter.type }} {{ parameter.name }};
{% endfor %}
{% if method.isConstructor or method.isStatic %}{% if factoryMethod != "" %}
    public static {% if not empty(typeParameterList) %}<{% for t in typeParameterList %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %} {{ className }}{{ classParameters }} {{ factoryMethod }}() {
        return new {{ className }}{% if not empty(typeParameterList) %}<>{% endif %}();
    }
{% endif %}{% else %}
    private final {{ method.declaringClass }} factory;

    public {{ className }}({{ method.declaringClass }} factory) {
        this.factory = factory;
    }
{% if factoryMethod != "" %}
    public static {% if not empty(typeParameterList) %}<{% for t in typeParameterList %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %} {{ className }}{{ classParameters }} {{ factoryMethod }}({{ method.declaringClass }} factory) {
        return new {{ className }}{% if not empty(typeParameterList) %}<>{% endif %}(factory);
    }
{% endif %}{% endif %}
{% for parameter in method.parameters %}
    public {{ className }}{{ classParameters }} {{ parameter.name }}({{ parameter.type }} value) {
        this.{{ parameter.name }} = value;
        return this;
    }
{% endfor %}
    @End
    public {{ productType }} {{ methodName }}() {
        {% if productType != "void" %}return {% endif %}{%
        if method.isConstructor
            %}new {{ method.declaringClass }}{%
        elseif method.isStatic
            %}{{ method.declaringClass.raw }}.{{ method.name }}{%
        else
            %}factory.{{ method.name }}{%
        endif
        %}({{ join(method.parameters, ", ") }});
    }
}
