package {{ type.packageName }};

public interface Nesting{{ type.simpleName }}Class {

    // {{ type.annotations }}

{% for method in type.declaredMethods %}

        {% if method.type.isSubclassOf(nestedType) %}
        void group{{method}}();
        {% else %}
        {{ method.type }} simple{{method}}();
        {% endif %}

{% endfor %}

}