{% set productType = var.type %}
{% set packageName = (packageName == "") ? var.packageName : packageName %}
{% set className = (className == "") ? concat(productType.simpleName, capitalize(methodName), "er") : className %}
package {{ packageName }};
import javax.annotation.Generated;

@Generated("Generated code using {{ templatePath }}")
public final class {{ className }} {

    private final {{ productType }} object;

    public {{ className }}({{ productType }} object) {
        this.object = object;
    }

{% for setter in productType.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    public {{ className }} {{ setter.name.substring(3) }}({{ setter.parameters[0].type }} value) {
        object.{{ setter.name }}(value);
        return this;
    }
{% endif %}{% endfor %}
    public {{ productType }} {{ methodName }}() {
        return {{ method.name }}(object);
    }

}