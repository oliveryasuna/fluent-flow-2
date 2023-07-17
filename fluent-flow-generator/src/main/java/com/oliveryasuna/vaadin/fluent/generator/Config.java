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

import com.oliveryasuna.commons.language.pattern.fluent.FluentFactory;
import com.oliveryasuna.commons.language.pattern.fluent.IFluentFactory;
import com.oliveryasuna.commons.language.pattern.fluent.breakdown.*;

import java.io.IOException;
import java.util.Properties;

/**
 * Generator configuration.
 *
 * @author Oliver Yasuna
 * @since 2.0.0
 */
public final class Config {

  // Static fields
  //--------------------------------------------------

  private static Config INSTANCE;

  // Static methods
  //--------------------------------------------------

  private static Config getInstance() {
    if(INSTANCE == null) {
      try {
        INSTANCE = new Config();
      } catch(final Exception e) {
        throw new RuntimeException(e);
      }
    }

    return INSTANCE;
  }

  public static String getVersion() {
    return getInstance().version;
  }

  public static Class<IFluentFactory<?, ?>> getIFluentFactoryClass() {
    return getInstance().iFluentFactoryClass;
  }

  public static Class<FluentFactory<?, ?>> getFluentFactoryClass() {
    return getInstance().fluentFactoryClass;
  }

  public static Class<BooleanValueBreak<?, ?>> getBooleanValueBreakClass() {
    return getInstance().booleanValueBreakClass;
  }

  public static Class<CharValueBreak<?, ?>> getCharValueBreakClass() {
    return getInstance().charValueBreakClass;
  }

  public static Class<ByteValueBreak<?, ?>> getByteValueBreakClass() {
    return getInstance().byteValueBreakClass;
  }

  public static Class<ShortValueBreak<?, ?>> getShortValueBreakClass() {
    return getInstance().shortValueBreakClass;
  }

  public static Class<IntValueBreak<?, ?>> getIntValueBreakClass() {
    return getInstance().intValueBreakClass;
  }

  public static Class<LongValueBreak<?, ?>> getLongValueBreakClass() {
    return getInstance().longValueBreakClass;
  }

  public static Class<FloatValueBreak<?, ?>> getFloatValueBreakClass() {
    return getInstance().floatValueBreakClass;
  }

  public static Class<DoubleValueBreak<?, ?>> getDoubleValueBreakClass() {
    return getInstance().doubleValueBreakClass;
  }

  public static Class<ValueBreak<?, ?, ?>> getValueBreakClass() {
    return getInstance().valueBreakClass;
  }

  public static Class<ArrayValueBreak<?, ?, ?>> getArrayValueBreakClass() {
    return getInstance().arrayValueBreakClass;
  }

  public static String getVaadinVersion() {
    return getInstance().vaadinVersion;
  }

  // Constructors
  //--------------------------------------------------

  private Config() throws Exception {
    super();

    final Properties properties = new Properties();

    try {
      properties.load(getClass().getResourceAsStream("/config.properties"));
    } catch(final IOException e) {
      throw new IllegalStateException("Failed to load configuration.", e);
    }

    this.version = properties.getProperty("version");

    this.iFluentFactoryClass = (Class<IFluentFactory<?, ?>>)Class.forName(properties.getProperty("name.IFluentFactory"));
    this.fluentFactoryClass = (Class<FluentFactory<?, ?>>)Class.forName(properties.getProperty("name.FluentFactory"));

    this.valueBreakClass = (Class<ValueBreak<?, ?, ?>>)Class.forName(properties.getProperty("name.ValueBreak"));
    this.byteValueBreakClass = (Class<ByteValueBreak<?, ?>>)Class.forName(properties.getProperty("name.ByteValueBreak"));
    this.shortValueBreakClass = (Class<ShortValueBreak<?, ?>>)Class.forName(properties.getProperty("name.ShortValueBreak"));
    this.intValueBreakClass = (Class<IntValueBreak<?, ?>>)Class.forName(properties.getProperty("name.IntValueBreak"));
    this.longValueBreakClass = (Class<LongValueBreak<?, ?>>)Class.forName(properties.getProperty("name.LongValueBreak"));
    this.floatValueBreakClass = (Class<FloatValueBreak<?, ?>>)Class.forName(properties.getProperty("name.FloatValueBreak"));
    this.doubleValueBreakClass = (Class<DoubleValueBreak<?, ?>>)Class.forName(properties.getProperty("name.DoubleValueBreak"));
    this.booleanValueBreakClass = (Class<BooleanValueBreak<?, ?>>)Class.forName(properties.getProperty("name.BooleanValueBreak"));
    this.charValueBreakClass = (Class<CharValueBreak<?, ?>>)Class.forName(properties.getProperty("name.CharValueBreak"));
    this.arrayValueBreakClass = (Class<ArrayValueBreak<?, ?, ?>>)Class.forName(properties.getProperty("name.ArrayValueBreak"));

    this.vaadinVersion = properties.getProperty("vaadin.version");
  }

  // Fields
  //--------------------------------------------------

  private final String version;

  private final Class<IFluentFactory<?, ?>> iFluentFactoryClass;

  private final Class<FluentFactory<?, ?>> fluentFactoryClass;

  private final Class<ValueBreak<?, ?, ?>> valueBreakClass;

  private final Class<ByteValueBreak<?, ?>> byteValueBreakClass;

  private final Class<ShortValueBreak<?, ?>> shortValueBreakClass;

  private final Class<IntValueBreak<?, ?>> intValueBreakClass;

  private final Class<LongValueBreak<?, ?>> longValueBreakClass;

  private final Class<FloatValueBreak<?, ?>> floatValueBreakClass;

  private final Class<DoubleValueBreak<?, ?>> doubleValueBreakClass;

  private final Class<BooleanValueBreak<?, ?>> booleanValueBreakClass;

  private final Class<CharValueBreak<?, ?>> charValueBreakClass;

  private final Class<ArrayValueBreak<?, ?, ?>> arrayValueBreakClass;

  private final String vaadinVersion;

}
