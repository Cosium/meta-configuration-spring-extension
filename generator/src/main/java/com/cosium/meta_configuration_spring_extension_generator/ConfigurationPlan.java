package com.cosium.meta_configuration_spring_extension_generator;

import static java.util.Objects.requireNonNull;

import com.cosium.meta_configuration_spring_extension.GenerateConfiguration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor14;
import javax.lang.model.util.Types;

/**
 * @author Réda Housni Alaoui
 */
class ConfigurationPlan {

  private final Types types;
  private final GenerateConfiguration annotation;
  private final Map<String, BeanPlan> beanByMetaId;
  private final Map<String, Parameter> parameterByKey;
  private final String currentPackageName;

  public ConfigurationPlan(
      Types types, TypeElement annotationCarrier, GenerateConfiguration annotation) {
    this.types = types;
    this.annotation = annotation;
    beanByMetaId =
        Arrays.stream(annotation.beans())
            .collect(
                Collectors.toMap(
                    GenerateConfiguration.Bean::metaId,
                    annotation1 -> new BeanPlan(types, annotation1)));
    parameterByKey =
        Arrays.stream(annotation.parameters())
            .collect(Collectors.toMap(GenerateConfiguration.Parameter::key, Parameter::new));

    String currentPackageCandidate =
        annotationCarrier.getEnclosingElement().accept(new PackageNameExtractor(), null);
    currentPackageName = requireNonNull(currentPackageCandidate);
  }

  public String sourceConfigurationClassName() {
    return Classes.computeClassName(types, annotation::sourceConfigurationClass);
  }

  public String generatedConfigurationClassPackageName() {
    String targetPackage = annotation.generatedConfigurationClassPackage();
    if (GenerateConfiguration.CURRENT_PACKAGE.equals(targetPackage)) {
      return currentPackageName;
    }
    return targetPackage;
  }

  public String generatedConfigurationClassName() {
    return annotation.generatedConfigurationClassName();
  }

  public boolean annotateWithAtConfiguration() {
    return annotation.annotateWithAtConfiguration();
  }

  public List<String> dependsOn() {
    return Arrays.stream(annotation.dependsOn()).toList();
  }

  public BeanPlan requireBeanPlan(String metaId) {
    return Optional.ofNullable(beanByMetaId.get(metaId))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "No "
                        + GenerateConfiguration.Bean.class
                        + " found for meta-id '"
                        + metaId
                        + "'"));
  }

  public Parameter requireParameter(String parameterKey) {
    return Optional.ofNullable(parameterByKey.get(parameterKey))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "No "
                        + GenerateConfiguration.Parameter.class
                        + " found for key '"
                        + parameterKey
                        + "'"));
  }

  public List<BeanPlan> beanPlans() {
    return beanByMetaId.values().stream().sorted(Comparator.comparing(BeanPlan::metaId)).toList();
  }

  private static class PackageNameExtractor extends SimpleElementVisitor14<String, Void> {
    @Override
    public String visitPackage(PackageElement packageElement, Void unused) {
      return packageElement.getQualifiedName().toString();
    }
  }
}
