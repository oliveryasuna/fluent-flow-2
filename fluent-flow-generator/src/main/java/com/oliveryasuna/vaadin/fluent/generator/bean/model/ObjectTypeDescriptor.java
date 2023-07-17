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

import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Describes an object type.
 *
 * @author Oliver Yasuna
 * @since 2.0.0
 */
public class ObjectTypeDescriptor extends TypeDescriptor<ClassOrInterfaceType> {

  // Static fields
  //--------------------------------------------------

  public static final String TYPE_ARGUMENTS = "typeArguments";

  // Constructors
  //--------------------------------------------------

  public ObjectTypeDescriptor() {
    super();
  }

  public ObjectTypeDescriptor(final ClassOrInterfaceType classOrInterfaceType) {
    this();

    setJpType(classOrInterfaceType);
  }

  // Methods
  //--------------------------------------------------

  public List<? extends TypeDescriptor<?>> getTypeArguments() {
    return getValue(TYPE_ARGUMENTS);
  }

  public void setTypeArguments(final List<? extends TypeDescriptor<?>> typeArguments) {
    setValue(TYPE_ARGUMENTS, typeArguments);
  }

  @Override
  protected Map<String, Object> createAttributes() {
    final Map<String, Object> attributes = new HashMap<>();

    final ClassOrInterfaceType classOrInterfaceType = getJpType();

    attributes.put(NAME, classOrInterfaceType.getNameWithScope());
    attributes.put(TYPE_ARGUMENTS, classOrInterfaceType.getTypeArguments()
        .stream()
        .flatMap(Collection::stream)
        .map(TypeDescriptor::of)
        .collect(Collectors.toList()));

    return attributes;
  }

}
