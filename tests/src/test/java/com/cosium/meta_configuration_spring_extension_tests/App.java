package com.cosium.meta_configuration_spring_extension_tests;

import com.cosium.meta_configuration_spring_extension.GenerateConfiguration;

/**
 * @author Réda Housni Alaoui
 */
@GenerateConfiguration(
    sourceConfigurationClass = MyConfiguration.class,
    generatedConfigurationClassName = "AlphaConfiguration",
    annotateWithAtConfiguration = true,
    beans = {
      @GenerateConfiguration.MetaBean(
          metaId = MyConfiguration.FOO_META_ID,
          beanNames = AlphaBeanNames.FOO,
          primary = true),
      @GenerateConfiguration.MetaBean(
          metaId = MyConfiguration.BAR_META_ID,
          beanNames = AlphaBeanNames.BAR,
          primary = true)
    })
@GenerateConfiguration(
    sourceConfigurationClass = MyConfiguration.class,
    generatedConfigurationClassName = "BetaConfiguration",
    annotateWithAtConfiguration = true,
    beans = {
      @GenerateConfiguration.MetaBean(
          metaId = MyConfiguration.FOO_META_ID,
          beanNames = BetaBeanNames.FOO),
      @GenerateConfiguration.MetaBean(
          metaId = MyConfiguration.BAR_META_ID,
          beanNames = BetaBeanNames.BAR)
    })
public class App {}
