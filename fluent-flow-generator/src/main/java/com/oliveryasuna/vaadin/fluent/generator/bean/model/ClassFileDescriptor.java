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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassFileDescriptor extends FeatureDescriptor {

  // Static fields
  //--------------------------------------------------

  public static final String PACKAGE = "package";

  public static final String IMPORTS = "imports";

  public static final String PRIMARY_CLASS = "primaryClass";

  // Constructors
  //--------------------------------------------------

  public ClassFileDescriptor() {
    super();
  }

  public ClassFileDescriptor(final CompilationUnit compilationUnit) {
    this();

    setCompilationUnit(compilationUnit);
  }

  // Fields
  //--------------------------------------------------

  private CompilationUnit compilationUnit;

  // Methods
  //--------------------------------------------------

  public String getPackage() {
    return getValue(PACKAGE);
  }

  public List<ImportDescriptor> getImports() {
    return getValue(IMPORTS);
  }

  public ClassDescriptor getPrimaryClass() {
    return getValue(PRIMARY_CLASS);
  }

  @Override
  protected Map<String, Object> createAttributes() {
    final Map<String, Object> attributes = new HashMap<>();

    final CompilationUnit compilationUnit = getCompilationUnit();

    compilationUnit.getPackageDeclaration().ifPresent(packageDeclaration -> {
      attributes.put(PACKAGE, packageDeclaration.getNameAsString());
    });

    attributes.put(IMPORTS, compilationUnit.getImports().stream()
        .map(ImportDescriptor::new)
        .collect(Collectors.toList()));

    compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(classOrInterfaceDeclaration -> {
      attributes.put(PRIMARY_CLASS, new ClassDescriptor(classOrInterfaceDeclaration));
    });

    return attributes;
  }

  // Getters/setters
  //--------------------------------------------------

  public CompilationUnit getCompilationUnit() {
    return compilationUnit;
  }

  public void setCompilationUnit(final CompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
  }

}
