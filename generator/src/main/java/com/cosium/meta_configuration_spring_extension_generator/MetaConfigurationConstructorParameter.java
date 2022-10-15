package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.InjectBeanName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * @author Réda Housni Alaoui
 */
public class MetaConfigurationConstructorParameter {

  private final VariableElement variableElement;

  public MetaConfigurationConstructorParameter(VariableElement variableElement) {
    this.variableElement = variableElement;
  }

  private String name() {
    return variableElement.getSimpleName().toString();
  }

  public Optional<ParameterSpec> createOverridingParameterSpec() {
    InjectBeanName injectBeanName = variableElement.getAnnotation(InjectBeanName.class);
    if (injectBeanName != null) {
      return Optional.empty();
    }

    ParameterSpec parameterSpec =
        ParameterSpec.builder(
                TypeName.get(variableElement.asType()),
                name(),
                variableElement.getModifiers().toArray(Modifier[]::new))
            .build();
    return Optional.of(parameterSpec);
  }

  public CodeBlock createParameterValue(ConfigurationPlan plan) {
    InjectBeanName injectBeanName = variableElement.getAnnotation(InjectBeanName.class);
    if (injectBeanName == null) {
      return CodeBlock.of("$N", name());
    }
    String beanName = plan.requireBeanPlan(injectBeanName.metaId()).beanName();
    return CodeBlock.of("$S", beanName);
  }
}
