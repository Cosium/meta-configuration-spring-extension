package com.cosium.meta_configuration_spring_extension_generator;

import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * @author Réda Housni Alaoui
 */
public class ExecutableElements {

  public static List<ExecutableElement> collect(TypeElement typeElement) {
    return typeElement.getEnclosedElements().stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .toList();
  }
}
