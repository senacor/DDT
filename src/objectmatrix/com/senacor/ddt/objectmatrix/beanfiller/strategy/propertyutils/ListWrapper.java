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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;

/**
 * {@link BeanWrapper} implementation that wraps {@link List}s or general
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class ListWrapper extends AbstractIndexedPropertyWrapper implements BeanWrapper {
  
  private final List list;
  
  /**
   * @param type
   *          Type of the target object. If <code>object</code> is null, an object of this type will be instantiated. If
   *          {@link List}, this will default to {@link ArrayList}. If not List, it must be a concrete implementation of
   *          List. May not be null. This type must have a default constructor if <code>object</code> is null.
   * @param object
   *          The target object that is to be wrapped. If null, a new object of the type <code>type</code> will be
   *          instantiated. Otherwise, this object must implement <code>type</code>.
   * @param minimumSize
   *          Required minimum list size. The wrapper will ensure that
   *          <code>((Collection)getWrapped()).size() >= minimumSize</code>, as long as the wrapped collection is not
   *          modified by other code. Must not be negative.
   * @param annotation
   *          DDT Annotations that are applicable to the object being wrapped, if any. Must not be null, but may be
   *          empty.
   * @throws InstantiationException
   *           If an object could not be instantiated
   * @throws IllegalAccessException
   *           If an object could not be instantiated
   * @throws ClassNotFoundException
   *           If the element-type defined by the annotation is not available.
   */
  public ListWrapper(final Class type, final Object object, final int minimumSize, final Properties annotation,
      final Transformer transformer) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    super(transformer);
    ParamChecker.notNull("type", type);
    ParamChecker.require("type must be java.util.List or implement it and be concrete", List.class.equals(type)
        || (List.class.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers())));
    if (object != null) {
      ParamChecker
          .require("object must be null or implement the given type!", type.isAssignableFrom(object.getClass()));
    }
    ParamChecker.notNegative("minimumSize", minimumSize);
    ParamChecker.notNull("annotation", annotation);
    if (object != null) {
      this.list = (List) object;
    } else if (List.class.equals(type)) {
      this.list = new ArrayList();
    } else {
      this.list = (List) type.newInstance();
    }
    ensureSize(minimumSize);
    parseElementTypeFromAnnotation(annotation);
  }
  
  private void ensureSize(final int minimumSize) {
    while (this.list.size() < minimumSize) {
      this.list.add(null);
    }
  }
  
  public void write(final String propertyName, final Object propertyValue) {
    final int index = parseIndex(propertyName);
    ensureSize(index + 1);
    this.list.set(index, propertyValue);
  }
  
  public Object read(final String propertyName) {
    try {
      return this.list.get(parseIndex(propertyName));
    } catch (final IndexOutOfBoundsException e) {
      return null;
    }
  }
  
  public boolean canAcceptType(final String propertyName, final Class wantedType) {
    return !wantedType.isPrimitive();
  }
  
  public Object getWrapped() {
    return this.list;
  }
}