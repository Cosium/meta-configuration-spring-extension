package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.MetaQualifier;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Réda Housni Alaoui
 */
public class MetaBeanMethodParameter {

  private final VariableElement variableElement;

  public MetaBeanMethodParameter(VariableElement variableElement) {
    this.variableElement = variableElement;
  }

  public String name() {
    return variableElement.getSimpleName().toString();
  }

  public ParameterSpec createOverridingParameterSpec(ConfigurationPlan plan) {
    ParameterSpec.Builder builder =
        ParameterSpec.builder(
            TypeName.get(variableElement.asType()),
            name(),
            variableElement.getModifiers().toArray(Modifier[]::new));

    MetaQualifier metaQualifier = variableElement.getAnnotation(MetaQualifier.class);
    if (metaQualifier != null) {
      String metaId = metaQualifier.metaId();
      BeanPlan beanPlan = plan.requireBeanPlan(metaId);
      builder.addAnnotation(
          AnnotationSpec.builder(Qualifier.class)
              .addMember("value", "$S", beanPlan.beanName())
              .build());
    }
    return builder.build();
  }
}
