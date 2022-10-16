package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.GenerateConfiguration;
import java.util.Arrays;
import java.util.List;

/**
 * @author Réda Housni Alaoui
 */
class BeanPlan {
  private final GenerateConfiguration.Bean annotation;

  public BeanPlan(GenerateConfiguration.Bean annotation) {
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
}
