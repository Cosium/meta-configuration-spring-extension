package com.cosium.meta_configuration_spring_extension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Réda Housni Alaoui
 */
@Retention(RetentionPolicy.CLASS)
public @interface MetaQualifier {
  String metaId();
}
