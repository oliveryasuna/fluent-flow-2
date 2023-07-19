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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

final class EnsureAllMethods {

  // Constructors
  //--------------------------------------------------

  private EnsureAllMethods() {
    super();
  }

  // Tests
  //--------------------------------------------------

  @Test
  void ensureAllMethods() throws IOException {
    final Set<Class<?>> generatedClasses = new ClassFinder()
        .find("com.oliveryasuna.vaadin.fluent", true, Class::isInterface);

    for(final Class<?> generatedClass : generatedClasses) {
      final Type sourceType = generatedClass.getTypeParameters()[0].getBounds()[0];
      final Class<?> sourceClass;

      if(sourceType instanceof final Class<?> clazz) {
        sourceClass = clazz;
      } else if(sourceType instanceof final ParameterizedType parameterizedType) {
        sourceClass = (Class<?>)parameterizedType.getRawType();
      } else {
        throw new RuntimeException("Unknown type: " + sourceType);
      }

      final List<Method> generatedMethods = List.of(generatedClass.getMethods());
      final List<String> generatedMethodNames = generatedMethods.stream()
          .map(Method::getName)
          .toList();

      final List<Method> sourceMethods = Arrays.stream(sourceClass.getMethods())
          .filter(method -> !method.getDeclaringClass().equals(Object.class))
          .filter(method -> !Modifier.isStatic(method.getModifiers()))
          .toList();

      for(final Method sourceMethod : sourceMethods) {
        final String sourceMethodName = sourceMethod.getName();

        Assertions.assertTrue(generatedMethodNames.contains(sourceMethodName), "Generated class \"" + generatedClass + "\" does not contain method \"" + sourceMethod + "\" from source class \"" + sourceClass + "\".");
      }
    }
  }

}
