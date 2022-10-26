package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.GenerateConfiguration;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.util.Types;

/**
 * @author Réda Housni Alaoui
 */
class BeanPlan {

  private final Types types;
  private final GenerateConfiguration.Bean annotation;

  public BeanPlan(Types types, GenerateConfiguration.Bean annotation) {
    this.types = types;
    this.annotation = annotation;
  }

  public String metaId() {
    return annotation.metaId();
  }

  public boolean primary() {
    return annotation.primary();
  }

  public String beanName() {
    return annotation.beanName();
  }

  public List<String> beanAliases() {
    return Arrays.stream(annotation.aliases()).toList();
  }

  public List<String> qualifyingAnnotations() {
    return Classes.computeClassNames(types, annotation::qualifyingAnnotations);
  }
}
