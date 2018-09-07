{% set productType = (method.isConstructor) ? (method.declaringClass) : (method.type) %}
{% set packageName = (packageName == "") ? method.declaringClass.packageName : packageName %}
{% set className = (className == "") ? concat(method.declaringClass.simpleName, (method.isConstructor) ? "" : capitalize(method.name), capitalize(methodName), "er") : className %}
{% set modelVar = method.parameters[0] %}
package {{ packageName }};
import javax.annotation.Generated;

@Generated("Generated code using {{ templatePath }}")
public interface {{ className }} {
{% for setter in modelVar.type.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    public {{ className }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value);
{% endif %}{% endfor %}
    public {{ productType }} {{ methodName }}();
}
