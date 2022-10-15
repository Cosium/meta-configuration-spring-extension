package com.cosium.meta_configuration_spring_extension_generator;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

/**
 * @author Réda Housni Alaoui
 */
public class MetaConfigurationConstructor {

  private final Types types;
  private final ExecutableElement executableElement;

  public MetaConfigurationConstructor(Types types, ExecutableElement executableElement) {
    this.types = types;
    this.executableElement = executableElement;
  }

  public static List<MetaConfigurationConstructor> collect(Types types, TypeElement typeElement) {
    return typeElement.getEnclosedElements().stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(executableElement -> executableElement.getKind() == ElementKind.CONSTRUCTOR)
        .map(constructor -> new MetaConfigurationConstructor(types, constructor))
        .toList();
  }

  public MethodSpec createDelegatingMethodSpec(ConfigurationPlan plan, FieldSpec delegateField) {
    MethodSpec.Builder builder = MethodSpec.constructorBuilder();
    List<MetaConfigurationConstructorParameter> parameters =
        executableElement.getParameters().stream()
            .map(
                variableElement ->
                    new MetaConfigurationConstructorParameter(types, variableElement))
            .toList();
    parameters.stream()
        .map(MetaConfigurationConstructorParameter::createOverridingParameterSpec)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(builder::addParameter);

    builder.addStatement(
        "$N = new $T($L)",
        delegateField.name,
        delegateField.type,
        parameters.stream()
            .map(parameter -> parameter.createParameterValue(plan))
            .collect(CodeBlock.joining(", ")));
    return builder.build();
  }
}
