package com.cosium.meta_configuration_spring_extension_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.cosium.meta_configuration_spring_extension.BeanMetadata;
import com.cosium.meta_configuration_spring_extension.BeansMetadata;
import com.cosium.meta_configuration_spring_extension.GenerateConfiguration;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Réda Housni Alaoui
 */
@GenerateConfiguration(
    sourceConfigurationClass = MyConfiguration.class,
    generatedConfigurationClassName = "AlphaConfiguration",
    annotateWithAtConfiguration = true,
    beans = {
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.METADATA,
          beanName = AlphaBeanNames.METADATA),
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.FOO_META_ID,
          beanName = AlphaBeanNames.FOO,
          aliases = AlphaBeanNames.FOO_ALIAS,
          primary = true),
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.BAR_META_ID,
          beanName = AlphaBeanNames.BAR,
          primary = true)
    })
@GenerateConfiguration(
    sourceConfigurationClass = MyConfiguration.class,
    generatedConfigurationClassName = "BetaConfiguration",
    annotateWithAtConfiguration = true,
    beans = {
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.METADATA,
          beanName = BetaBeanNames.METADATA),
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.FOO_META_ID,
          beanName = BetaBeanNames.FOO),
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.BAR_META_ID,
          beanName = BetaBeanNames.BAR)
    })
@SpringBootTest
class MyConfigurationTest {

  @Autowired private BeanFactory beanFactory;

  @Qualifier(AlphaBeanNames.METADATA)
  @Autowired
  private BeansMetadata alphaBeansMetadata;

  @Qualifier(AlphaBeanNames.FOO)
  @Autowired
  private Foo alphaFoo;

  @Qualifier(AlphaBeanNames.BAR)
  @Autowired
  private Bar alphaBar;

  @Qualifier(BetaBeanNames.METADATA)
  @Autowired
  private BeansMetadata betaBeansMetadata;

  @Qualifier(BetaBeanNames.FOO)
  @Autowired
  private Foo betaFoo;

  @Qualifier(BetaBeanNames.BAR)
  @Autowired
  private Bar betaBar;

  @Test
  @DisplayName("Beans are distinct")
  void test1() {
    assertThat(alphaFoo).isNotSameAs(betaFoo);
    assertThat(alphaBar).isNotSameAs(betaBar);
  }

  @Test
  @DisplayName("alpha are primaries")
  void test2() {
    assertThat(beanFactory.getBean(Foo.class)).isSameAs(alphaFoo);
    assertThat(beanFactory.getBean(Bar.class)).isSameAs(alphaBar);
  }

  @Test
  @DisplayName("each bar holds the correct foo instance")
  void test3() {
    assertThat(alphaBar.foo()).isSameAs(alphaFoo);
    assertThat(betaBar.foo()).isSameAs(betaFoo);
  }

  @Test
  @DisplayName("Bean names are correctly injected")
  void test4() {
    assertThat(alphaFoo.beanName()).isEqualTo(AlphaBeanNames.FOO);
    assertThat(alphaBar.beanName()).isEqualTo(AlphaBeanNames.BAR);

    assertThat(betaFoo.beanName()).isEqualTo(BetaBeanNames.FOO);
    assertThat(betaBar.beanName()).isEqualTo(BetaBeanNames.BAR);
  }

  @Test
  @DisplayName("BeansMetadata are valid")
  void test5() {
    assertThat(alphaBeansMetadata.byMetaId(MyConfiguration.FOO_META_ID).stream())
        .extracting(BeanMetadata::beanName, BeanMetadata::aliases, BeanMetadata::primary)
        .contains(tuple(AlphaBeanNames.FOO, List.of(AlphaBeanNames.FOO_ALIAS), true));

    assertThat(alphaBeansMetadata.byMetaId(MyConfiguration.BAR_META_ID).stream())
        .extracting(BeanMetadata::beanName, BeanMetadata::aliases, BeanMetadata::primary)
        .contains(tuple(AlphaBeanNames.BAR, List.of(), true));

    assertThat(betaBeansMetadata.byMetaId(MyConfiguration.FOO_META_ID).stream())
        .extracting(BeanMetadata::beanName, BeanMetadata::aliases, BeanMetadata::primary)
        .contains(tuple(BetaBeanNames.FOO, List.of(), false));

    assertThat(betaBeansMetadata.byMetaId(MyConfiguration.BAR_META_ID).stream())
        .extracting(BeanMetadata::beanName, BeanMetadata::aliases, BeanMetadata::primary)
        .contains(tuple(BetaBeanNames.BAR, List.of(), false));
  }
}
