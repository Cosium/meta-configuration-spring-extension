package com.cosium.meta_configuration_spring_extension_generator;

import static java.util.Objects.requireNonNull;

import com.cosium.meta_configuration_spring_extension.GenerateConfigurations;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Réda Housni Alaoui
 */
class Configurations {

  private final Elements elements;
  private final Types types;
  private final TypeElement typeElement;

  public Configurations(Elements elements, Types types, TypeElement typeElement) {
    this.elements = requireNonNull(elements);
    this.types = requireNonNull(types);
    this.typeElement = requireNonNull(typeElement);
  }

  public List<Configuration> list() {
    GenerateConfigurations generateConfigurations =
        typeElement.getAnnotation(GenerateConfigurations.class);
    return Arrays.stream(generateConfigurations.value())
        .map(annotation -> new ConfigurationPlan(types, typeElement, annotation))
        .map(configurationPlan -> new Configuration(elements, types, configurationPlan))
        .toList();
  }
}
