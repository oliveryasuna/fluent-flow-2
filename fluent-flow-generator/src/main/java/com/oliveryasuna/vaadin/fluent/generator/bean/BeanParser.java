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

package com.oliveryasuna.vaadin.fluent.generator.bean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.oliveryasuna.vaadin.fluent.generator.bean.model.ClassFileDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class BeanParser {

  // Constructors
  //--------------------------------------------------

  public BeanParser() {
    super();
  }

  // Methods
  //--------------------------------------------------

  public ClassFileDescriptor parse(final Class<?> clazz) throws Exception {
    final String classJarPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();

    final String sourceJarPath = classJarPath.substring(0, classJarPath.lastIndexOf(".jar")) + "-sources.jar";
    final String sourcePath = clazz.getName().replaceAll("\\.", "/") + ".java";

    final ZipFile zipFile = new ZipFile(sourceJarPath);
    final ZipEntry zipEntry = zipFile.getEntry(sourcePath);

    final CompilationUnit compilationUnit;

    try(final InputStream sourceInput = zipFile.getInputStream(zipEntry)) {
      final ParseResult<CompilationUnit> result = new JavaParser().parse(sourceInput);
      final Optional<CompilationUnit> optionalCompilationUnit = result.getResult();

      if(!result.isSuccessful() || optionalCompilationUnit.isEmpty()) {
        throw new IllegalStateException("Failed to parse class file: " + clazz.getName());
      }

      compilationUnit = optionalCompilationUnit.get();
    }

    return new ClassFileDescriptor(compilationUnit);
  }

}
