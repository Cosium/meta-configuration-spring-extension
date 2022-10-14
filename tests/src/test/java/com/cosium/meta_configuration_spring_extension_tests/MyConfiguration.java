package com.cosium.meta_configuration_spring_extension_tests;

import com.cosium.meta_configuration_spring_extension.MetaBean;
import com.cosium.meta_configuration_spring_extension.MetaQualifier;

/**
 * @author Réda Housni Alaoui
 */
public class MyConfiguration {

  public static final String FOO_META_ID = "FOO";
  public static final String BAR_META_ID = "BAR";

  @MetaBean(metaId = FOO_META_ID)
  public Foo foo() {
    return new Foo();
  }

  @MetaBean(metaId = BAR_META_ID)
  public Bar bar(@MetaQualifier(metaId = FOO_META_ID) Foo foo) {
    return new Bar(foo);
  }
}
