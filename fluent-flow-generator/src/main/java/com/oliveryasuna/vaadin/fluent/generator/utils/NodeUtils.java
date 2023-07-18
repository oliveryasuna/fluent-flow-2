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
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.oliveryasuna.commons.language.exception.UnsupportedInstantiationException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class NodeUtils {

  // Static methods
  //--------------------------------------------------

  public static <NODE extends Node> NodeList<NODE> of(final Object... nodeOrNodeLists) {
    if(nodeOrNodeLists == null) {
      return null;
    }

    if(nodeOrNodeLists.length == 0) {
      return new NodeList<>();
    }

    return (NodeList<NODE>)Arrays.stream(nodeOrNodeLists)
        .filter(Objects::nonNull)
        .map(NodeUtils::listOf)
        .flatMap(NodeList::stream)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(NodeList::new));
  }

  public static <NODE extends Node> NodeList<NODE> listOf(final Object nodeOrNodeList) {
    if(nodeOrNodeList == null) {
      return null;
    }

    if(nodeOrNodeList instanceof final NodeList<?> nodeList) {
      return (NodeList<NODE>)nodeList;
    } else if(nodeOrNodeList instanceof final Node node) {
      return (NodeList<NODE>)NodeList.nodeList(node);
    }

    throw new IllegalArgumentException("Only `Node` and `NodeList` are supported.");
  }

  public static <NODE extends Node> NodeList<NODE> copyAll(final NodeList<NODE> nodes) {
    if(nodes == null) {
      return null;
    }

    return nodes.stream()
        .map(NodeUtils::copy)
        .collect(Collectors.toCollection(NodeList::new));
  }

  public static <NODE extends Node> NODE copy(final NODE node) {
    if(node == null) {
      return null;
    }

    return (NODE)node.clone();
  }

  public static <TYPE extends Type> NodeList<TYPE> typeArgumentsFromTypeParameters(final NodeList<TypeParameter> typeParameters) {
    return (NodeList<TYPE>)copyAll(typeParameters).stream()
        .map(NodeUtils::typeArgumentFromTypeParameter)
        .collect(Collectors.toCollection(NodeList::new));
  }

  public static Type typeArgumentFromTypeParameter(final TypeParameter typeParameter) {
    return new ClassOrInterfaceType(null, typeParameter.getNameAsString());
  }

  public static ClassOrInterfaceType typeWithTypeArguments(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
    final NodeList<TypeParameter> typeParameters = classOrInterfaceDeclaration.getTypeParameters();

    final ClassOrInterfaceType type = new ClassOrInterfaceType()
        .setName(classOrInterfaceDeclaration.getNameAsString());

    if(typeParameters.isNonEmpty()) {
      type.setTypeArguments(typeArgumentsFromTypeParameters(typeParameters));
    }

    return type;
  }

  public static Optional<ClassOrInterfaceDeclaration> getParentClass(final Node node) {
    if(node == null) {
      return Optional.empty();
    }

    if(node instanceof final ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
      return Optional.of(classOrInterfaceDeclaration);
    }

    return getParentClass(node.getParentNode().orElse(null));
  }

  // Constructors
  //--------------------------------------------------

  private NodeUtils() {
    super();

    throw new UnsupportedInstantiationException();
  }

}
