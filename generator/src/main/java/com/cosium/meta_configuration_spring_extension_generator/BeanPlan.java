package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.GenerateConfiguration;
import java.util.Arrays;
import java.util.List;

/**
 * @author Réda Housni Alaoui
 */
public class BeanPlan {
  private final GenerateConfiguration.GeneratedBean annotation;

  public BeanPlan(GenerateConfiguration.GeneratedBean annotation) {
    this.annotation = annotation;
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
