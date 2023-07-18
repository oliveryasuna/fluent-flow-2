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

package com.oliveryasuna.vaadin.fluent.generator.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.oliveryasuna.vaadin.fluent.generator.utils.NodeUtils;

import java.util.ArrayList;
import java.util.List;

public final class OutputBuilder {

  // Constructors
  //--------------------------------------------------

  public OutputBuilder(final Class<?> sourceClass, final CompilationUnit sourceFile) {
    super();

    this.sourceClass = sourceClass;
    this.sourceFile = sourceFile;
  }

  // Fields
  //--------------------------------------------------

  // TODO: Really more of a context variable.
  private final Class<?> sourceClass;

  // TODO: Really more of a context variable.
  private final CompilationUnit sourceFile;

  private String packageName;

  private final NodeList<ImportDeclaration> imports = new NodeList<>();

  private String classJavadoc;

  private final NodeList<Modifier> classModifiers = new NodeList<>();

  private boolean isInterface;

  private String className;

  private final NodeList<TypeParameter> typeParameters = new NodeList<>();

  private final NodeList<ClassOrInterfaceType> extendedTypes = new NodeList<>();

  private final NodeList<ClassOrInterfaceType> implementedTypes = new NodeList<>();

  private final NodeList<ConstructorDeclaration> constructors = new NodeList<>();

  private final NodeList<FieldDeclaration> fields = new NodeList<>();

  private final NodeList<MethodDeclaration> methods = new NodeList<>();

  private final List<String> generationWarnings = new ArrayList<>();

  private final List<String> generationErrors = new ArrayList<>();

  // Methods
  //--------------------------------------------------

  public OutputBuilder addImport(final ImportDeclaration import_) {
    imports.add(NodeUtils.copy(import_));

    return this;
  }

  public OutputBuilder addClassModifier(final Modifier modifier) {
    classModifiers.add(NodeUtils.copy(modifier));

    return this;
  }

  public OutputBuilder addTypeParameter(final TypeParameter typeParameter) {
    typeParameters.add(NodeUtils.copy(typeParameter));

    return this;
  }

  public OutputBuilder addExtendedType(final ClassOrInterfaceType extendedType) {
    extendedTypes.add(NodeUtils.copy(extendedType));

    return this;
  }

  public OutputBuilder addImplementedType(final ClassOrInterfaceType implementedType) {
    implementedTypes.add(NodeUtils.copy(implementedType));

    return this;
  }

  public OutputBuilder addConstructor(final ConstructorDeclaration constructor) {
    constructors.add(NodeUtils.copy(constructor));

    return this;
  }

  public OutputBuilder addField(final FieldDeclaration field) {
    fields.add(NodeUtils.copy(field));

    return this;
  }

  public OutputBuilder addMethod(final MethodDeclaration method) {
    methods.add(NodeUtils.copy(method));

    return this;
  }

  public OutputBuilder addGenerationWarning(final String warning) {
    generationWarnings.add(warning);

    return this;
  }

  public OutputBuilder addGenerationError(final String error) {
    generationErrors.add(error);

    return this;
  }

  public CompilationUnit build() {
    return new CompilationUnit()
        .setPackageDeclaration(packageName)
        .setImports(imports)
        .setTypes(NodeList.nodeList(new ClassOrInterfaceDeclaration()
            .setJavadocComment(classJavadoc)
            .setModifiers(classModifiers)
            .setInterface(isInterface)
            .setName(className)
            .setTypeParameters(typeParameters)
            .setExtendedTypes(extendedTypes)
            .setImplementedTypes(implementedTypes)
            .setMembers(NodeUtils.of(
                constructors,
                fields,
                methods
            ))));
  }

  // Getters/setters
  //--------------------------------------------------

  public Class<?> getSourceClass() {
    return sourceClass;
  }

  public CompilationUnit getSourceFile() {
    return sourceFile;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  public NodeList<ImportDeclaration> getImports() {
    return imports;
  }

  public String getClassJavadoc() {
    return classJavadoc;
  }

  public void setClassJavadoc(final String classJavadoc) {
    this.classJavadoc = classJavadoc;
  }

  public NodeList<Modifier> getClassModifiers() {
    return classModifiers;
  }

  public boolean isInterface() {
    return isInterface;
  }

  public void setInterface(final boolean anInterface) {
    isInterface = anInterface;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(final String className) {
    this.className = className;
  }

  public NodeList<TypeParameter> getTypeParameters() {
    return typeParameters;
  }

  public NodeList<ClassOrInterfaceType> getExtendedTypes() {
    return extendedTypes;
  }

  public NodeList<ClassOrInterfaceType> getImplementedTypes() {
    return implementedTypes;
  }

  public NodeList<ConstructorDeclaration> getConstructors() {
    return constructors;
  }

  public NodeList<FieldDeclaration> getFields() {
    return fields;
  }

  public NodeList<MethodDeclaration> getMethods() {
    return methods;
  }

  public List<String> getGenerationWarnings() {
    return generationWarnings;
  }

  public List<String> getGenerationErrors() {
    return generationErrors;
  }

}
