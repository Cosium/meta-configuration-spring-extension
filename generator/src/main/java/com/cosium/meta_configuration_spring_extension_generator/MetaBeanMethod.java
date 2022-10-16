package com.cosium.meta_configuration_spring_extension_generator;

import static java.util.Objects.requireNonNull;

import com.cosium.meta_configuration_spring_extension.MetaBean;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author Réda Housni Alaoui
 */
class MetaBeanMethod {

  private final ExecutableElement executableElement;
  private final MetaBean metaBeanAnnotation;

  public MetaBeanMethod(ExecutableElement executableElement, MetaBean metaBeanAnnotation) {
    this.executableElement = requireNonNull(executableElement);
    this.metaBeanAnnotation = requireNonNull(metaBeanAnnotation);
  }

  public static List<MetaBeanMethod> collect(TypeElement typeElement) {
    return typeElement.getEnclosedElements().stream()
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(executableElement -> executableElement.getKind() == ElementKind.METHOD)
        .map(MetaBeanMethod::parse)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  public static Optional<MetaBeanMethod> parse(ExecutableElement executableElement) {
    MetaBean annotation = executableElement.getAnnotation(MetaBean.class);
    if (annotation == null) {
      return Optional.empty();
    }
    return Optional.of(new MetaBeanMethod(executableElement, annotation));
  }

  public MethodSpec createDelegatingMethodSpec(ConfigurationPlan plan, FieldSpec delegateField) {
    MethodSpec.Builder methodSpecBuilder =
        MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
            .returns(TypeName.get(executableElement.getReturnType()))
            .addModifiers(executableElement.getModifiers().toArray(Modifier[]::new));

    if (isPrimary(plan)) {
      methodSpecBuilder.addAnnotation(Primary.class);
    }

    AnnotationSpec.Builder beanSpecBuilder = AnnotationSpec.builder(Bean.class);
    beanSpecBuilder.addMember("value", "$S", getBeanName(plan));
    collectBeanAliases(plan).forEach(alias -> beanSpecBuilder.addMember("value", "$S", alias));
    methodSpecBuilder.addAnnotation(beanSpecBuilder.build());

    List<MetaBeanMethodParameter> methodParameters = methodParameters();
    methodParameters.stream()
        .map(parameter -> parameter.createOverridingParameterSpec(plan))
        .forEach(methodSpecBuilder::addParameter);

    CodeBlock superCallParams =
        methodParameters.stream()
            .map(parameter -> CodeBlock.of("$N", parameter.name()))
            .collect(CodeBlock.joining(", "));

    TypeMirror returnType = executableElement.getReturnType();
    if (returnType.getKind() == TypeKind.VOID) {
      methodSpecBuilder.addStatement(
          "$N.$N(%s)",
          delegateField, executableElement.getSimpleName().toString(), superCallParams.toString());
    } else {
      methodSpecBuilder.addStatement(
          "return $N.$N($L)",
          delegateField,
          executableElement.getSimpleName().toString(),
          superCallParams.toString());
    }

    return methodSpecBuilder.build();
  }

  private boolean isPrimary(ConfigurationPlan plan) {
    return plan.requireBeanPlan(metaBeanAnnotation.metaId()).primary();
  }

  private String getBeanName(ConfigurationPlan plan) {
    return plan.requireBeanPlan(metaBeanAnnotation.metaId()).beanName();
  }

  private List<String> collectBeanAliases(ConfigurationPlan plan) {
    return plan.requireBeanPlan(metaBeanAnnotation.metaId()).beanAliases();
  }

  private List<MetaBeanMethodParameter> methodParameters() {
    return executableElement.getParameters().stream().map(MetaBeanMethodParameter::new).toList();
  }
}
