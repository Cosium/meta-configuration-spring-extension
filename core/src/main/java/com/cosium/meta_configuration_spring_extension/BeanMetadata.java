package com.cosium.meta_configuration_spring_extension;

import java.util.List;

/**
 * @author Réda Housni Alaoui
 */
public final class BeanMetadata {

  private final String metaId;
  private final String beanName;
  private final List<String> aliases;
  private final boolean primary;

  public BeanMetadata(Builder builder) {
    metaId = builder.metaId;
    beanName = builder.beanName;
    aliases = List.copyOf(builder.aliases);
    primary = builder.primary;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String metaId() {
    return metaId;
  }

  public String beanName() {
    return beanName;
  }

  public List<String> aliases() {
    return aliases;
  }

  public boolean primary() {
    return primary;
  }

  public static class Builder {
    private String metaId;
    private String beanName;
    private List<String> aliases;
    private boolean primary;

    public Builder metaId(String metaId) {
      this.metaId = metaId;
      return this;
    }

    public Builder beanName(String beanName) {
      this.beanName = beanName;
      return this;
    }

    public Builder aliases(List<String> aliases) {
      this.aliases = List.copyOf(aliases);
      return this;
    }

    public Builder primary(boolean primary) {
      this.primary = primary;
      return this;
    }

    public BeanMetadata build() {
      return new BeanMetadata(this);
    }
  }
}
