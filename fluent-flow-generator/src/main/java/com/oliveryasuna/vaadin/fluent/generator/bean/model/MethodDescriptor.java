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

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Describes a method.
 *
 * @author Oliver Yasuna
 * @since 2.0.0
 */
public class MethodDescriptor extends FeatureDescriptor {

  // Static fields
  //--------------------------------------------------

  public static final String ABSTRACT = "abstract";

  public static final String FINAL = "final";

  public static final String NATIVE = "native";

  public static final String PRIVATE = "private";

  public static final String PROTECTED = "protected";

  public static final String PUBLIC = "public";

  public static final String STATIC = "static";

  public static final String SYNCHRONIZED = "synchronized";

  public static final String DEFAULT = "default";

  private static final String TYPE_PARAMETERS = "typeParameters";

  public static final String RETURN_TYPE = "returnType";

  public static final String NAME = "name";

  public static final String PARAMETERS = "parameters";

  // Constructors
  //--------------------------------------------------

  public MethodDescriptor() {
    super();
  }

  public MethodDescriptor(final MethodDeclaration methodDeclaration) {
    this();

    setMethodDeclaration(methodDeclaration);
  }

  // Fields
  //--------------------------------------------------

  private MethodDeclaration methodDeclaration;

  // Methods
  //--------------------------------------------------

  public boolean isAbstract() {
    return getValue(ABSTRACT);
  }

  public void setAbstract(final boolean value) {
    setValue(ABSTRACT, value);
  }

  public boolean isFinal() {
    return getValue(FINAL);
  }

  public void setFinal(final boolean value) {
    setValue(FINAL, value);
  }

  public boolean isNative() {
    return getValue(NATIVE);
  }

  public void setNative(final boolean value) {
    setValue(NATIVE, value);
  }

  public boolean isPrivate() {
    return getValue(PRIVATE);
  }

  public void setPrivate(final boolean value) {
    setValue(PRIVATE, value);
  }

  public boolean isProtected() {
    return getValue(PROTECTED);
  }

  public void setProtected(final boolean value) {
    setValue(PROTECTED, value);
  }

  public boolean isPublic() {
    return getValue(PUBLIC);
  }

  public void setPublic(final boolean value) {
    setValue(PUBLIC, value);
  }

  public boolean isStatic() {
    return getValue(STATIC);
  }

  public void setStatic(final boolean value) {
    setValue(STATIC, value);
  }

  public boolean isSynchronized() {
    return getValue(SYNCHRONIZED);
  }

  public void setSynchronized(final boolean value) {
    setValue(SYNCHRONIZED, value);
  }

  public boolean isDefault() {
    return getValue(DEFAULT);
  }

  public void setDefault(final boolean value) {
    setValue(DEFAULT, value);
  }

  public TypeDescriptor<?> getReturnType() {
    return getValue(RETURN_TYPE);
  }

  public void setReturnType(final TypeDescriptor<?> value) {
    setValue(RETURN_TYPE, value);
  }

  public List<TypeDescriptor<?>> getTypeParameters() {
    return getValue(TYPE_PARAMETERS);
  }

  public void setTypeParameters(final List<TypeDescriptor<?>> value) {
    setValue(TYPE_PARAMETERS, value);
  }

  public String getName() {
    return getValue(NAME);
  }

  public void setName(final String value) {
    setValue(NAME, value);
  }

  public List<ParameterDescriptor> getParameters() {
    return getValue(PARAMETERS);
  }

  public void setParameters(final List<ParameterDescriptor> value) {
    setValue(PARAMETERS, value);
  }

  @Override
  protected Map<String, Object> createAttributes() {
    final Map<String, Object> attributes = new HashMap<>();

    final MethodDeclaration methodDeclaration = getMethodDeclaration();

    attributes.put(ABSTRACT, methodDeclaration.isAbstract());
    attributes.put(FINAL, methodDeclaration.isFinal());
    attributes.put(NATIVE, methodDeclaration.isNative());
    attributes.put(PRIVATE, methodDeclaration.isPrivate());
    attributes.put(PROTECTED, methodDeclaration.isProtected());
    attributes.put(PUBLIC, methodDeclaration.isPublic());
    attributes.put(STATIC, methodDeclaration.isStatic());
    attributes.put(SYNCHRONIZED, methodDeclaration.isSynchronized());
    attributes.put(DEFAULT, methodDeclaration.isDefault());
    attributes.put(TYPE_PARAMETERS, methodDeclaration.getTypeParameters().stream()
        .map(TypeDescriptor::of)
        .collect(Collectors.toList()));
    attributes.put(RETURN_TYPE, TypeDescriptor.of(methodDeclaration.getType()));
    attributes.put(NAME, methodDeclaration.getNameAsString());
    attributes.put(PARAMETERS, methodDeclaration.getParameters().stream()
        .map(ParameterDescriptor::new)
        .collect(Collectors.toList()));

    return attributes;
  }

  // Getters/setters
  //--------------------------------------------------

  public MethodDeclaration getMethodDeclaration() {
    return methodDeclaration;
  }

  public void setMethodDeclaration(final MethodDeclaration methodDeclaration) {
    this.methodDeclaration = methodDeclaration;
  }

}
