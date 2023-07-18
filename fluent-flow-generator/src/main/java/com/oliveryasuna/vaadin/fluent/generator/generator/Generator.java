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

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.oliveryasuna.vaadin.fluent.generator.Config;
import com.oliveryasuna.vaadin.fluent.generator.utils.NodeUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: This class is not up-to-date with the coding standards I followed in
//       `InterfaceBaseGenerator` and `InterfaceConcreteGenerator`.

public abstract class Generator implements GenericVisitor<Boolean, OutputBuilder> {

  // Static fields
  //--------------------------------------------------

  protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  protected static final String DEFAULT_OUTPUT_BASE_PACKAGE_NAME = "com.oliveryasuna.vaadin.fluent";

  protected static final String DEFAULT_WRAPPED_TYPE_PARAMETER_NAME = "__T";

  protected static final String DEFAULT_SUBCLASS_TYPE_PARAMETER_NAME = "__F";

  // Constructors
  //--------------------------------------------------

  protected Generator(final String name, final Set<Class<?>> generatedClasses) {
    super();

    this.name = name;
    this.generatedClasses = Collections.unmodifiableSet(generatedClasses);
  }

  // Fields
  //--------------------------------------------------

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final String name;

  private final Set<Class<?>> generatedClasses;

  // Methods
  //--------------------------------------------------

  // Main
  //

  public GeneratorResult generate(final CompilationUnit sourceFile, final Class<?> sourceClass) {
    final OutputBuilder outputBuilder = new OutputBuilder(sourceClass, sourceFile);

    sourceFile.accept(this, outputBuilder);

    if(outputBuilder.getGenerationErrors().isEmpty()) {
      return new GeneratorResult(outputBuilder.build(), outputBuilder.getGenerationWarnings());
    } else {
      return new GeneratorResult(outputBuilder.getGenerationWarnings(), outputBuilder.getGenerationErrors());
    }
  }

  // Helpers
  //

  /**
   * Generates the output package name from the source package name.
   *
   * @param sourcePackageName The source package name.
   *
   * @return The output package name.
   */
  protected String generatePackageName(final String sourcePackageName) {
    return (getOutputBasePackageName() + "." + sourcePackageName);
  }

  /**
   * Generates {@code @param} Javadoc for type parameters of the source class.
   *
   * @param sourceClass The source class.
   *
   * @return The {@code @param} Javadoc for type parameters of the source class.
   */
  protected String generateWrappedTypeParametersJavadoc(final ClassOrInterfaceDeclaration sourceClass) {
    final String sourceClassName = sourceClass.getNameAsString();

    return sourceClass.getTypeParameters()
        .stream()
        .map(TypeParameter::getNameAsString)
        .map(typeParameterName -> "@param <" + typeParameterName + "> See {@link " + sourceClassName + "}.")
        .collect(Collectors.joining("\n"));
  }

  protected abstract String generateClassSimpleName(final ClassOrInterfaceDeclaration sourceClass);

  /**
   * Generates type parameters for the generated class.
   *
   * @param sourceClass The source class.
   *
   * @return The type parameters for the generated class.
   */
  protected abstract NodeList<TypeParameter> generateTypeParameters(final ClassOrInterfaceDeclaration sourceClass);

  protected NodeList<TypeParameter> generateFluentTypeParameters(final ClassOrInterfaceDeclaration sourceClass) {
    return NodeList.nodeList(generateWrappedTypeParameter(sourceClass), generateSubclassTypeParameter(sourceClass));
  }

  /**
   * Generates a type parameter for the source class.
   *
   * @param sourceClass The source class.
   *
   * @return The type parameter for the source class.
   */
  protected TypeParameter generateWrappedTypeParameter(final ClassOrInterfaceDeclaration sourceClass) {
    return new TypeParameter(getWrappedTypeParameterName(), NodeList.nodeList(NodeUtils.typeWithTypeArguments(sourceClass)));
  }

  /**
   * Generates a type parameter for specifying the subclass of the generated class.
   *
   * @param sourceClass The source class.
   *
   * @return The type parameter for specifying the subclass of the generated class.
   */
  protected TypeParameter generateSubclassTypeParameter(final ClassOrInterfaceDeclaration sourceClass) {
    return new TypeParameter(getSubclassTypeParameterName(), NodeList.nodeList(generateSubclassTypeWithTypeArguments(sourceClass)));
  }

