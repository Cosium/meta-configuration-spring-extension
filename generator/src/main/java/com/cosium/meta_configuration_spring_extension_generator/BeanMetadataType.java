package com.cosium.meta_configuration_spring_extension_generator;

import com.cosium.meta_configuration_spring_extension.BeanMetadata;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Réda Housni Alaoui
 */
public class BeanMetadataType {

  private final BeanPlan beanPlan;

  public BeanMetadataType(BeanPlan beanPlan) {
    this.beanPlan = beanPlan;
  }

  public CodeBlock createInstantiationCode() {
    CodeBlock aliases =
        beanPlan.beanAliases().stream()
            .map(alias -> CodeBlock.of("$S", alias))
            .collect(CodeBlock.joining(", "));
    return Stream.of(
            CodeBlock.of("$T.builder()", ClassName.get(BeanMetadata.class)),
            CodeBlock.of("metaId($S)", beanPlan.metaId()),
            CodeBlock.of("beanName($S)", beanPlan.beanName()),
            CodeBlock.of("aliases($T.of($L))", ClassName.get(List.class), aliases),
            CodeBlock.of("primary($L)", beanPlan.primary()),
            CodeBlock.of("build()"))
        .collect(CodeBlock.joining("."));
  }
}
