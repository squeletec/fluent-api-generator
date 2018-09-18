{% set productType = var.type %}
{% set packageName = (packageName == "") ? var.packageName : packageName %}
{% set className = (className == "") ? concat(productType.simpleName, capitalize(methodName), "er") : className %}
package {{ packageName }};

import javax.annotation.Generated;
import fluent.api.End;

@Generated("Generated code using {{ templatePath }}")
public interface {{ className }} {

{% for setter in productType.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    {{ className }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value);
{% endif %}{% endfor %}
    @End
    {{ productType }} {{ methodName }}();

}