  /**
   * Generates type arguments for the generated class.
   *
   * @param sourceClass The source class.
   *
   * @return The type arguments for the generated class.
   */
  protected abstract NodeList<Type> generateTypeArguments(ClassOrInterfaceDeclaration sourceClass);

  protected NodeList<Type> generateFluentTypeArguments(final ClassOrInterfaceDeclaration sourceClass) {
    return NodeList.nodeList(generateWrappedTypeArgument(sourceClass), generateSubclassTypeArgument(sourceClass));
  }

  /**
   * Generates a type argument for the wrapped type parameter.
   *
   * @param sourceClass The source class.
   *
   * @return The type argument for the wrapped type parameter.
   */
  protected ClassOrInterfaceType generateWrappedTypeArgument(final ClassOrInterfaceDeclaration sourceClass) {
    return new ClassOrInterfaceType(null, getWrappedTypeParameterName());
  }

  /**
   * Generates a type argument for the subclass type parameter.
   *
   * @param sourceClass The source class.
   *
   * @return The type argument for the subclass type parameter.
   */
  protected ClassOrInterfaceType generateSubclassTypeArgument(final ClassOrInterfaceDeclaration sourceClass) {
    return new ClassOrInterfaceType(null, getSubclassTypeParameterName());
  }

  /**
   * Generates a type of the subclass type with its type arguments.
   *
   * @param sourceClass The source class.
   *
   * @return The type of the subclass type with its type arguments.
   */
  protected abstract ClassOrInterfaceType generateSubclassTypeWithTypeArguments(final ClassOrInterfaceDeclaration sourceClass);

  protected abstract String generateJavadoc(final ClassOrInterfaceDeclaration sourceClass);

  protected ClassOrInterfaceType generateFluentMethodReturnType(
      final MethodDeclaration sourceMethod,
      final ClassOrInterfaceDeclaration sourceClass,
      final OutputBuilder outputBuilder
  ) {
    final Type sourceReturnType = sourceMethod.getType();

    final ClassOrInterfaceType generatedReturnType;

    if(sourceReturnType instanceof VoidType) {
      generatedReturnType = new ClassOrInterfaceType(null, getSubclassTypeParameterName());
    } else if(sourceReturnType instanceof final PrimitiveType sourceReturnPrimitiveType) {
      final PrimitiveType.Primitive sourceReturnPrimitive = sourceReturnPrimitiveType.getType();

      final Class<?> generatedReturnClass;

      if(sourceReturnPrimitive == PrimitiveType.Primitive.BYTE) {
        generatedReturnClass = Config.getByteValueBreakClass();
      } else if(sourceReturnPrimitive == PrimitiveType.Primitive.SHORT) {
        generatedReturnClass = Config.getShortValueBreakClass();
      } else if(sourceReturnPrimitive == PrimitiveType.Primitive.INT) {
        generatedReturnClass = Config.getIntValueBreakClass();
      } else if(sourceReturnPrimitive == PrimitiveType.Primitive.LONG) {
        generatedReturnClass = Config.getLongValueBreakClass();
      } else if(sourceReturnPrimitive == PrimitiveType.Primitive.FLOAT) {
        generatedReturnClass = Config.getFloatValueBreakClass();
      } else if(sourceReturnPrimitive == PrimitiveType.Primitive.DOUBLE) {
        generatedReturnClass = Config.getDoubleValueBreakClass();
      } else if(sourceReturnPrimitive == PrimitiveType.Primitive.BOOLEAN) {
        generatedReturnClass = Config.getBooleanValueBreakClass();
      } else if(sourceReturnPrimitive == PrimitiveType.Primitive.CHAR) {
        generatedReturnClass = Config.getCharValueBreakClass();
      } else {
        throw new IllegalStateException("Unknown primitive type: " + sourceReturnPrimitive);
      }

      generatedReturnType = new ClassOrInterfaceType(
          null,
          new SimpleName(generatedReturnClass.getSimpleName()),
          generateFluentTypeArguments(sourceClass)
      );
    } else if(sourceReturnType instanceof final ClassOrInterfaceType sourceReturnClassType) {
      generatedReturnType = new ClassOrInterfaceType(
          null,
          new SimpleName(Config.getValueBreakClass().getSimpleName()),
          NodeUtils.of(
              generateFluentTypeArguments(sourceClass),
              resolveType(sourceReturnClassType, outputBuilder)
          )
      );
    } else if(sourceReturnType instanceof final ArrayType sourceReturnArrayType) {
      final Type sourceReturnArrayTypeElementType = sourceReturnArrayType.getElementType();

      if(sourceReturnArrayTypeElementType instanceof final PrimitiveType sourceReturnTypeArrayTypePrimitiveElementType) {
        final PrimitiveType.Primitive primitive = sourceReturnTypeArrayTypePrimitiveElementType.getType();

        final Class<?> generatedReturnClass;

        if(primitive == PrimitiveType.Primitive.BYTE) {
          generatedReturnClass = Config.getByteArrayValueBreakClass();
        } else if(primitive == PrimitiveType.Primitive.SHORT) {
          generatedReturnClass = Config.getShortArrayValueBreakClass();
        } else if(primitive == PrimitiveType.Primitive.INT) {
          generatedReturnClass = Config.getIntArrayValueBreakClass();
        } else if(primitive == PrimitiveType.Primitive.LONG) {
          generatedReturnClass = Config.getLongArrayValueBreakClass();
        } else if(primitive == PrimitiveType.Primitive.FLOAT) {
          generatedReturnClass = Config.getFloatArrayValueBreakClass();
        } else if(primitive == PrimitiveType.Primitive.DOUBLE) {
          generatedReturnClass = Config.getDoubleArrayValueBreakClass();
        } else if(primitive == PrimitiveType.Primitive.BOOLEAN) {
          generatedReturnClass = Config.getBooleanArrayValueBreakClass();
        } else if(primitive == PrimitiveType.Primitive.CHAR) {
          generatedReturnClass = Config.getCharArrayValueBreakClass();
        } else {
          throw new IllegalStateException("Unknown primitive type: " + primitive);
        }

        generatedReturnType = new ClassOrInterfaceType()
            .setName(generatedReturnClass.getSimpleName())
            .setTypeArguments(generateFluentTypeArguments(sourceClass));
      } else {
        generatedReturnType = new ClassOrInterfaceType()
            .setName(Config.getArrayValueBreakClass().getSimpleName())
            .setTypeArguments(NodeUtils.of(
                generateFluentTypeArguments(sourceClass),
                resolveType(sourceReturnType.getElementType(), outputBuilder)
            ));
      }
    } else {
      throw new IllegalStateException("Unknown return type: " + sourceReturnType.getClass().getName());
    }

    generatedReturnType.setAnnotations(sourceMethod.getType().getAnnotations());

    return generatedReturnType;
  }

