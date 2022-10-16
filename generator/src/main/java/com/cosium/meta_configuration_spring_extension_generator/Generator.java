package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.GenerateConfiguration;
import com.cosium.meta_configuration_spring_extension.GenerateConfigurations;
import com.google.auto.service.AutoService;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author Réda Housni Alaoui
 */
@AutoService(Processor.class)
public class Generator extends AbstractProcessor {
  private static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;

  private Elements elements;
  private Types types;
  private Filer filer;
  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elements = processingEnv.getElementUtils();
    types = processingEnv.getTypeUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver() || annotations.isEmpty()) {
      return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    roundEnv.getElementsAnnotatedWithAny(getSupportedAnnotationClasses()).stream()
        .map(TypeElement.class::cast)
        .forEach(this::process);

    return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
  }

  private void process(TypeElement typeElement) {
    try {
      new Configurations(elements, types, typeElement)
          .list()
          .forEach(configuration -> configuration.writeTo(filer));
    } catch (RuntimeException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), typeElement);
    }
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return getSupportedAnnotationClasses().stream()
        .map(Class::getCanonicalName)
        .collect(Collectors.toSet());
  }

  private Set<Class<? extends Annotation>> getSupportedAnnotationClasses() {
    return Set.of(GenerateConfigurations.class, GenerateConfiguration.class);
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
