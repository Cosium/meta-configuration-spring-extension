package com.cosium.meta_configuration_spring_extension_generator;

import static java.util.Objects.requireNonNull;

import com.cosium.meta_configuration_spring_extension.MetaBean;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import java.util.Collections;
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
public class MetaBeanMethod {

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

  public MethodSpec createOverridingMethodSpec(ConfigurationPlan plan) {
    MethodSpec.Builder methodSpecBuilder =
        MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
            .addAnnotation(Override.class)
            .returns(TypeName.get(executableElement.getReturnType()))
            .addModifiers(executableElement.getModifiers().toArray(Modifier[]::new));

    if (isPrimary(plan)) {
      methodSpecBuilder.addAnnotation(Primary.class);
    }

    AnnotationSpec.Builder beanSpecBuilder = AnnotationSpec.builder(Bean.class);
    beanName(plan).ifPresent(beanName -> beanSpecBuilder.addMember("value", "$S", beanName));
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
          "super.$N(%s)", executableElement.getSimpleName().toString(), superCallParams.toString());
    } else {
      methodSpecBuilder.addStatement(
          "return super.$N($L)",
          executableElement.getSimpleName().toString(),
          superCallParams.toString());
    }

    return methodSpecBuilder.build();
  }

  private boolean isPrimary(ConfigurationPlan plan) {
    return plan.getBeanPlan(metaBeanAnnotation.metaId()).map(BeanPlan::primary).orElse(false);
  }

  private Optional<String> beanName(ConfigurationPlan plan) {
    return plan.getBeanPlan(metaBeanAnnotation.metaId()).map(BeanPlan::beanName);
  }

  private List<String> collectBeanAliases(ConfigurationPlan plan) {
    return plan.getBeanPlan(metaBeanAnnotation.metaId())
        .map(BeanPlan::beanAliases)
        .orElseGet(Collections::emptyList);
  }

  private List<MetaBeanMethodParameter> methodParameters() {
    return executableElement.getParameters().stream().map(MetaBeanMethodParameter::new).toList();
  }
}
