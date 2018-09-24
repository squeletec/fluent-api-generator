{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set modelVar = method.parameters[modelArgument] %}
{% set className = (className == "") ? concat(modelVar.type.simpleName, capitalize(methodName), "er") : className %}
{% set classParameters = empty(modelVar.type.parameterVariables) ? "" : concat("<", join(modelVar.type.parameterVariables, ", "), ">") %}
package {{ packageName }};
import javax.annotation.Generated;
import fluent.api.End;

@Generated("Generated code using {{ templatePath }}")
public interface {{ className }}{% if not empty(modelVar.type.parameterVariables) %}<{% for t in modelVar.type.parameterVariables %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %} {
{% for setter in modelVar.type.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    public {{ className }}{{ classParameters }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value);
{% endif %}{% endfor %}
    @End
    public {{ productType }} {{ methodName }}();
}
