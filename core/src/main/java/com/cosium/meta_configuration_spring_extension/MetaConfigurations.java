package com.cosium.meta_configuration_spring_extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author Réda Housni Alaoui
 */
@Target(ElementType.TYPE)
public @interface MetaConfigurations {
  GenerateConfiguration[] value();
}
