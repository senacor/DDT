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

import java.lang.reflect.Array;
import java.util.Properties;

import com.senacor.ddt.objectmatrix.beanfiller.BeanAccessStrategy;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;
import com.senacor.ddt.util.StateChecker;

/**
 * A {@link BeanWrapper} for arrays. If new properties are set with an index that would not fit into the wrapped array,
 * a new array of the appropriate size is created and transparently substituted in the parent bean. This requires that
 * the parent's array property is writable.
 * 
 * @author Carl-Eric Menzel
 */
public class ArrayWrapper extends AbstractIndexedPropertyWrapper {
  
  private final Class componentType;
  
  private Object array;
  
  private final Object parentBean;
  
  private final BeanAccessStrategy accessor;
  
  private final String propertyNameInParent;
  
  /**
   * @param type
   *          Array type. Not null, <code>type.isArray()</code> must be true.
   * @param object
   *          The array to wrap. If null, a new array will be created.
   * @param minimumSize
   *          Minimum array size. This is the size the array will be initialized to. Must be non-negative.
   * @param parentBean
   *          The parent bean that contains the wrapped array. Both this and propertyNameInParent must be set or null.
   *          If null, the array will not be written into a parent bean when it is resized and must be retrieved with
   *          {@link #getWrapped()}.
   * @param propertyNameInParent
   *          The array property's name in the parent bean. Not blank if parentBean is non-null.
   * @param accessor
   *          A suitable access strategy to write the parent's properties. Not null.
   * @param transformer
   *          A transformer to use. Not null.
   */
  public ArrayWrapper(final Class type, final Object object, final int minimumSize, final Object parentBean,
      final String propertyNameInParent, final BeanAccessStrategy accessor, final Transformer transformer,
      final Properties annotation) {
    super(transformer, annotation);
    ParamChecker.notNull("type", type);
    ParamChecker.require("Type must be an array!", type.isArray());
    ParamChecker.notNegative("minimumSize", minimumSize);
    ParamChecker.notNull("accessor", accessor);
    ParamChecker.require("parentBean and propertyNameInParent must both be set or both be null",
        ((parentBean != null) && (propertyNameInParent != null))
            || ((parentBean == null) && (propertyNameInParent == null)));
    if (parentBean == null) {
      this.parentBean = this;
      this.propertyNameInParent = null;
    } else {
      this.propertyNameInParent = propertyNameInParent;
      this.parentBean = parentBean;
    }
    this.accessor = accessor;
    this.componentType = type.getComponentType();
    if (Object.class.equals(getElementType())) {
      setElementType(this.componentType);
    }
    StateChecker.require("element type must be assignable to array component type", this.componentType
        .isAssignableFrom(getElementType()));
    if (object != null) {
      ParamChecker.require("object must be null or of the given type!", type.isAssignableFrom(object.getClass()));
      this.array = object;
    }
    ensureSize(minimumSize);
    assert this.array != null;
    assert this.componentType != null;
  }
  
  private void ensureSize(final int minimumSize) {
    if ((this.array == null) || (Array.getLength(this.array) < minimumSize)) {
      final Object oldArray = this.array;
      this.array = Array.newInstance(this.componentType, minimumSize);
      if (oldArray != null) {
        System.arraycopy(oldArray, 0, this.array, 0, Array.getLength(oldArray));
      }
      if (this.parentBean != this) {
        this.accessor.writeProperty(this.parentBean, this.propertyNameInParent, this.array, new Properties());
      }
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.beanfiller.strategy.propertyutils.BeanWrapper#getWrapped()
   */
  public Object getWrapped() {
    return this.array;
  }
  
  /**
   * Read a property of the wrapped object, i.e. a field of the array.
   * 
   * @see com.senacor.ddt.objectmatrix.beanfiller.strategy.propertyutils.BeanWrapper#read(java.lang.String)
   * @param propertyName
   *          String representation of an integer array index. Not blank, not negative, integer only.
   */
  public Object read(final String propertyName) {
    try {
      return Array.get(this.array, parseIndex(propertyName));
    } catch (final ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }
  
  /**
   * Write to a field of the wrapped array.
   * 
   * @see com.senacor.ddt.objectmatrix.beanfiller.strategy.propertyutils.BeanWrapper#write(java.lang.String,
   *      java.lang.Object)
   * @param propertyName
   *          String representation of an integer array index. Not blank, not negative, integer only.
   * @param propertyValue
   *          The value to be written. Must be compatible to the array's component type.
   */
  public void write(final String propertyName, final Object propertyValue) {
    final int index = parseIndex(propertyName);
    ensureSize(index + 1);
    Array.set(this.array, index, propertyValue);
  }
  
  /**
   * Return the type of the named property. This will always be the array's component type.
   * 
   * @see com.senacor.ddt.objectmatrix.beanfiller.strategy.propertyutils.AbstractMultiValuedBeanWrapper#getPropertyType(java.lang.String,
   *      java.util.Properties)
   * @param propertyName
   *          Property name. Not blank.
   * @param annotations
   *          Any available annotations.
   */
  public Class getPropertyType(final String propertyName, final Properties annotations) {
    return this.componentType;
  }
  
  public Class resolveTypeForInstantiation(final String propertyName, final Class typeToInstantiate,
      final Properties annotation) {
    if (typeToInstantiate == null) {
      return getElementType();
    } else if (this.componentType.isAssignableFrom(typeToInstantiate)) {
      return typeToInstantiate;
    } else {
      throw new TypeMismatchException(this.componentType, typeToInstantiate);
    }
  }
}