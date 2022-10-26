package com.cosium.meta_configuration_spring_extension_generator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.util.Types;

/**
 * @author Réda Housni Alaoui
 */
class Classes {

  private Classes() {}

  public static List<String> computeClassNames(Types types, Supplier<Class<?>[]> classes) {
    try {
      return Arrays.stream(classes.get()).map(Class::getCanonicalName).toList();
    } catch (MirroredTypesException e) {
      return e.getTypeMirrors().stream()
          .map(types::asElement)
          .map(TypeElement.class::cast)
          .map(TypeElement::getQualifiedName)
          .map(Object::toString)
          .toList();
    }
  }

  public static String computeClassName(Types types, Supplier<Class<?>> classSupplier) {
    TypeElement sourceConfigurationElement;
    try {
      return classSupplier.get().getCanonicalName();
    } catch (MirroredTypeException e) {
      sourceConfigurationElement = (TypeElement) types.asElement(e.getTypeMirror());
    }
    return sourceConfigurationElement.getQualifiedName().toString();
  }
}
