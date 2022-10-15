package com.cosium.meta_configuration_spring_extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Réda Housni Alaoui
 */
public final class BeansMetadata {

  private final Map<String, BeanMetadata> beanMetadataByMetaId;

  public BeansMetadata(Builder builder) {
    beanMetadataByMetaId = Map.copyOf(builder.beanMetadataByMetaId);
  }

  public static Builder builder() {
    return new Builder();
  }

  public Optional<BeanMetadata> byMetaId(String metaId) {
    return Optional.ofNullable(beanMetadataByMetaId.get(metaId));
  }

  public static class Builder {
    private final Map<String, BeanMetadata> beanMetadataByMetaId = new HashMap<>();

    public Builder addBean(BeanMetadata beanMetadata) {
      String metaId = beanMetadata.metaId();
      BeanMetadata old = beanMetadataByMetaId.putIfAbsent(metaId, beanMetadata);
      if (old != null) {
        throw new IllegalArgumentException(old + " already exists for meta-id '" + metaId + "'");
      }
      return this;
    }

    public BeansMetadata build() {
      return new BeansMetadata(this);
    }
  }
}
