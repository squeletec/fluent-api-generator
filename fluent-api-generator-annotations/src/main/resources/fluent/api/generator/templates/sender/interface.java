{% set method = var.method %}
{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set className = (className == "") ? concat(var.type.simpleName, capitalize(methodName), "er") : className %}
{% set classParameters = empty(var.type.parameterVariables) ? "" : concat("<", join(var.type.parameterVariables, ", "), ">") %}
package {{ packageName }};
import javax.annotation.Generated;
import fluent.api.End;

@Generated("Generated code using {{ templatePath }}")
public interface {{ className }}{% if not empty(var.type.parameterVariables) %}<{% for t in var.type.parameterVariables %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %} {
{% for setter in var.type.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    public {{ className }}{{ classParameters }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value);
{% endif %}{% endfor %}
    @End
    public {{ productType }} {{ methodName }}();
}
