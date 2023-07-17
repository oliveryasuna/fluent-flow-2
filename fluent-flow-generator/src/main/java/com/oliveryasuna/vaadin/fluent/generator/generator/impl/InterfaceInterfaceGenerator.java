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
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.VoidType;
import com.oliveryasuna.vaadin.fluent.generator.Config;
import com.oliveryasuna.vaadin.fluent.generator.generator.Generator;
import com.oliveryasuna.vaadin.fluent.generator.generator.OutputBuilder;
import com.oliveryasuna.vaadin.fluent.generator.utils.NodeUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

// TODO: This class is not up-to-date with the coding standards I followed in
//       `InterfaceBaseGenerator` and `InterfaceConcreteGenerator`.

public final class InterfaceInterfaceGenerator extends Generator {

  // Constructors
  //--------------------------------------------------

  public InterfaceInterfaceGenerator(final Set<Class<?>> generatedClasses) {
    super("interface→interface", generatedClasses);
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

    // Set to interface.
    outputBuilder.setInterface(true);

    // Add extends.
    sourceClass.getExtendedTypes().stream()
        .map(sourceClassExtendedType -> {
          final String sourceClassExtendedTypeName = sourceClassExtendedType.getNameAsString();

          final ClassOrInterfaceType generatedClassExtendedType = new ClassOrInterfaceType();

          if(hasGeneratedClass(sourceClassExtendedTypeName)) {
            final String generatedClassSimpleName = generateInterfaceSimpleName(sourceClassExtendedTypeName);

            outputBuilder.addImport(new ImportDeclaration(
                getGeneratedClassPackageName(sourceClassExtendedTypeName) + "." + generatedClassSimpleName,
                false,
                false
            ));

            generatedClassExtendedType.setName(generatedClassSimpleName);
            generatedClassExtendedType.setTypeArguments(NodeUtils.of(
                generateFluentTypeArguments(sourceClass),
                sourceClassExtendedType.getTypeArguments()
                    .orElse(new NodeList<>())
            ));
          } else {
            // Import already added.

            generatedClassExtendedType.setName(sourceClassExtendedType.getNameAsString());
            generatedClassExtendedType.setTypeArguments(sourceClassExtendedType.getTypeArguments()
                .orElse(null));
          }

          return generatedClassExtendedType;
        })
        .forEachOrdered(outputBuilder::addExtendedType);

    outputBuilder.addExtendedType(new ClassOrInterfaceType(
        null,
        new SimpleName(Config.getIFluentFactoryClass().getSimpleName()),
        generateFluentTypeArguments(sourceClass)
    ));

    sourceClass.getMethods()
        .forEach(sourceMethod -> sourceMethod.accept(this, outputBuilder));

    return true;
  }

  @Override
  public Boolean visit(final MethodDeclaration sourceMethod, final OutputBuilder outputBuilder) {
    if(sourceMethod.isStatic()) {
      return false;
    }

    final String sourceMethodName = sourceMethod.getNameAsString();
    final NodeList<Parameter> sourceMethodParameters = sourceMethod.getParameters();

    final MethodDeclaration generatedMethod = new MethodDeclaration();

    final ClassOrInterfaceType generatedMethodReturnType = generateFluentMethodReturnType(
        sourceMethod,
        sourceMethod.getParentNode()
            .map(ClassOrInterfaceDeclaration.class::cast)
            .orElseThrow()
    );
    final NodeList<Parameter> generatedMethodParameters = new NodeList<>();
    final StringBuilder generatedMethodArgumentList = new StringBuilder();

    for(int i = 0; i < sourceMethodParameters.size(); i++) {
      final Parameter sourceMethodParameter = sourceMethodParameters.get(i);

      generatedMethodParameters.add(NodeUtils.copy(sourceMethodParameter)
          .setFinal(true));

      // Add package local enum imports.
      // This is because enums are not included in "*" imports.

      String sourceMethodParameterTypeName = sourceMethodParameter.getTypeAsString();

      if(sourceMethodParameterTypeName.contains("<")) {
        sourceMethodParameterTypeName = sourceMethodParameterTypeName.substring(0, sourceMethodParameterTypeName.indexOf('<'));
      }

      final String potentialSourceMethodParameterFullTypeName = outputBuilder.getSourceClass().getPackageName() + "." + sourceMethodParameterTypeName;

      try {
        final Class<?> sourceMethodParameterClass = Class.forName(potentialSourceMethodParameterFullTypeName);

        if(!java.lang.reflect.Modifier.isPublic(sourceMethodParameterClass.getModifiers())) {
          outputBuilder.addGenerationError("Parameter type is not accessible: " + sourceMethodParameterTypeName);

          return false;
        }

        if(sourceMethodParameterClass.isEnum()) {
          outputBuilder.addImport(new ImportDeclaration(potentialSourceMethodParameterFullTypeName, false, false));
        }
      } catch(final ClassNotFoundException ignored) {
      }

      // Add to the argument list.

      generatedMethodArgumentList.append(sourceMethodParameter.getNameAsString());

      if(i < sourceMethodParameters.size() - 1) {
        generatedMethodArgumentList.append(", ");
      }
    }

//    generatedMethod.setAnnotations(NodeUtils.copyAll(sourceMethod.getAnnotations()));
    generatedMethod.setModifier(Modifier.Keyword.DEFAULT, true);
    generatedMethod.setTypeParameters(NodeUtils.copyAll(sourceMethod.getTypeParameters()));
    generatedMethod.setType(generatedMethodReturnType);
    generatedMethod.setName(generateFluentMethodName(
        sourceMethodName,
        sourceMethod.getParentNode()
            .map(ClassOrInterfaceDeclaration.class::cast)
            .orElseThrow()
    ));
    generatedMethod.setParameters(generatedMethodParameters);
    generatedMethod.setThrownExceptions(NodeUtils.copyAll(sourceMethod.getThrownExceptions()));

    final BlockStmt blockStmt = new BlockStmt();

    if(sourceMethod.getType() instanceof VoidType) {
      blockStmt.addStatement(String.format("get().%s(%s);", sourceMethodName, generatedMethodArgumentList));
      // TODO: Add empty line.
      blockStmt.addStatement("return uncheckedThis();");
    } else {
      final StringBuilder statement = new StringBuilder();

      statement
          .append("return ")
          .append("new ").append(generatedMethodReturnType.getNameAsString()).append("<>").append('(')
          /**/.append("uncheckedThis(), ")
          /**/.append("get().").append(sourceMethodName).append('(')
          /**//**/.append(generatedMethodArgumentList)
          /**/.append(')')
          .append(')')
          .append(';');

      blockStmt.addStatement(statement.toString());
    }

    generatedMethod.setBody(blockStmt);

    outputBuilder.addMethod(generatedMethod);

    return true;
  }

}
