package com.cosium.meta_configuration_spring_extension_tests;

import com.cosium.meta_configuration_spring_extension.BeansMetadata;
import com.cosium.meta_configuration_spring_extension.InjectBeanName;
import com.cosium.meta_configuration_spring_extension.MetaBean;
import com.cosium.meta_configuration_spring_extension.MetaQualifier;

/**
 * @author Réda Housni Alaoui
 */
public class MyConfiguration {

  public static final String METADATA = "METADATA";
  public static final String FOO_META_ID = "FOO";
  public static final String BAR_META_ID = "BAR";

  private final BeansMetadata beansMetadata;
  private final String fooBeanName;
  private final String barBeanName;

  public MyConfiguration(
      BeansMetadata beansMetadata,
      @InjectBeanName(metaId = FOO_META_ID) String fooBeanName,
      @InjectBeanName(metaId = BAR_META_ID) String barBeanName) {
    this.beansMetadata = beansMetadata;
    this.fooBeanName = fooBeanName;
    this.barBeanName = barBeanName;
  }

  @MetaBean(metaId = METADATA)
  public BeansMetadata beansMetadata() {
    return beansMetadata;
  }

  @MetaBean(metaId = FOO_META_ID)
  public Foo foo() {
    return new Foo(fooBeanName);
  }

  @MetaBean(metaId = BAR_META_ID)
  public Bar bar(@MetaQualifier(metaId = FOO_META_ID) Foo foo) {
    return new Bar(barBeanName, foo);
  }
}