  protected String generateFluentMethodName(final String sourceMethodName, final ClassOrInterfaceDeclaration sourceClass) {
    if(sourceMethodName.equals("apply") && sourceClass.getExtendedTypes().stream()
        .anyMatch(extendedType -> extendedType.getNameAsString().equals("SerializableFunction"))) {
      return "apply_";
    }

    if(sourceMethodName.equals("get")) {
      return "get_";
    }

    return sourceMethodName;
  }

  protected BlockStmt generateFluentMethodBody(
      final MethodDeclaration sourceMethod,
      final ClassOrInterfaceDeclaration sourceClass,
      final OutputBuilder outputBuilder
  ) {
    if(sourceMethod.getType() instanceof VoidType) {
      return new BlockStmt()
          .addStatement(String.format("get().%s(%s);", sourceMethod.getNameAsString(), generateFluentMethodArgumentList(sourceMethod)))
          // TODO: Add empty line.
          .addStatement("return uncheckedThis();");
    } else {
      return new BlockStmt()
          .addStatement(String.format(
              "return new %s<>(uncheckedThis(), get().%s(%s));",
              generateFluentMethodReturnType(sourceMethod, sourceClass, outputBuilder).getNameAsString(),
              sourceMethod.getNameAsString(),
              generateFluentMethodArgumentList(sourceMethod)
          ));
    }
  }

  protected String generateFluentMethodArgumentList(final MethodDeclaration sourceMethod) {
    return sourceMethod.getParameters().stream()
        .map(NodeWithSimpleName::getNameAsString)
        .collect(Collectors.joining(", "));
  }

  /**
   * Generates the simple name of a generated interface.
   *
   * @param simpleName The simple name of the source class.
   *
   * @return The name of a generated interface.
   */
  protected String generateInterfaceSimpleName(final String simpleName) {
    return ("I" + simpleName + "Factory");
  }

