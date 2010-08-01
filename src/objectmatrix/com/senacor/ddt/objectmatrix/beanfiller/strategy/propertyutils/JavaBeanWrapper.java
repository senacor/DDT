/*
 * Copyright (c) 2008 Senacor Technologies AG.
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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;

import com.senacor.ddt.objectmatrix.beanfiller.NoSuchPropertyException;
import com.senacor.ddt.objectmatrix.beanfiller.PropertyAccessException;
import com.senacor.ddt.objectmatrix.beanfiller.PropertyNotFoundException;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;

public class JavaBeanWrapper implements BeanWrapper {
  
  private final Object bean;
  
  public JavaBeanWrapper(final Object wrapped) {
    ParamChecker.notNull("wrapped", wrapped);
    this.bean = wrapped;
  }
  
  public Class getPropertyType(final String propertyName, final Properties annotation) {
    PropertyDescriptor pd;
    try {
      pd = PropertyUtils.getPropertyDescriptor(this.bean, propertyName);
    } catch (final Exception e) {
      throw new PropertyAccessException(this.bean.getClass(), propertyName, e);
    }
    final Class result;
    if (pd == null) {
      try {
        result = getAccessibleField(this.bean.getClass(), propertyName).getType();
      } catch (final Exception e) {
        throw new PropertyNotFoundException(this.bean.getClass().getName() + "." + propertyName, e);
      }
    } else {
      result = pd.getPropertyType();
    }
    return result;
  }
  
  public Object read(final String propertyName) {
    try {
      return PropertyUtils.getProperty(this.bean, propertyName);
    } catch (final Exception e) {
      try {
        return readFieldDirectly(this.bean, propertyName);
      } catch (final Exception e2) {
        throw new NoSuchPropertyException(this.bean.getClass(), propertyName, e, e2);
      }
    }
  }
  
  public void write(final String propertyName, final Object propertyValueToWrite) {
    final Class propertyType = getPropertyType(propertyName, null);
    if (propertyValueToWrite != null) {
      validateTypeToWrite(propertyType, propertyValueToWrite.getClass());
    }
    try {
      PropertyUtils.setProperty(this.bean, propertyName, propertyValueToWrite);
    } catch (final Exception e) {
      try {
        writeFieldDirectly(this.bean, propertyName, propertyValueToWrite);
      } catch (final Exception e2) {
        throw new NoSuchPropertyException(this.bean.getClass(), propertyName, e, e2);
      }
    }
  }
  
  public Object getWrapped() {
    return this.bean;
  }
  
  private Field getAccessibleField(final Class type, final String fieldName) throws NoSuchFieldException {
    final Class startingType = type;
    Class currentType = startingType;
    Field field = findField(fieldName, currentType.getFields());
    while ((field == null) && !currentType.equals(Object.class)) {
      field = findField(fieldName, currentType.getDeclaredFields());
      currentType = currentType.getSuperclass();
    }
    if (field == null) {
      throw new NoSuchFieldException("Can't find field '" + fieldName + "' in class '" + startingType.getName() + "'");
    } else {
      return field;
    }
  }
  
  private Field findField(final String fieldName, final Field[] fields) {
    for (int i = 0; i < fields.length; i++) {
      if (fields[i].getName().equals(fieldName)) {
        if (!fields[i].isAccessible()) {
          fields[i].setAccessible(true);
        }
        return fields[i];
      }
    }
    return null;
  }
  
  private void writeFieldDirectly(final Object bean, final String propertyName, final Object propertyValue)
      throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
    final Field field = getAccessibleField(bean.getClass(), propertyName);
    field.set(bean, propertyValue);
  }
  
  private Object readFieldDirectly(final Object bean, final String propertyName) throws NoSuchFieldException,
      IllegalArgumentException, IllegalAccessException {
    final Field field = getAccessibleField(bean.getClass(), propertyName);
    return field.get(bean);
  }
  
  public Class resolveTypeForInstantiation(final String propertyName, final Class typeToInstantiate,
      final Properties annotation) {
    final Class propertyType = getPropertyType(propertyName, null);
    if (typeToInstantiate == null) {
      return propertyType;
    } else {
      return validateTypeToWrite(propertyType, typeToInstantiate);
    }
  }
  
  private Class validateTypeToWrite(final Class propertyType, final Class typeToWrite) {
    if (autobox(propertyType).isAssignableFrom(autobox(typeToWrite))) {
      return typeToWrite;
    } else {
      throw new TypeMismatchException(propertyType, typeToWrite);
    }
  }
  
  private Class autobox(final Class possiblyPrimitive) {
    if (possiblyPrimitive.isPrimitive()) {
      return (Class) Transformer.BOXED_TYPES.get(possiblyPrimitive);
    } else {
      return possiblyPrimitive;
    }
  }
}
