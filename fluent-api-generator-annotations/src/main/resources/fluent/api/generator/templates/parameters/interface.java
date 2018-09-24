{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set className = (className == "") ? concat(method.declaringClass.simpleName, (method.isConstructor) ? "" : capitalize(method.name), capitalize(methodName), "er") : className %}
{% set typeParameterList = (method.isConstructor) ? productType.parameters : ((method.isStatic) ? method.typeVariables : merge(method.declaringClass.parameters, method.typeVariables)) %}
{% set classParameters = (empty(typeParameterList)) ? "" : concat("<", join(typeParameterList, ", "), ">") %}
package {{ packageName }};
import javax.annotation.Generated;
import fluent.api.End;

@Generated("Generated code using {{ templatePath }}")
public interface {{ className }}{{ classParameters }} {
{% for parameter in method.parameters %}
    public {{ className }}{{ classParameters }} {{ parameter.name }}({{ parameter.type }} value);
{% endfor %}
    @End
    public {{ productType }} {{ methodName }}();
}
