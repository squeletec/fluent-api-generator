{% set productType = defined(var) ? var.type : type %}
{% set packageName = (packageName == "") ? (defined(var) ? var.packageName : type.packageName) : packageName %}
{% set classSuffix = "Check" %}
{% set className = (className == "") ? concat(productType.simpleName, classSuffix) : className %}
{% set factoryMethod = (factoryMethod == "") ? concat(productType.simpleName, "With") : factoryMethod %}
{% set classParameters = empty(productType.parameterVariables) ? "" : concat("<", join(productType.parameterVariables, ", "), ">") %}
package {{ packageName }};
import javax.annotation.Generated;
import fluent.api.End;
import fluent.validation.Check;
import fluent.validation.CheckDsl;
import {{productType}};


@Generated("Generated code using {{ templatePath }}")
public final class {{ className }}{% if not empty(productType.parameterVariables) %}<{% for t in productType.parameterVariables %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %}
        extends CheckDsl<{{ className }}{{ classParameters }} , {{ productType.simpleName }}> {

    protected {{ className }}(Check<? super {{ productType.simpleName }}> check) {
        super(check, {{ className }}::new);
    }

    protected {{ className }}() {
        super({{ className }}::new);
    }

    public static {% if not empty(productType.parameterVariables) %}<{% for t in productType.parameterVariables %}{% if loop.first %}{% else %}, {% endif %}{{ t.declaration }}{% endfor %}>{% endif %} {{ className }}{{ classParameters }} {{ factoryMethod }}() {
        return new {{ className }}{% if not empty(productType.parameterVariables) %}<>{% endif %}();
    }
{% for getter in productType.methods %}{% if getter.name.startsWith("get") and getter.name != "getClass" and getter.parameters.size == 0 %}
    public {{ className }}{{ classParameters }} {{ getter.propertyName }}({{ getter.type }} value) {
        return withField("{{ getter.propertyName }}", {{ productType.simpleName }}::{{ getter }}).equalTo(value);
    }
{% endif %}{% endfor %}

}
