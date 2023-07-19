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

package com.oliveryasuna.vaadin.fluent;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Discovers classes on the classpath.
 *
 * @author Oliver Yasuna
 * @since 2.0.0
 */
public class ClassFinder {

  // Static fields
  //--------------------------------------------------

  protected static final JavaFileManager.Location LOCATION = StandardLocation.CLASS_PATH;

  protected static final Set<JavaFileObject.Kind> KINDS = Set.of(JavaFileObject.Kind.CLASS);

  // Constructors
  //--------------------------------------------------

  public ClassFinder() {
    super();
  }

  // Fields
  //--------------------------------------------------

  private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

  private final JavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

  // Methods
  //--------------------------------------------------

  public Set<Class<?>> find(final String basePackage, final boolean recursive, final Predicate<Class<?>> filter) throws IOException {
    final Iterable<JavaFileObject> javaFiles = getFileManager().list(LOCATION, basePackage, KINDS, recursive);

    final String basePackagePath = basePackage.replace('.', '/');
    final String basePackagePathWithSlash = basePackagePath + "/";

    return StreamSupport.stream(javaFiles.spliterator(), true)
        // Map to `Class`.
        .map(javaFile -> {
          final String uri = javaFile.toUri().toString();

          return uri
              .substring(uri.indexOf(basePackagePathWithSlash), uri.length() - 6)
              .replaceAll(File.separator, ".");
        })
        .map(className -> {
          try {
            return Class.forName(className);
          } catch(final ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        })
        .filter(filter)
        .collect(Collectors.toSet());
  }

  // Getters/setters
  //--------------------------------------------------

  public JavaCompiler getCompiler() {
    return compiler;
  }

  public JavaFileManager getFileManager() {
    return fileManager;
  }

}
