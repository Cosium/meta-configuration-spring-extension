package com.cosium.meta_configuration_spring_extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Réda Housni Alaoui
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.PACKAGE})
@Repeatable(GenerateConfigurations.class)
public @interface GenerateConfiguration {
  Class<?> sourceConfigurationClass();

  String generatedConfigurationClassName();

  boolean annotateWithAtConfiguration() default false;

  GeneratedBean[] generatedBeans() default {};

  @interface GeneratedBean {
    String metaId();

    String[] beanNames() default {};

    boolean primary() default false;
  }
}
