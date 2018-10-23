{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set methodName = (methodName == "") ? ((method.isConstructor) ? "build" : method.name) : methodName %}
{% set classSuffix = concat(capitalize(methodName), "er") %}
{% set className = (className == "") ? concat(method.declaringClass.simpleName, classSuffix.replaceFirst("teer", "tor").replaceFirst("eer", "er")) : className %}
{% set typeParameterList = (method.isConstructor) ? productType.parameters : ((method.isStatic) ? method.typeVariables : merge(method.declaringClass.parameters, method.typeVariables)) %}
{% set classParameters = (empty(typeParameterList)) ? "" : concat("<", join(typeParameterList, ", "), ">") %}
package {{ packageName }};
import javax.annotation.Generated;
import fluent.api.End;

@Generated("Generated code using {{ templatePath }}")
public interface {{ className }}{% if not empty(typeParameterList) %}<{% for t in typeParameterList %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %} {
{% for parameter in method.parameters %}
    public {{ className }}{{ classParameters }} {{ parameter.name }}({{ parameter.type }} value);
{% endfor %}
    @End
    public {{ productType }} {{ methodName }}();
}
