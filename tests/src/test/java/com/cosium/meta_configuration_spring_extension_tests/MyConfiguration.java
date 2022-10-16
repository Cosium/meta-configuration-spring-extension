package com.cosium.meta_configuration_spring_extension_tests;

import com.cosium.meta_configuration_spring_extension.BeansMetadata;
import com.cosium.meta_configuration_spring_extension.InjectBeanName;
import com.cosium.meta_configuration_spring_extension.InjectParameterValue;
import com.cosium.meta_configuration_spring_extension.MetaBean;
import com.cosium.meta_configuration_spring_extension.MetaQualifier;

/**
 * @author Réda Housni Alaoui
 */
public class MyConfiguration {

  public static final String CONFIGURATION_ID_KEY = "CONFIGURATION";

  public static final String METADATA_ID = "METADATA";
  public static final String FOO_META_ID = "FOO";
  public static final String BAR_META_ID = "BAR";

  private final BeansMetadata beansMetadata;
  private final String fooBeanName;
  private final String barBeanName;
  private final String configurationId;

  public MyConfiguration(
      BeansMetadata beansMetadata,
      @InjectBeanName(metaId = FOO_META_ID) String fooBeanName,
      @InjectBeanName(metaId = BAR_META_ID) String barBeanName,
      @InjectParameterValue(key = CONFIGURATION_ID_KEY) String configurationId) {
    this.beansMetadata = beansMetadata;
    this.fooBeanName = fooBeanName;
    this.barBeanName = barBeanName;
    this.configurationId = configurationId;
  }

  @MetaBean(metaId = METADATA_ID)
  public BeansMetadata beansMetadata() {
    return beansMetadata;
  }

  @MetaBean(metaId = FOO_META_ID)
  public Foo foo() {
    return new Foo(configurationId, fooBeanName);
  }

  @MetaBean(metaId = BAR_META_ID)
  public Bar bar(@MetaQualifier(metaId = FOO_META_ID) Foo foo) {
    return new Bar(barBeanName, foo);
  }
}
