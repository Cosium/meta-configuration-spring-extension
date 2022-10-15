package com.cosium.meta_configuration_spring_extension_generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Réda Housni Alaoui
 */
public class Configuration {

  private final String packageName;
  private final TypeSpec typeSpec;

  public Configuration(Elements elements, Types types, ConfigurationPlan plan) {
    packageName = plan.generatedConfigurationClassPackageName();

    String sourceConfigurationClassName = plan.sourceConfigurationClassName();
    TypeElement sourceConfigurationType = elements.getTypeElement(sourceConfigurationClassName);

    TypeSpec.Builder typeSpecBuilder =
        TypeSpec.classBuilder(plan.generatedConfigurationClassName());
    if (plan.annotateWithAtConfiguration()) {
      typeSpecBuilder.addAnnotation(org.springframework.context.annotation.Configuration.class);
    }

    FieldSpec delegateField =
        FieldSpec.builder(
                TypeName.get(sourceConfigurationType.asType()),
                "delegate",
                Modifier.PRIVATE,
                Modifier.FINAL)
            .build();
    typeSpecBuilder.addField(delegateField);

    MetaConfigurationConstructor.collect(types, sourceConfigurationType).stream()
        .map(constructor -> constructor.createDelegatingMethodSpec(plan, delegateField))
        .forEach(typeSpecBuilder::addMethod);

    MetaBeanMethod.collect(sourceConfigurationType).stream()
        .map(metaBeanMethod -> metaBeanMethod.createDelegatingMethodSpec(plan, delegateField))
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
