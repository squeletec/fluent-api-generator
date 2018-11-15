{% set productType = defined(var) ? var.type : type %}
{% set packageName = (packageName == "") ? (defined(var) ? var.packageName : type.packageName) : packageName %}
{% set classSuffix = concat(capitalize(methodName), "er").replaceFirst("teer", "tor").replaceFirst("eer", "er") %}
{% set className = (className == "") ? concat(productType.simpleName, classSuffix) : className %}
{% set classParameters = empty(productType.parameterVariables) ? "" : concat("<", join(productType.parameterVariables, ", "), ">") %}
package {{ packageName }}.impl;
import javax.annotation.Generated;
import {{ packageName }}.{{ className }};

@Generated("Generated code using {{ templatePath }}")
public final class {{ className }}Impl{% if not empty(productType.parameterVariables) %}<{% for t in productType.parameterVariables %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %}
    implements {{ className }}{{ classParameters }} {

    private final {{ productType }} object;

    public {{ className }}Impl({{ productType }} object) {
        this.object = object;
    }
{% if productType.hasDefaultConstructor %}
    public {{ className }}Impl() {
        this(new {{ productType }}());
    }
{% endif %}{% if factoryMethod != "" %}
    public static {% if not empty(productType.parameterVariables) %}<{% for t in productType.parameterVariables %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %} {{ className }}{{ classParameters }} {{ factoryMethod }}({{ productType }} object) {
        return new {{ className }}Impl{% if not empty(productType.parameterVariables) %}<>{% endif %}(object);
    }
{% if productType.hasDefaultConstructor %}
    public static {% if not empty(productType.parameterVariables) %}<{% for t in productType.parameterVariables %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %} {{ className }}{{ classParameters }} {{ factoryMethod }}() {
        return new {{ className }}Impl{% if not empty(productType.parameterVariables) %}<>{% endif %}();
    }
{% endif %}{% endif %}
{% for setter in productType.methods %}{% if setter.name.startsWith("set") and setter.parameters.size == 1 %}
    @Override
    public {{ className }}{{ classParameters }} {{ setter.propertyName }}({{ setter.parameters[0].type }} value) {
        object.{{ setter.name }}(value);
        return this;
    }
{% endif %}{% endfor %}
    @Override
    public {{ productType }} {{ methodName }}() {
        return object;
    }

}
