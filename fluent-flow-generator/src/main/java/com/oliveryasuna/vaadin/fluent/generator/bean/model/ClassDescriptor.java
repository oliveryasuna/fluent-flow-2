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

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Describes a class.
 *
 * @author Oliver Yasuna
 * @since 2.0.0
 */
public class ClassDescriptor extends FeatureDescriptor {

  // Static fields
  //--------------------------------------------------

  public static final String ABSTRACT = "abstract";

  public static final String FINAL = "final";

  public static final String INTERFACE = "interface";

  public static final String PRIVATE = "private";

  public static final String PROTECTED = "protected";

  public static final String PUBLIC = "public";

  public static final String STATIC = "static";

  public static final String NAME = "name";

  public static final String TYPE_PARAMETERS = "typeParameters";

  public static final String EXTENDS = "extends";

  public static final String IMPLEMENTS = "implements";

  public static final String METHODS = "methods";

  // Constructors
  //--------------------------------------------------

  public ClassDescriptor() {
    super();
  }

  public ClassDescriptor(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
    this();

    setClassOrInterfaceDeclaration(classOrInterfaceDeclaration);
  }

  // Fields
  //--------------------------------------------------

  private ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

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

  public boolean isInterface() {
    return getValue(INTERFACE);
  }

  public void setInterface(final boolean value) {
    setValue(INTERFACE, value);
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

  public String getName() {
    return getValue(NAME);
  }

  public void setName(final String value) {
    setValue(NAME, value);
  }

  public List<TypeParameterTypeDescriptor> getTypeParameters() {
    return getValue(TYPE_PARAMETERS);
  }

  public void setTypeParameters(final List<TypeParameterTypeDescriptor> value) {
    setValue(TYPE_PARAMETERS, value);
  }

  public List<ObjectTypeDescriptor> getExtends() {
    return getValue(EXTENDS);
  }

  public void setExtends(final List<ObjectTypeDescriptor> value) {
    setValue(EXTENDS, value);
  }

  public List<ObjectTypeDescriptor> getImplements() {
    return getValue(IMPLEMENTS);
  }

  public void setImplements(final List<ObjectTypeDescriptor> value) {
    setValue(IMPLEMENTS, value);
  }

  public List<MethodDescriptor> getMethods() {
    return getValue(METHODS);
  }

  public void setMethods(final List<MethodDescriptor> value) {
    setValue(METHODS, value);
  }

  @Override
  protected Map<String, Object> createAttributes() {
    final Map<String, Object> attributes = new HashMap<>();

    final ClassOrInterfaceDeclaration classOrInterfaceDeclaration = getClassOrInterfaceDeclaration();

    attributes.put(ABSTRACT, classOrInterfaceDeclaration.isAbstract());
    attributes.put(FINAL, classOrInterfaceDeclaration.isFinal());
    attributes.put(INTERFACE, classOrInterfaceDeclaration.isInterface());
    attributes.put(PRIVATE, classOrInterfaceDeclaration.isPrivate());
    attributes.put(PROTECTED, classOrInterfaceDeclaration.isProtected());
    attributes.put(PUBLIC, classOrInterfaceDeclaration.isPublic());
    attributes.put(STATIC, classOrInterfaceDeclaration.isStatic());
    attributes.put(NAME, classOrInterfaceDeclaration.getNameAsString());
    attributes.put(TYPE_PARAMETERS, classOrInterfaceDeclaration.getTypeParameters().stream()
        .map(TypeParameterTypeDescriptor::new)
        .collect(Collectors.toList()));
    attributes.put(EXTENDS, classOrInterfaceDeclaration.getExtendedTypes().stream()
        .map(ObjectTypeDescriptor::new)
        .collect(Collectors.toList()));
    attributes.put(IMPLEMENTS, classOrInterfaceDeclaration.getImplementedTypes().stream()
        .map(ObjectTypeDescriptor::new)
        .collect(Collectors.toList()));
    attributes.put(METHODS, classOrInterfaceDeclaration.getMethods().stream()
        .map(MethodDescriptor::new)
        .collect(Collectors.toList()));

    return attributes;
  }

  // Getters/setters
  //--------------------------------------------------

  public ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration() {
    return classOrInterfaceDeclaration;
  }

  public void setClassOrInterfaceDeclaration(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
    this.classOrInterfaceDeclaration = classOrInterfaceDeclaration;
  }

}
