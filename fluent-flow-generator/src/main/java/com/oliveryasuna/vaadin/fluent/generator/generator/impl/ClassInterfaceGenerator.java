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

package com.oliveryasuna.vaadin.fluent.generator.generator.impl;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.oliveryasuna.vaadin.fluent.generator.Config;
import com.oliveryasuna.vaadin.fluent.generator.generator.Generator;
import com.oliveryasuna.vaadin.fluent.generator.generator.OutputBuilder;
import com.oliveryasuna.vaadin.fluent.generator.utils.NodeUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ClassInterfaceGenerator extends Generator {

  // Constructors
  //--------------------------------------------------

  public ClassInterfaceGenerator(final Set<Class<?>> generatedClasses) {
    super("class→interface", generatedClasses);
  }

  // Methods
  //--------------------------------------------------

  // Generation
  //

  @Override
  protected String generateClassSimpleName(final ClassOrInterfaceDeclaration sourceClass) {
    return generateInterfaceSimpleName(sourceClass.getNameAsString());
  }

  @Override
  protected NodeList<TypeParameter> generateTypeParameters(final ClassOrInterfaceDeclaration sourceClass) {
    return NodeUtils.of(generateFluentTypeParameters(sourceClass), NodeUtils.copyAll(sourceClass.getTypeParameters()));
  }

  @Override
  protected NodeList<Type> generateTypeArguments(final ClassOrInterfaceDeclaration sourceClass) {
    return NodeUtils.of(generateFluentTypeArguments(sourceClass), NodeUtils.typeArgumentsFromTypeParameters(sourceClass.getTypeParameters()));
  }

  @Override
  protected ClassOrInterfaceType generateSubclassTypeWithTypeArguments(final ClassOrInterfaceDeclaration sourceClass) {
    return new ClassOrInterfaceType()
        .setName(generateInterfaceSimpleName(sourceClass.getNameAsString()))
        .setTypeArguments(generateTypeArguments(sourceClass));
  }

  @Override
  protected String generateJavadoc(final ClassOrInterfaceDeclaration sourceClass) {
    return """
        Fluent interface for {@link %s}.
        <p>
        THIS IS A GENERATED FILE.
        <p>
        Date: %s<br/>
        Vaadin: %s

        @param <%s> The type of the wrapped object.
        @param <%s> The type of the factory.
        %s
        @author Oliver Yasuna"""
        .formatted(
            sourceClass.getNameAsString(),
            LocalDate.now().format(DATE_TIME_FORMATTER),
            Config.getVaadinVersion(),
            getWrappedTypeParameterName(),
            getSubclassTypeParameterName(),
            Optional.ofNullable(generateWrappedTypeParametersJavadoc(sourceClass))
                .filter(Predicate.not(String::isEmpty))
                .map(value -> value + "\n")
                .orElse("")
        );
  }

  // Visiting
  //

  @Override
  public Boolean visit(final ClassOrInterfaceDeclaration sourceClass, final OutputBuilder outputBuilder) {
    if(Boolean.FALSE.equals(super.visit(sourceClass, outputBuilder))) {
      return false;
    }

    // Make an interface.

    outputBuilder.setInterface(true);

    // Extend `IFluentFactory`.

    outputBuilder.addExtendedType(new ClassOrInterfaceType()
        .setName(Config.getIFluentFactoryClass().getSimpleName())
        .setTypeArguments(generateFluentTypeArguments(sourceClass)));

    // Add extended types.

    for(final ClassOrInterfaceType sourceClassExtendedType : sourceClass.getExtendedTypes()) {
      final String sourceClassExtendedTypeSimpleName = sourceClassExtendedType.getNameAsString();

      // We only need to implement generated interfaces.
      if(!hasGeneratedClass(sourceClassExtendedTypeSimpleName)) {
        continue;
      }

      // Get the generated interface name.

      final String generatedInterfaceSimpleName = generateInterfaceSimpleName(sourceClassExtendedTypeSimpleName);

      // Add import.

      outputBuilder.addImport(new ImportDeclaration(
          getGeneratedClassPackageName(sourceClassExtendedTypeSimpleName) + "." + generatedInterfaceSimpleName,
          false,
          false
      ));

      // Add extended type.

      outputBuilder.addExtendedType(new ClassOrInterfaceType()
          .setName(generatedInterfaceSimpleName)
          .setTypeArguments(NodeUtils.of(
              generateFluentTypeArguments(sourceClass),
              sourceClassExtendedType.getTypeArguments()
                  .orElse(null)
          )));
    }

    // Visit methods.

    sourceClass.getMethods()
        .forEach(sourceClassMethod -> sourceClassMethod.accept(this, outputBuilder));

    return true;
  }

  @Override
  public Boolean visit(final MethodDeclaration sourceMethod, final OutputBuilder outputBuilder) {
    // Skip non-public, static methods, object methods, and those that contain
    // "$$".

    final String sourceMethodName = sourceMethod.getNameAsString();

    if(!sourceMethod.isPublic() || sourceMethod.isStatic() || sourceMethodName.equals("equals") || sourceMethodName.equals("hashCode")
        || sourceMethodName.equals("toString") || sourceMethodName.contains("$$")) {
      return false;
    }

    // Generate the method.

    outputBuilder.addMethod(new MethodDeclaration()
        .setDefault(true)
        .setTypeParameters(NodeUtils.copyAll(sourceMethod.getTypeParameters()))
        .setType(generateFluentMethodReturnType(
            sourceMethod,
            NodeUtils.getParentClass(sourceMethod)
                .orElseThrow(),
            outputBuilder
        ))
        .setName(generateFluentMethodName(
            sourceMethod.getNameAsString(),
            NodeUtils.getParentClass(sourceMethod)
                .orElseThrow()
        ))
        .setParameters(sourceMethod.getParameters().stream()
            .map(sourceMethodParameter -> new Parameter()
                .setFinal(true)
                .setType(resolveType(sourceMethodParameter.getType(), outputBuilder))
                .setName(sourceMethodParameter.getNameAsString()))
            .collect(Collectors.toCollection(NodeList::new)))
        .setThrownExceptions(NodeUtils.copyAll(sourceMethod.getThrownExceptions()))
        .setBody(generateFluentMethodBody(
            sourceMethod,
            NodeUtils.getParentClass(sourceMethod)
                .orElseThrow(),
            outputBuilder
        )));

    return true;
  }

}
