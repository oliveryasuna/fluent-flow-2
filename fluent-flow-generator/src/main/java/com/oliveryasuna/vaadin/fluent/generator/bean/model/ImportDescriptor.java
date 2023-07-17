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

import com.github.javaparser.ast.ImportDeclaration;

import java.util.HashMap;
import java.util.Map;

public class ImportDescriptor extends FeatureDescriptor {

  // Static fields
  //--------------------------------------------------

  public static final String STATIC = "static";

  public static final String PACKAGE = "package";

  public static final String CLASS_NAME = "className";

  // Constructors
  //--------------------------------------------------

  public ImportDescriptor() {
    super();
  }

  public ImportDescriptor(final ImportDeclaration importDeclaration) {
    this();

    setImportDeclaration(importDeclaration);
  }

  // Fields
  //--------------------------------------------------

  private ImportDeclaration importDeclaration;

  // Methods
  //--------------------------------------------------

  public boolean isStatic() {
    return getValue(STATIC);
  }

  public String getPackage() {
    return getValue(PACKAGE);
  }

  public String getClassName() {
    return getValue(CLASS_NAME);
  }

  @Override
  protected Map<String, Object> createAttributes() {
    final Map<String, Object> attributes = new HashMap<>();

    final ImportDeclaration importDeclaration = getImportDeclaration();

    attributes.put(STATIC, importDeclaration.isStatic());

    final String name = importDeclaration.getNameAsString();

    attributes.put(PACKAGE, name.substring(0, name.lastIndexOf('.')));
    attributes.put(CLASS_NAME, name.substring(name.lastIndexOf('.') + 1));

    return attributes;
  }

  // Getters/setters
  //--------------------------------------------------

  public ImportDeclaration getImportDeclaration() {
    return importDeclaration;
  }

  public void setImportDeclaration(final ImportDeclaration importDeclaration) {
    this.importDeclaration = importDeclaration;
  }

}