  /**
   * Generates the simple name of a generated base class.
   *
   * @param simpleName The simple name of the source class.
   *
   * @return The name of a generated base class.
   */
  protected String generateBaseClassSimpleName(final String simpleName) {
    return ("Abstract" + simpleName + "Factory");
  }

  /**
   * Generates the simple name of a generated concrete class.
   *
   * @param simpleName The simple name of the source class.
   *
   * @return The name of a generated concrete class.
   */
  protected String generateConcreteClassSimpleName(final String simpleName) {
    return (simpleName + "Factory");
  }

  protected String getOutputBasePackageName() {
    return DEFAULT_OUTPUT_BASE_PACKAGE_NAME;
  }

  protected String getWrappedTypeParameterName() {
    return DEFAULT_WRAPPED_TYPE_PARAMETER_NAME;
  }

  protected String getSubclassTypeParameterName() {
    return DEFAULT_SUBCLASS_TYPE_PARAMETER_NAME;
  }

  protected String getGeneratedClassPackageName(final String classSimpleName) {
    return getGeneratedClasses().stream()
        .filter(generatedClass -> generatedClass.getSimpleName().equals(classSimpleName))
        .findFirst()
        .map(Class::getPackageName)
        .map(this::generatePackageName)
        .orElseThrow();
  }

  protected boolean hasGeneratedClass(final String classSimpleName) {
    return getGeneratedClasses().stream()
        .map(Class::getSimpleName)
        .anyMatch(generatedClassName -> generatedClassName.equals(classSimpleName));
  }

  protected Type resolveType(final Type type, final OutputBuilder outputBuilder) {
    if(!(type instanceof final ClassOrInterfaceType objectType)) {
      return NodeUtils.copy(type);
    }

    final String typeName = objectType.getNameAsString();
    final Class<?> sourceClass = outputBuilder.getSourceClass();

    // Handle special cases.

    final String sourceClassSimpleName = sourceClass.getSimpleName();

    if(typeName.equals("Alignment") && (sourceClassSimpleName.equals("VerticalLayout") || sourceClassSimpleName.equals("HorizontalLayout"))) {
      return new ClassOrInterfaceType()
          .setName("FlexComponent.Alignment");
    }

    // Try to find inner class.

    try {
      Class.forName(sourceClass.getName() + "$" + typeName);

      return new ClassOrInterfaceType()
          .setName(sourceClassSimpleName + "." + typeName);
    } catch(final ClassNotFoundException ignored) {
      return NodeUtils.copy(type);
    }
  }

  // Visitors
  //

  @Override
  public Boolean visit(final CompilationUnit sourceFile, final OutputBuilder outputBuilder) {
    // Only support one type declaration.

    final NodeList<TypeDeclaration<?>> sourceFileTypes = sourceFile.getTypes();

    if(sourceFileTypes.size() != 1) {
      outputBuilder.addGenerationError("Expected exactly one type declaration.");

      return false;
    }

    // Only support class type declarations.

    final TypeDeclaration<?> sourceFileType = sourceFileTypes.get(0);

    if(!(sourceFileType instanceof ClassOrInterfaceDeclaration)) {
      outputBuilder.addGenerationError("Expected a class or interface declaration.");

      return false;
    }

    // Add wildcard imports for fluent factory and value break packages.

    outputBuilder.addImport(new ImportDeclaration(Config.getFluentFactoryClass().getPackageName(), false, true));
    outputBuilder.addImport(new ImportDeclaration(Config.getValueBreakClass().getPackageName(), false, true));

    // Visit necessary.

    sourceFile.getPackageDeclaration()
        .ifPresent(packageDeclaration -> packageDeclaration.accept(this, outputBuilder));
    sourceFile.getImports()
        .forEach(importDeclaration -> importDeclaration.accept(this, outputBuilder));
    sourceFile.getType(0).accept(this, outputBuilder);

    return true;
  }

  @Override
  public Boolean visit(final PackageDeclaration sourcePackage, final OutputBuilder outputBuilder) {
    // Set package name.

    final String sourcePackageName = sourcePackage.getNameAsString();

    outputBuilder.setPackageName(generatePackageName(sourcePackageName));

    // Add some useful imports.

    // Add import for the source package with an asterisk.
    // This simplifies handling imports.
    outputBuilder.addImport(new ImportDeclaration(sourcePackageName, false, true));

    // Add asterisk import for the source class.
    // This handles inner classes.
    outputBuilder.addImport(new ImportDeclaration(outputBuilder.getSourceClass().getName(), false, true));

    return true;
  }

