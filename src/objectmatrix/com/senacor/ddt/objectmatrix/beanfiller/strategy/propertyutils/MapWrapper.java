/*
 * Copyright (c) 2007 Senacor Technologies AG.
 *  
 * All rights reserved. Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided that the following
 * conditions are met: 
 *
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. 
 *
 * Redistributions in binary form must reproduce the above copyright 
 * notice, this list of conditions and the following disclaimer in the 
 * documentation and/or other materials provided with the distribution. 
 *
 * Neither the name of Senacor Technologies AG nor the names of its 
 * contributors may be used to endorse or promote products derived from 
 * this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS 
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER 
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package com.senacor.ddt.objectmatrix.beanfiller.strategy.propertyutils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.senacor.ddt.objectmatrix.beanfiller.BeanFiller;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;

public class MapWrapper extends AbstractMultiValuedBeanWrapper implements BeanWrapper {
  
  private final Map map;
  
  private Class keyType;
  
  private final Transformer transformer;
  
  public MapWrapper(final Class type, final Object object, final Properties annotation, final Transformer transformer)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    super(transformer);
    ParamChecker.notNull("type", type);
    ParamChecker.notNull("transformer", transformer);
    ParamChecker.require("type must be java.util.Map or implement it and be concrete", Map.class.equals(type)
        || (Map.class.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers())));
    if (object != null) {
      ParamChecker
          .require("object must be null or implement the given type!", type.isAssignableFrom(object.getClass()));
    }
    ParamChecker.notNull("annotation", annotation);
    if (object != null) {
      this.map = (Map) object;
    } else if (Map.class.equals(type)) {
      this.map = new HashMap();
    } else {
      this.map = (Map) type.newInstance();
    }
    this.transformer = transformer;
    parseElementTypeFromAnnotation(annotation);
  }
  
  protected void parseElementTypeFromAnnotation(final Properties annotation) {
    final String elementTypeHint = annotation.getProperty(BeanFiller.AnnotationKeys.ELEMENT_TYPE);
    if (elementTypeHint == null) {
      setElementTypeFromString(null); // defaults to object
      this.keyType = String.class; // our keys are strings by default
    } else {
      final int commaIndex = elementTypeHint.indexOf(',');
      if (commaIndex > 0) {
        final String keyTypeHint = elementTypeHint.substring(0, commaIndex);
        final String valueTypeHint = elementTypeHint.substring(commaIndex + 1);
        this.keyType = (Class) this.transformer.transform(keyTypeHint, Class.class);
        setElementTypeFromString(valueTypeHint);
      } else {
        setElementTypeFromString(elementTypeHint);
        this.keyType = String.class;
      }
    }
    assert this.keyType != null;
    assert getElementType() != null;
  }
  
  public Object read(final String propertyName) {
    return this.map.get(createKeyFromString(propertyName));
  }
  
  private Object createKeyFromString(final String propertyName) {
    return this.transformer.transform(chopBracketsIfNecessary(propertyName), this.keyType);
  }
  
  public void write(final String propertyName, final Object propertyValue) {
    this.map.put(createKeyFromString(propertyName), propertyValue);
  }
  
  public boolean canAcceptType(final String propertyName, final Class wantedType) {
    return getElementType().isAssignableFrom(wantedType);
  }
  
  public Object getWrapped() {
    return this.map;
  }
  
}
