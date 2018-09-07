{% set productType = var.type %}
{% set packageName = (packageName == "") ? var.packageName : packageName %}
{% set className = (className == "") ? concat(productType.simpleName, capitalize(methodName), "er") : className %}
package {{ packageName }}.impl;
import javax.annotation.Generated;
import {{ packageName }}.{{ className }};

@Generated("Generated code using {{ templatePath }}")
public final class {{ className }}Impl implements {{ className }} {

    private final {{ productType }} object;

    public {{ className }}Impl({{ productType }} object) {
        this.object = object;
    }
{% for setter in productType.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    @Override
    public {{ className }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value) {
        object.{{ setter.name }}(value);
        return this;
    }
{% endif %}{% endfor %}
    @Override
    public {{ productType }} {{ methodName }}() {
        return {{ method.name }}(object);
    }

}