  @Override
  public Boolean visit(final ImportDeclaration sourceImport, final OutputBuilder outputBuilder) {
    // Get the name of the import.

    final String sourceImportName = sourceImport.getNameAsString();

    // If the import is static, just import it.

    if(sourceImport.isStatic()) {
      outputBuilder.addImport(new ImportDeclaration(sourceImportName, true, false));

      return true;
    }

    // Make sure that the import is accessible.
    // The import could be nested, so we replace the dots with dollar signs.

    String sourceImportNameTest = sourceImportName;

    while(sourceImportNameTest.contains(".")) {
      try {
        final Class<?> clazz = Class.forName(sourceImportNameTest);

        if(!java.lang.reflect.Modifier.isPublic(clazz.getModifiers())) {
          outputBuilder.addGenerationWarning("Import is not accessible: " + sourceImportName);

          return false;
        }
      } catch(final ClassNotFoundException ignored) {
      }

      // Replace last "." with "$".
      sourceImportNameTest = sourceImportNameTest.substring(0, sourceImportNameTest.lastIndexOf('.')) + "$" + sourceImportNameTest.substring(sourceImportNameTest.lastIndexOf('.') + 1);
    }

    // Add the import.

    outputBuilder.addImport(sourceImport);

    // Also add asterisk import for the source import if not already.

    if(!sourceImport.isAsterisk()) {
      outputBuilder.addImport(new ImportDeclaration(sourceImportName, false, true));
    }

    return true;
  }

  @Override
  public Boolean visit(final ClassOrInterfaceDeclaration sourceClass, final OutputBuilder outputBuilder) {
    // Add class Javadoc.
    outputBuilder.setClassJavadoc(generateJavadoc(sourceClass));

    // Add `public` modifier.
    outputBuilder.addClassModifier(Modifier.publicModifier());

    // Set class name.
    outputBuilder.setClassName(generateClassSimpleName(sourceClass));

    // Add type parameters.
    outputBuilder.getTypeParameters().addAll(generateTypeParameters(sourceClass));

    return true;
  }

  @Override
  public Boolean visit(final ConstructorDeclaration sourceConstructor, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ConstructorDeclaration");
  }

  @Override
  public Boolean visit(final MethodDeclaration sourceMethod, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: MethodDeclaration");
  }

  @Override
  public Boolean visit(final NodeList nodeList, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: NodeList");
  }

  @Override
  public Boolean visit(final AnnotationDeclaration annotationDeclaration, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: AnnotationDeclaration");
  }

  @Override
  public Boolean visit(final AnnotationMemberDeclaration annotationMemberDeclaration, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: AnnotationMemberDeclaration");
  }

  @Override
  public Boolean visit(final ArrayAccessExpr arrayAccessExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ArrayAccessExpr");
  }

  @Override
  public Boolean visit(final ArrayCreationExpr arrayCreationExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ArrayCreationExpr");
  }

  @Override
  public Boolean visit(final ArrayCreationLevel arrayCreationLevel, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ArrayCreationLevel");
  }

  @Override
  public Boolean visit(final ArrayInitializerExpr arrayInitializerExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ArrayInitializerExpr");
  }

  @Override
  public Boolean visit(final ArrayType arrayType, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ArrayType");
  }

  @Override
  public Boolean visit(final AssertStmt assertStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: AssertStmt");
  }

  @Override
  public Boolean visit(final AssignExpr assignExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: AssignExpr");
  }

  @Override
  public Boolean visit(final BinaryExpr binaryExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: BinaryExpr");
  }

  @Override
  public Boolean visit(final BlockComment blockComment, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: BlockComment");
  }

  @Override
  public Boolean visit(final BlockStmt blockStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: BlockStmt");
  }

  @Override
  public Boolean visit(final BooleanLiteralExpr booleanLiteralExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: BooleanLiteralExpr");
  }

  @Override
  public Boolean visit(final BreakStmt breakStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: BreakStmt");
  }

  @Override
  public Boolean visit(final CastExpr castExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: CastExpr");
  }

  @Override
  public Boolean visit(final CatchClause catchClause, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: CatchClause");
  }

  @Override
  public Boolean visit(final CharLiteralExpr charLiteralExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: CharLiteralExpr");
  }

