package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.GenerateConfigurations;
import com.google.auto.service.AutoService;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Réda Housni Alaoui
 */
@AutoService(Processor.class)
public class Generator extends AbstractProcessor {
  private static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;

  private Elements elements;
  private Types types;
  private Filer filer;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.elements = processingEnv.getElementUtils();
    this.types = processingEnv.getTypeUtils();
    this.filer = processingEnv.getFiler();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver() || annotations.isEmpty()) {
      return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    roundEnv.getElementsAnnotatedWith(GenerateConfigurations.class).stream()
        .map(TypeElement.class::cast)
        .map(typeElement -> new Configurations(elements, types, typeElement))
        .map(Configurations::list)
        .flatMap(Collection::stream)
        .forEach(configuration -> configuration.writeTo(filer));

    return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(GenerateConfigurations.class.getCanonicalName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
