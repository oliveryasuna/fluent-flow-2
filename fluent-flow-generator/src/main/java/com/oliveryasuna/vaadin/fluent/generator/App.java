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

package com.oliveryasuna.vaadin.fluent.generator;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.oliveryasuna.vaadin.fluent.generator.classfinder.ClassFinder;
import com.oliveryasuna.vaadin.fluent.generator.generator.Generator;
import com.oliveryasuna.vaadin.fluent.generator.generator.GeneratorResult;
import com.oliveryasuna.vaadin.fluent.generator.generator.impl.InterfaceBaseGenerator;
import com.oliveryasuna.vaadin.fluent.generator.generator.impl.InterfaceConcreteGenerator;
import com.oliveryasuna.vaadin.fluent.generator.generator.impl.InterfaceInterfaceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public final class App {

  // Entry point
  //--------------------------------------------------

  public static void main(final String[] args) throws Exception {
    final ClassFinder classFinder = new ClassFinder();
    final Set<Class<?>> classes = classFinder.find(
        "com.vaadin.flow",
        true,
        clazz -> !clazz.isAnnotation() && !clazz.isMemberClass() && Modifier.isPublic(clazz.getModifiers())
    );

    final Set<Class<?>> interfaces = classes.stream()
        .filter(Class::isInterface)
        .collect(Collectors.toUnmodifiableSet());

    generateClasses(interfaces, new InterfaceInterfaceGenerator(classes));
    generateClasses(interfaces, new InterfaceBaseGenerator(classes));
    generateClasses(interfaces, new InterfaceConcreteGenerator(classes));
  }

  // Static fields
  //--------------------------------------------------

  private static final File OUTPUT_DIRECTORY = new File("../fluent-flow-addon/src/main/java");

  // Static methods
  //--------------------------------------------------

  private static void generateClasses(final Set<Class<?>> sourceClasses, final Generator generator) {
    for(final Class<?> sourceClass : sourceClasses) {
      generateClass(sourceClass, generator);
    }
  }

  private static boolean generateClass(final Class<?> sourceClass, final Generator generator) {
    final String generatorName = generator.getName();

    LOGGER.info("[{}] Generating class for \"{}\".", generatorName, sourceClass.getName());

    final InputStream sourceFileInput;

    try {
      sourceFileInput = resolveClassSource(sourceClass);
    } catch(final IOException e) {
      LOGGER.error("[{}] Failed to resolve source for \"{}\".", generatorName, sourceClass.getName(), e);

      return false;
    }

    final CompilationUnit sourceFile;

    try {
      sourceFile = StaticJavaParser.parse(sourceFileInput);
    } catch(final ParseProblemException e) {
      LOGGER.error("[{}] Failed to parse source for \"{}\".", generatorName, sourceClass.getName(), e);

      return false;
    }

    final GeneratorResult result = generator.generate(sourceFile, sourceClass);

    if(result.hasErrors()) {
      LOGGER.error(
          "[{}] Failed to generate class for \"{}\":\n{}",
          generatorName,
          sourceClass.getName(),
          result.getErrors()
              .stream()
              .map(value -> "* " + value)
              .collect(Collectors.joining("\n"))
      );

      return false;
    }

    try {
      write(result);
    } catch(final IOException e) {
      LOGGER.error("[{}] Failed to write generated class for \"{}\".", generatorName, sourceClass.getName(), e);

      return false;
    }

    if(result.hasWarnings()) {
      LOGGER.warn(
          "[{}] Generated class for \"{}\" with warnings:\n{}",
          generatorName,
          sourceClass.getName(), result.getWarnings()
              .stream()
              .map(value -> "* " + value)
              .collect(Collectors.joining("\n"))
      );
    } else {
      LOGGER.info("[{}] Generated class for \"{}\".", generatorName, sourceClass.getName());
    }

    return true;
  }

  private static InputStream resolveClassSource(final Class<?> clazz) throws IOException {
    final String compiledJarPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    final String sourceJarPath = compiledJarPath.substring(0, compiledJarPath.lastIndexOf(".jar")) + "-sources.jar";
    final String sourcePath = clazz.getName().replaceAll("\\.", "/") + ".java";

    final ZipFile zipFile = new ZipFile(sourceJarPath);
    final ZipEntry zipEntry = zipFile.getEntry(sourcePath);

    return zipFile.getInputStream(zipEntry);
  }

  private static void write(final GeneratorResult result) throws IOException {
    final CompilationUnit compilationUnit = result.getCompilationUnit();

    final File folder = new File(
        OUTPUT_DIRECTORY,
        compilationUnit.getPackageDeclaration()
            .map(packageDeclaration -> packageDeclaration.getNameAsString())
            .orElseThrow()
            .replaceAll("\\.", "/")
    );
    final File file = new File(folder, compilationUnit.getType(0).getNameAsString() + ".java");

    folder.mkdirs();
    FileUtils.writeStringToFile(file, compilationUnit.toString(), StandardCharsets.UTF_8);
  }

}