  @Override
  public Boolean visit(final ClassExpr classExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ClassExpr");
  }

  @Override
  public Boolean visit(final ClassOrInterfaceType classOrInterfaceType, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ClassOrInterfaceType");
  }

  @Override
  public Boolean visit(final ConditionalExpr conditionalExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ConditionalExpr");
  }

  @Override
  public Boolean visit(final ContinueStmt continueStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ContinueStmt");
  }

  @Override
  public Boolean visit(final DoStmt doStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: DoStmt");
  }

  @Override
  public Boolean visit(final DoubleLiteralExpr doubleLiteralExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: DoubleLiteralExpr");
  }

  @Override
  public Boolean visit(final EmptyStmt emptyStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: EmptyStmt");
  }

  @Override
  public Boolean visit(final EnclosedExpr enclosedExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: EnclosedExpr");
  }

  @Override
  public Boolean visit(final EnumConstantDeclaration enumConstantDeclaration, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: EnumConstantDeclaration");
  }

  @Override
  public Boolean visit(final EnumDeclaration enumDeclaration, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: EnumDeclaration");
  }

  @Override
  public Boolean visit(final ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ExplicitConstructorInvocationStmt");
  }

  @Override
  public Boolean visit(final ExpressionStmt expressionStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ExpressionStmt");
  }

  @Override
  public Boolean visit(final FieldAccessExpr fieldAccessExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: FieldAccessExpr");
  }

  @Override
  public Boolean visit(final FieldDeclaration fieldDeclaration, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: FieldDeclaration");
  }

  @Override
  public Boolean visit(final ForStmt forStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ForStmt");
  }

  @Override
  public Boolean visit(final ForEachStmt forEachStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ForEachStmt");
  }

  @Override
  public Boolean visit(final IfStmt ifStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: IfStmt");
  }

  @Override
  public Boolean visit(final InitializerDeclaration initializerDeclaration, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: InitializerDeclaration");
  }

  @Override
  public Boolean visit(final InstanceOfExpr instanceOfExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: InstanceOfExpr");
  }

  @Override
  public Boolean visit(final IntegerLiteralExpr integerLiteralExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: IntegerLiteralExpr");
  }

  @Override
  public Boolean visit(final IntersectionType intersectionType, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: IntersectionType");
  }

  @Override
  public Boolean visit(final JavadocComment javadocComment, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: JavadocComment");
  }

  @Override
  public Boolean visit(final LabeledStmt labeledStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: LabeledStmt");
  }

  @Override
  public Boolean visit(final LambdaExpr lambdaExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: LambdaExpr");
  }

  @Override
  public Boolean visit(final LineComment lineComment, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: LineComment");
  }

  @Override
  public Boolean visit(final LocalClassDeclarationStmt localClassDeclarationStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: LocalClassDeclarationStmt");
  }

  @Override
  public Boolean visit(final LocalRecordDeclarationStmt localRecordDeclarationStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: LocalRecordDeclarationStmt");
  }

  @Override
  public Boolean visit(final LongLiteralExpr longLiteralExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: LongLiteralExpr");
  }

  @Override
  public Boolean visit(final MarkerAnnotationExpr markerAnnotationExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: MarkerAnnotationExpr");
  }

  @Override
  public Boolean visit(final MemberValuePair memberValuePair, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: MemberValuePair");
  }

  @Override
  public Boolean visit(final MethodCallExpr methodCallExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: MethodCallExpr");
  }

  @Override
  public Boolean visit(final MethodReferenceExpr methodReferenceExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: MethodReferenceExpr");
  }

  @Override
  public Boolean visit(final NameExpr nameExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: NameExpr");
  }

  @Override
  public Boolean visit(final Name name, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: Name");
  }

  @Override
  public Boolean visit(final NormalAnnotationExpr normalAnnotationExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: NormalAnnotationExpr");
  }

  @Override
  public Boolean visit(final NullLiteralExpr nullLiteralExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: NullLiteralExpr");
  }

  @Override
  public Boolean visit(final ObjectCreationExpr objectCreationExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ObjectCreationExpr");
  }

  @Override
  public Boolean visit(final Parameter parameter, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: Parameter");
  }

  @Override
  public Boolean visit(final PrimitiveType primitiveType, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: PrimitiveType");
  }

