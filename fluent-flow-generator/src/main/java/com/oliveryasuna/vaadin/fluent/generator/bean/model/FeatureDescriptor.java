/*
 * Copyright 2023 Oliver Yasuna
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oliveryasuna.vaadin.fluent.generator.bean.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Describes a feature.
 *
 * @author Oliver Yasuna
 * @since 2.0.0
 */
public abstract class FeatureDescriptor {

  // Constructors
  //--------------------------------------------------

  protected FeatureDescriptor() {
    super();
  }

  // Fields
  //--------------------------------------------------

  private Map<String, Object> attributes = new HashMap<>();

  // Methods
  //--------------------------------------------------

  protected abstract Map<String, Object> createAttributes();

  public <T> T getValue(final String attributeName) {
    final Object value = getAttributes().get(attributeName);

    if(value == null) {
      return null;
    }

    return (T)value;
  }

  public <T> T getValue(final String attributeName, final Class<T> type) {
    final Object value = getAttributes().get(attributeName);

    if(value == null) {
      return null;
    }

    return type.cast(value);
  }

  public <T> T getValue(final String attributeName, final T defaultValue) {
    final T value = getValue(attributeName);

    if(value == null) {
      return defaultValue;
    }

    return value;
  }

  public <T> T getValue(final String attributeName, final Class<T> type, final T defaultValue) {
    final T value = getValue(attributeName, type);

    if(value == null) {
      return defaultValue;
    }

    return value;
  }

  public <T> Optional<T> findValue(final String attributeName) {
    return Optional.ofNullable(getValue(attributeName));
  }

  public <T> Optional<T> findValue(final String attributeName, final Class<T> type) {
    return Optional.ofNullable(getValue(attributeName, type));
  }

  public void setValue(final String attributeName, final Object value) {
    getAttributes().put(attributeName, value);
  }

  public void computeAttributes(final boolean recursive) {
    final Map<String, Object> attributes = createAttributes();

    if(recursive) {
      attributes.values().forEach(value -> {
        final Class<?> valueClass = value.getClass();

        if(FeatureDescriptor.class.isAssignableFrom(valueClass)) {
          ((FeatureDescriptor)value).computeAttributes(true);
        } else if(FeatureDescriptor[].class.isAssignableFrom(valueClass)) {
          for(final FeatureDescriptor featureDescriptor : (FeatureDescriptor[])value) {
            featureDescriptor.computeAttributes(true);
          }
        } else if(Iterable.class.isAssignableFrom(valueClass)) {
          ((Iterable<?>)value).forEach(item -> {
            if(FeatureDescriptor.class.isAssignableFrom(item.getClass())) {
              ((FeatureDescriptor)item).computeAttributes(true);
            }
          });
        }
      });
    }

    setAttributes(attributes);
  }

  // Getters/setters
  //--------------------------------------------------

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  protected void setAttributes(final Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  // Object methods
  //--------------------------------------------------

  @Override
  public boolean equals(final Object other) {
    if(this == other) return true;
    if(other == null || getClass() != other.getClass()) return false;

    final FeatureDescriptor otherCasted = (FeatureDescriptor)other;

    return new EqualsBuilder()
        .append(attributes, otherCasted.attributes)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(attributes)
        .toHashCode();
  }

}
