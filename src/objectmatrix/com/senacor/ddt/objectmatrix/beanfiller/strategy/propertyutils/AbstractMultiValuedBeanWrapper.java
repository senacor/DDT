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

import java.util.Properties;

import com.senacor.ddt.objectmatrix.beanfiller.BeanFiller;
import com.senacor.ddt.typetransformer.NoSuccessfulTransformerException;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;

public abstract class AbstractMultiValuedBeanWrapper implements BeanWrapper {
  
  protected final void setElementType(final Class elementType) {
    this.elementType = elementType;
  }
  
  private final Transformer transformer;
  
  protected AbstractMultiValuedBeanWrapper(final Transformer transformer) {
    ParamChecker.notNull("transformer", transformer);
    this.transformer = transformer;
  }
  
  protected AbstractMultiValuedBeanWrapper(final Transformer transformer, final Properties annotation) {
    this(transformer);
    parseElementTypeFromAnnotation(annotation);
  }
  
  private Class elementType;
  
  protected void parseElementTypeFromAnnotation(final Properties annotation) {
    final String elementTypeHint = annotation.getProperty(BeanFiller.AnnotationKeys.ELEMENT_TYPE);
    setElementTypeFromString(elementTypeHint);
  }
  
  protected void setElementTypeFromString(final String elementTypeHint) {
    if (elementTypeHint != null) {
      this.elementType = (Class) this.transformer.transform(elementTypeHint, Class.class);
    } else {
      this.elementType = Object.class;
    }
  }
  
  protected String chopBracketsIfNecessary(final String string) {
    String result = string;
    if (result.startsWith("[")) {
      result = result.substring(1, result.length());
    }
    if (result.endsWith("]")) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }
  
  protected Class getElementType() {
    return this.elementType;
  }
  
  public Class getPropertyType(final String propertyName, final Properties annotations) {
    final Object value = read(propertyName);
    if (value != null) {
      return value.getClass();
    } else if (annotations.containsKey(BeanFiller.AnnotationKeys.TYPE_HINT)) {
      try {
        return (Class) this.transformer.transform(annotations.getProperty(BeanFiller.AnnotationKeys.TYPE_HINT),
            Class.class);
      } catch (final NoSuccessfulTransformerException e) {
        throw new TypeDiscoveryFailedException(e);
      }
    } else {
      return getElementType();
    }
  }
  
  public Class resolveTypeForInstantiation(final String propertyName, final Class typeToInstantiate,
      final Properties annotation) {
    if (typeToInstantiate == null) {
      return this.elementType;
    } else if (this.elementType.isAssignableFrom(typeToInstantiate)) {
      return typeToInstantiate;
    } else {
      throw new TypeMismatchException(this.elementType, typeToInstantiate);
    }
  }
}