  @Override
  public Boolean visit(final RecordDeclaration recordDeclaration, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: RecordDeclaration");
  }

  @Override
  public Boolean visit(final CompactConstructorDeclaration compactConstructorDeclaration, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: CompactConstructorDeclaration");
  }

  @Override
  public Boolean visit(final ReturnStmt returnStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ReturnStmt");
  }

  @Override
  public Boolean visit(final SimpleName simpleName, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: SimpleName");
  }

  @Override
  public Boolean visit(final SingleMemberAnnotationExpr singleMemberAnnotationExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: SingleMemberAnnotationExpr");
  }

  @Override
  public Boolean visit(final StringLiteralExpr stringLiteralExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: StringLiteralExpr");
  }

  @Override
  public Boolean visit(final SuperExpr superExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: SuperExpr");
  }

  @Override
  public Boolean visit(final SwitchEntry switchEntry, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: SwitchEntry");
  }

  @Override
  public Boolean visit(final SwitchStmt switchStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: SwitchStmt");
  }

  @Override
  public Boolean visit(final SynchronizedStmt synchronizedStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: SynchronizedStmt");
  }

  @Override
  public Boolean visit(final ThisExpr thisExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ThisExpr");
  }

  @Override
  public Boolean visit(final ThrowStmt throwStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ThrowStmt");
  }

  @Override
  public Boolean visit(final TryStmt tryStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: TryStmt");
  }

  @Override
  public Boolean visit(final TypeExpr typeExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: TypeExpr");
  }

  @Override
  public Boolean visit(final TypeParameter typeParameter, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: TypeParameter");
  }

  @Override
  public Boolean visit(final UnaryExpr unaryExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: UnaryExpr");
  }

  @Override
  public Boolean visit(final UnionType unionType, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: UnionType");
  }

  @Override
  public Boolean visit(final UnknownType unknownType, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: UnknownType");
  }

  @Override
  public Boolean visit(final VariableDeclarationExpr variableDeclarationExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: VariableDeclarationExpr");
  }

  @Override
  public Boolean visit(final VariableDeclarator variableDeclarator, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: VariableDeclarator");
  }

  @Override
  public Boolean visit(final VoidType voidType, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: VoidType");
  }

  @Override
  public Boolean visit(final WhileStmt whileStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: WhileStmt");
  }

  @Override
  public Boolean visit(final WildcardType wildcardType, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: WildcardType");
  }

  @Override
  public Boolean visit(final ModuleDeclaration moduleDeclaration, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ModuleDeclaration");
  }

  @Override
  public Boolean visit(final ModuleRequiresDirective moduleRequiresDirective, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ModuleRequiresDirective");
  }

  @Override
  public Boolean visit(final ModuleExportsDirective moduleExportsDirective, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ModuleExportsDirective");
  }

  @Override
  public Boolean visit(final ModuleProvidesDirective moduleProvidesDirective, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ModuleProvidesDirective");
  }

  @Override
  public Boolean visit(final ModuleUsesDirective moduleUsesDirective, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ModuleUsesDirective");
  }

  @Override
  public Boolean visit(final ModuleOpensDirective moduleOpensDirective, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ModuleOpensDirective");
  }

  @Override
  public Boolean visit(final UnparsableStmt unparsableStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: UnparsableStmt");
  }

  @Override
  public Boolean visit(final ReceiverParameter receiverParameter, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: ReceiverParameter");
  }

  @Override
  public Boolean visit(final VarType varType, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: VarType");
  }

  @Override
  public Boolean visit(final Modifier modifier, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: Modifier");
  }

  @Override
  public Boolean visit(final SwitchExpr switchExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: SwitchExpr");
  }

  @Override
  public Boolean visit(final TextBlockLiteralExpr textBlockLiteralExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: TextBlockLiteralExpr");
  }

  @Override
  public Boolean visit(final YieldStmt yieldStmt, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: YieldStmt");
  }

  @Override
  public Boolean visit(final PatternExpr patternExpr, final OutputBuilder outputBuilder) {
    throw new NotImplementedException("NOT IMPLEMENTED: PatternExpr");
  }

  // Getters/setters
  //--------------------------------------------------

  public Logger getLogger() {
    return logger;
  }

  public String getName() {
    return name;
  }

  public Set<Class<?>> getGeneratedClasses() {
    return generatedClasses;
  }

}
