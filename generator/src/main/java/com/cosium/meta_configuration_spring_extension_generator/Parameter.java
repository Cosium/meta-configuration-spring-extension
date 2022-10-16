package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.GenerateConfiguration;

/**
 * @author Réda Housni Alaoui
 */
record Parameter(String key, String value) {

  public Parameter(GenerateConfiguration.Parameter parameter) {
    this(parameter.key(), parameter.value());
  }
}
