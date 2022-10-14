package com.cosium.meta_configuration_spring_extension_tests;

import com.cosium.meta_configuration_spring_extension.GenerateConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Réda Housni Alaoui
 */
@GenerateConfiguration(
    sourceConfigurationClass = MyConfiguration.class,
    generatedConfigurationClassName = "AlphaConfiguration",
    annotateWithAtConfiguration = true,
    generatedBeans = {
      @GenerateConfiguration.GeneratedBean(
          metaId = MyConfiguration.FOO_META_ID,
          beanName = AlphaBeanNames.FOO,
          primary = true),
      @GenerateConfiguration.GeneratedBean(
          metaId = MyConfiguration.BAR_META_ID,
          beanName = AlphaBeanNames.BAR,
          primary = true)
    })
@GenerateConfiguration(
    sourceConfigurationClass = MyConfiguration.class,
    generatedConfigurationClassName = "BetaConfiguration",
    annotateWithAtConfiguration = true,
    generatedBeans = {
      @GenerateConfiguration.GeneratedBean(
          metaId = MyConfiguration.FOO_META_ID,
          beanName = BetaBeanNames.FOO),
      @GenerateConfiguration.GeneratedBean(
          metaId = MyConfiguration.BAR_META_ID,
          beanName = BetaBeanNames.BAR)
    })
@SpringBootApplication
public class App {}
