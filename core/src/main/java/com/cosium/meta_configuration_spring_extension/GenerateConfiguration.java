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

  String CURRENT_PACKAGE = "[CURRENT_PACKAGE]";

  Class<?> sourceConfigurationClass();

  String generatedConfigurationClassPackage() default CURRENT_PACKAGE;

  String generatedConfigurationClassName();

  boolean annotateWithAtConfiguration() default false;

  Bean[] beans() default {};

  @interface Bean {
    String metaId();

    String beanName();

    String[] aliases() default {};

    boolean primary() default false;
  }
}
