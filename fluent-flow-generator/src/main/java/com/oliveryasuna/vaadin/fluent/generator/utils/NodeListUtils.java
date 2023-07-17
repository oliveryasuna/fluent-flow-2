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

package com.oliveryasuna.vaadin.fluent.generator.utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.oliveryasuna.commons.language.exception.UnsupportedInstantiationException;

@Deprecated
public final class NodeListUtils {

  // Static methods
  //--------------------------------------------------

  // TODO: Move to `NodeUtils`.
  @SafeVarargs
  public static <N extends Node> NodeList<N> concat(final NodeList<? extends N> nodeList, final NodeList<? extends N>... nodeLists) {
    final NodeList<N> result = new NodeList<>();

    result.addAll(nodeList);

    if(nodeLists != null) {
      for(final NodeList<? extends N> nodeList2 : nodeLists) {
        result.addAll(nodeList2);
      }
    }

    return result;
  }

  // TODO: Move to `NodeUtils`.
  public static <N extends Node> NodeList<N> concat(final NodeList<N> nodeList, final N... nodes) {
    final NodeList<N> result = new NodeList<>();

    result.addAll(nodeList);

    if(nodes != null) {
      for(final N node : nodes) {
        result.add(node);
      }
    }

    return result;
  }

  // Constructors
  //--------------------------------------------------

  private NodeListUtils() {
    super();

    throw new UnsupportedInstantiationException();
  }

}
