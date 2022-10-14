package com.cosium.meta_configuration_spring_extension_generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author Réda Housni Alaoui
 */
public class Configuration {

  private final String packageName;
  private final TypeSpec typeSpec;

  public Configuration(Elements elements, ConfigurationPlan plan) {
    packageName = plan.generatedConfigurationClassPackageName();

    String sourceConfigurationClassName = plan.sourceConfigurationClassName();
    TypeElement sourceConfigurationType = elements.getTypeElement(sourceConfigurationClassName);

    TypeSpec.Builder typeSpecBuilder =
        TypeSpec.classBuilder(plan.generatedConfigurationClassName());
    if (plan.annotateWithAtConfiguration()) {
      typeSpecBuilder.addAnnotation(org.springframework.context.annotation.Configuration.class);
    }

    typeSpecBuilder.superclass(sourceConfigurationType.asType());

    MetaBeanMethod.collect(sourceConfigurationType).stream()
        .map(metaBeanMethod -> metaBeanMethod.createOverridingMethodSpec(plan))
        .forEach(typeSpecBuilder::addMethod);

    typeSpec = typeSpecBuilder.build();
  }

  public void writeTo(Filer filer) {
    try {
      JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
