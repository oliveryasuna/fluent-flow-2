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

import java.util.Collections;
import java.util.List;

public final class GeneratorResult {

  // Constructors
  //--------------------------------------------------

  private GeneratorResult(final CompilationUnit compilationUnit, final List<String> warnings, final List<String> errors) {
    super();

    this.compilationUnit = compilationUnit;
    this.warnings = Collections.unmodifiableList(warnings);
    this.errors = Collections.unmodifiableList(errors);
  }

  GeneratorResult(final CompilationUnit compilationUnit, final List<String> warnings) {
    this(compilationUnit, warnings, Collections.emptyList());
  }

  GeneratorResult(final List<String> warnings, final List<String> errors) {
    this(null, warnings, errors);
  }

  // Fields
  //--------------------------------------------------

  private final CompilationUnit compilationUnit;

  private final List<String> warnings;

  private final List<String> errors;

  // Methods
  //--------------------------------------------------

  public boolean hasCompilationUnit() {
    return (compilationUnit != null);
  }

  public boolean hasWarnings() {
    return !warnings.isEmpty();
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  // Getters/setters
  //--------------------------------------------------

  public CompilationUnit getCompilationUnit() {
    return compilationUnit;
  }

  public List<String> getWarnings() {
    return warnings;
  }

  public List<String> getErrors() {
    return errors;
  }

}
