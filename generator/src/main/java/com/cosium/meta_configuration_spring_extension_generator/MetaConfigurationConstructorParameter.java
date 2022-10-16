package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.BeansMetadata;
import com.cosium.meta_configuration_spring_extension.InjectBeanName;
import com.cosium.meta_configuration_spring_extension.InjectParameterValue;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;

/**
 * @author Réda Housni Alaoui
 */
class MetaConfigurationConstructorParameter {

  private final VariableElement variableElement;
  private final TypeElement typeElement;

  public MetaConfigurationConstructorParameter(Types types, VariableElement variableElement) {
    this.variableElement = variableElement;
    typeElement = (TypeElement) types.asElement(variableElement.asType());
  }

  private String name() {
    return variableElement.getSimpleName().toString();
  }

  public Optional<ParameterSpec> createOverridingParameterSpec() {
    if (isBeansMetadata()) {
      return Optional.empty();
    }
    if (variableElement.getAnnotation(InjectBeanName.class) != null) {
      return Optional.empty();
    }
    if (variableElement.getAnnotation(InjectParameterValue.class) != null) {
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
    if (isBeansMetadata()) {
      List<CodeBlock> beansMetadataBuild = new ArrayList<>();
      beansMetadataBuild.add(CodeBlock.of("$T\n.builder()", ClassName.get(BeansMetadata.class)));
      plan.beanPlans().stream()
          .map(BeanMetadataType::new)
          .map(BeanMetadataType::createInstantiationCode)
          .map(metadataBuild -> CodeBlock.of("addBean(\n$>$L$<\n)", metadataBuild))
          .forEach(beansMetadataBuild::add);
      beansMetadataBuild.add(CodeBlock.of("build()"));
      return beansMetadataBuild.stream().collect(CodeBlock.joining("\n."));
    }

    InjectBeanName injectBeanName = variableElement.getAnnotation(InjectBeanName.class);
    if (injectBeanName != null) {
      String beanName = plan.requireBeanPlan(injectBeanName.metaId()).beanName();
      return CodeBlock.of("$S", beanName);
    }

    InjectParameterValue injectParameterValue =
        variableElement.getAnnotation(InjectParameterValue.class);
    if (injectParameterValue != null) {
      String parameterValue = plan.requireParameter(injectParameterValue.key()).value();
      return CodeBlock.of("$S", parameterValue);
    }

    return CodeBlock.of("$N", name());
  }

  private boolean isBeansMetadata() {
    return hasExactType(BeansMetadata.class);
  }

  private boolean hasExactType(Class<?> type) {
    return typeElement.getQualifiedName().contentEquals(type.getCanonicalName());
  }
}
