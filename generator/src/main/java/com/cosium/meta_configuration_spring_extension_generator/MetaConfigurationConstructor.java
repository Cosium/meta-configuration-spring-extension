package com.cosium.meta_configuration_spring_extension_generator;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * @author Réda Housni Alaoui
 */
public class MetaConfigurationConstructor {

  private final ExecutableElement executableElement;

  public MetaConfigurationConstructor(ExecutableElement executableElement) {
    this.executableElement = executableElement;
  }

  public static List<MetaConfigurationConstructor> collect(TypeElement typeElement) {
    return typeElement.getEnclosedElements().stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(executableElement -> executableElement.getKind() == ElementKind.CONSTRUCTOR)
        .map(MetaConfigurationConstructor::new)
        .toList();
  }

  public MethodSpec createOverridingMethodSpec(ConfigurationPlan plan) {
    MethodSpec.Builder builder = MethodSpec.constructorBuilder();
    List<MetaConfigurationConstructorParameter> parameters =
        executableElement.getParameters().stream()
            .map(MetaConfigurationConstructorParameter::new)
            .toList();
    parameters.stream()
        .map(MetaConfigurationConstructorParameter::createOverridingParameterSpec)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(builder::addParameter);

    builder.addStatement(
        "super($L)",
        parameters.stream()
            .map(parameter -> parameter.createParameterValue(plan))
            .collect(CodeBlock.joining(", ")));
    return builder.build();
  }
}
