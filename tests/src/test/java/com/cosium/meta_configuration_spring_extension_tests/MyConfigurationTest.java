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
          metaId = MyConfiguration.METADATA_ID,
          beanName = AlphaConstants.METADATA_BEAN_NAME),
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.FOO_META_ID,
          beanName = AlphaConstants.FOO_BEAN_NAME,
          aliases = AlphaConstants.FOO_ALIAS,
          primary = true),
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.BAR_META_ID,
          beanName = AlphaConstants.BAR_BEAN_NAME,
          primary = true)
    },
    parameters =
        @GenerateConfiguration.Parameter(
            key = MyConfiguration.CONFIGURATION_ID_KEY,
            value = AlphaConstants.CONFIGURATION_ID))
@GenerateConfiguration(
    sourceConfigurationClass = MyConfiguration.class,
    generatedConfigurationClassName = "BetaConfiguration",
    annotateWithAtConfiguration = true,
    beans = {
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.METADATA_ID,
          beanName = BetaConstants.METADATA_BEAN_NAME),
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.FOO_META_ID,
          beanName = BetaConstants.FOO_BEAN_NAME),
      @GenerateConfiguration.Bean(
          metaId = MyConfiguration.BAR_META_ID,
          beanName = BetaConstants.BAR_BEAN_NAME)
    },
    parameters =
        @GenerateConfiguration.Parameter(
            key = MyConfiguration.CONFIGURATION_ID_KEY,
            value = BetaConstants.CONFIGURATION_ID))
@SpringBootTest
class MyConfigurationTest {

  @Autowired private BeanFactory beanFactory;

  @Qualifier(AlphaConstants.METADATA_BEAN_NAME)
  @Autowired
  private BeansMetadata alphaBeansMetadata;

  @Qualifier(AlphaConstants.FOO_BEAN_NAME)
  @Autowired
  private Foo alphaFoo;

  @Qualifier(AlphaConstants.BAR_BEAN_NAME)
  @Autowired
  private Bar alphaBar;

  @Qualifier(BetaConstants.METADATA_BEAN_NAME)
  @Autowired
  private BeansMetadata betaBeansMetadata;

  @Qualifier(BetaConstants.FOO_BEAN_NAME)
  @Autowired
  private Foo betaFoo;

  @Qualifier(BetaConstants.BAR_BEAN_NAME)
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
    assertThat(alphaFoo.beanName()).isEqualTo(AlphaConstants.FOO_BEAN_NAME);
    assertThat(alphaBar.beanName()).isEqualTo(AlphaConstants.BAR_BEAN_NAME);

    assertThat(betaFoo.beanName()).isEqualTo(BetaConstants.FOO_BEAN_NAME);
    assertThat(betaBar.beanName()).isEqualTo(BetaConstants.BAR_BEAN_NAME);
  }

  @Test
  @DisplayName("BeansMetadata are valid")
  void test5() {
    assertThat(alphaBeansMetadata.byMetaId(MyConfiguration.FOO_META_ID).stream())
        .extracting(BeanMetadata::beanName, BeanMetadata::aliases, BeanMetadata::primary)
        .contains(tuple(AlphaConstants.FOO_BEAN_NAME, List.of(AlphaConstants.FOO_ALIAS), true));

    assertThat(alphaBeansMetadata.byMetaId(MyConfiguration.BAR_META_ID).stream())
        .extracting(BeanMetadata::beanName, BeanMetadata::aliases, BeanMetadata::primary)
        .contains(tuple(AlphaConstants.BAR_BEAN_NAME, List.of(), true));

    assertThat(betaBeansMetadata.byMetaId(MyConfiguration.FOO_META_ID).stream())
        .extracting(BeanMetadata::beanName, BeanMetadata::aliases, BeanMetadata::primary)
        .contains(tuple(BetaConstants.FOO_BEAN_NAME, List.of(), false));

    assertThat(betaBeansMetadata.byMetaId(MyConfiguration.BAR_META_ID).stream())
        .extracting(BeanMetadata::beanName, BeanMetadata::aliases, BeanMetadata::primary)
        .contains(tuple(BetaConstants.BAR_BEAN_NAME, List.of(), false));
  }

  @Test
  @DisplayName("Parameters are correctly injected")
  void test6() {
    assertThat(alphaFoo.configurationId()).isEqualTo(AlphaConstants.CONFIGURATION_ID);
    assertThat(betaFoo.configurationId()).isEqualTo(BetaConstants.CONFIGURATION_ID);
  }
}
