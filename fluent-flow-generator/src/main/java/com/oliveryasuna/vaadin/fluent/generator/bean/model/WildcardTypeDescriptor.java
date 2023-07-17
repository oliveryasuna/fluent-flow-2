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

import com.github.javaparser.ast.type.WildcardType;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes a wildcard type.
 *
 * @author Oliver Yasuna
 * @since 2.0.0
 */
public class WildcardTypeDescriptor extends TypeDescriptor<WildcardType> {

  // Static fields
  //--------------------------------------------------

  public static final String EXTEND = "extend";

  public static final String SUPER = "super";

  // Constructors
  //--------------------------------------------------

  public WildcardTypeDescriptor() {
    super();
  }

  public WildcardTypeDescriptor(final WildcardType wildcardType) {
    this();

    setWildcardType(wildcardType);
  }

  // Fields
  //--------------------------------------------------

  private WildcardType wildcardType;

  // Methods
  //--------------------------------------------------

  public TypeDescriptor<?> getExtend() {
    return getValue(EXTEND);
  }

  public void setExtend(final TypeDescriptor<?> extend) {
    setValue(EXTEND, extend);
  }

  public TypeDescriptor<?> getSuper() {
    return getValue(SUPER);
  }

  public void setSuper(final TypeDescriptor<?> superType) {
    setValue(SUPER, superType);
  }

  @Override
  protected Map<String, Object> createAttributes() {
    final Map<String, Object> attributes = new HashMap<>();

    final WildcardType wildcardType = getWildcardType();

    attributes.put(NAME, "?");

    wildcardType.getExtendedType().ifPresent(extendedType -> {
      attributes.put(EXTEND, of(extendedType));
    });
    wildcardType.getSuperType().ifPresent(superType -> {
      attributes.put(SUPER, of(superType));
    });

    return attributes;
  }

  // Getters/setters
  //--------------------------------------------------

  public WildcardType getWildcardType() {
    return wildcardType;
  }

  public void setWildcardType(final WildcardType wildcardType) {
    this.wildcardType = wildcardType;
  }

}
