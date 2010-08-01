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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.objectmatrix.beanfiller.BadIndexException;
import com.senacor.ddt.objectmatrix.beanfiller.BeanAccessStrategy;
import com.senacor.ddt.objectmatrix.beanfiller.BeanFiller;
import com.senacor.ddt.objectmatrix.beanfiller.NoConcreteClassException;
import com.senacor.ddt.objectmatrix.beanfiller.PropertyAccessException;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;

public class JavaBeanAccessStrategy implements BeanAccessStrategy {
  private static final Log log = LogFactory.getLog(JavaBeanAccessStrategy.class);
  
  private Transformer transformer;
  
  private final Map wrappedObjects = new IdentityHashMap();
  
  public JavaBeanAccessStrategy() {
    // nothing to be done
  }
  
  public Class getPropertyType(final Object rawBean, final String propertyName, final Properties annotation) {
    final BeanWrapper bean = wrap(rawBean, annotation);
    
    return bean.getPropertyType(propertyName, annotation);
  }
  
  private BeanWrapper wrap(final Object rawBean, final Properties annotation) {
    if (this.wrappedObjects.containsKey(rawBean)) {
      return (BeanWrapper) this.wrappedObjects.get(rawBean);
    } else if (rawBean instanceof BeanWrapper) {
      return (BeanWrapper) rawBean;
    } else if (rawBean instanceof ObjectHolder) {
      return wrap(((ObjectHolder) rawBean).getWrapped(), annotation);
    } else {
      final BeanWrapper wrapped = createWrapper(rawBean.getClass(), rawBean, null, null, annotation);
      return wrapped;
    }
  }
  
  public Object instantiateAndSet(final Object rawBean, final String propertyName, final Class typeToInstantiate,
      final Properties annotation) {
    final BeanWrapper bean = wrap(rawBean, annotation);
    try {
      Class type = bean.resolveTypeForInstantiation(propertyName, typeToInstantiate, annotation);
      type = getConcreteType(type);
      Object value;
      if (mustBeWrapped(type)) {
        value = createWrapper(type, null, bean, propertyName, annotation);
      } else {
        value = instantiate(type);
      }
      writeProperty(bean, propertyName, value, annotation);
      return value;
    } catch (final Exception e) {
      throw new PropertyAccessException(bean.getClass(), propertyName, e);
    }
  }
  
  public Object instantiate(final Class type) throws PropertyAccessException {
    ParamChecker.notNull("type", type);
    final Object result;
    try {
      if (type.isArray()) {
        result = Array.newInstance(type.getComponentType(), 1);
      } else {
        result = type.newInstance();
      }
    } catch (final Exception e) {
      throw new CannotInstantiateException(type, e);
    }
    return result;
  }
  
  private BeanWrapper createWrapper(final Class type, final Object toBeWrapped, final Object parentBean,
      final String propertyNameInParent, final Properties annotation) {
    final BeanWrapper wrapper;
    try {
      if (Map.class.isAssignableFrom(type)) {
        wrapper = new MapWrapper(type, toBeWrapped, annotation, this.transformer);
      } else if (Collection.class.isAssignableFrom(type)) {
        if (List.class.isAssignableFrom(type)) {
          wrapper =
              new ListWrapper(type, toBeWrapped, parseMinimumSizeForIndexedProperty(annotation), annotation,
                  this.transformer);
        } else {
          // use generic collection wrapper for all collections that are not lists
          wrapper = new CollectionWrapper(type, toBeWrapped, annotation, this.transformer);
        }
      } else if (type.isArray()) {
        wrapper =
            new ArrayWrapper(type, toBeWrapped, parseMinimumSizeForIndexedProperty(annotation), parentBean,
                propertyNameInParent, this, this.transformer, annotation);
      } else {
        wrapper = new JavaBeanWrapper(toBeWrapped);
      }
    } catch (final Exception e) {
      throw new PropertyAccessException(parentBean.getClass(), propertyNameInParent, e);
    }
    if (toBeWrapped != null) {
      this.wrappedObjects.put(toBeWrapped, wrapper);
    }
    return wrapper;
  }
  
  private int parseMinimumSizeForIndexedProperty(final Properties annotations) {
    try {
      return Integer.parseInt(annotations.getProperty(BeanFiller.AnnotationKeys.LENGTH, "0"));
    } catch (final NumberFormatException e) {
      throw new BadIndexException("can't parse index", e);
    }
  }
  
  private static final Set /* <Class> */WRAPPED_TYPES = new HashSet() {
    {
      add(List.class);
      add(Set.class);
      add(Collection.class);
      add(Map.class);
    }
  };
  
  private boolean mustBeWrapped(final Class type) {
    if (type.isArray()) {
      return true;
    } else {
      final Iterator iterator = WRAPPED_TYPES.iterator();
      while (iterator.hasNext()) {
        final Class wrappedType = (Class) iterator.next();
        if (wrappedType.isAssignableFrom(type)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Object writeProperty(final Object rawBean, final String propertyName, final Object propertyValue,
      final Properties annotation) {
    final BeanWrapper bean = wrap(rawBean, annotation);
    final Object actualPropertyValue;
    {
      if (propertyValue instanceof BeanWrapper) {
        actualPropertyValue = ((BeanWrapper) propertyValue).getWrapped();
      } else {
        actualPropertyValue = propertyValue;
      }
    }
    try {
      (bean).write(propertyName, actualPropertyValue);
    } catch (final Exception e) {
      throw new PropertyAccessException(bean.getClass(), propertyName, e);
    }
    
    return propertyValue;
  }
  
  public Object readProperty(final Object rawBean, final String propertyName, final Properties annotation) {
    final BeanWrapper bean = wrap(rawBean, annotation);
    try {
      final Object property = (bean).read(propertyName);
      if ((property != null) && mustBeWrapped(property.getClass())) {
        return createWrapper(property.getClass(), property, bean, propertyName, annotation);
      } else {
        return property;
      }
    } catch (final Exception e) {
      throw new PropertyAccessException(bean.getClass(), propertyName, e);
    }
  }
  
  public boolean doesObjectImplement(final Class typeToImplement, final Object objectToCheck) {
    if (objectToCheck instanceof BeanWrapper) {
      return doesObjectImplement(typeToImplement, ((BeanWrapper) objectToCheck).getWrapped());
    } else if (objectToCheck != null) {
      return typeToImplement.isAssignableFrom(objectToCheck.getClass());
    } else {
      return false;
    }
  }
  
  public void setTransformer(final Transformer transformer) {
    ParamChecker.notNull("transformer", transformer);
    this.transformer = transformer;
  }
  
  public ObjectHolder createHolder(final Object bean, final Properties annotation) {
    if (mustBeWrapped(bean.getClass())) {
      return createWrapper(bean.getClass(), bean, null, null, annotation);
    } else {
      return new ObjectHolder() {
        public Object getWrapped() {
          return bean;
        }
      };
    }
  }
  
  /**
   * @see #getConcreteType(Class)
   */
  private final Map abstractToConcrete = new HashMap() {
    {
      put(Collection.class, List.class);
      put(List.class, ArrayList.class);
      put(Set.class, HashSet.class);
      put(Map.class, HashMap.class);
      put(Number.class, Integer.class);
      put(Calendar.class, GregorianCalendar.class);
    }
  };
  
  public Class getConcreteType(final Class type) {
    Class result = type;
    while ((result != null) && (!result.isPrimitive())
        && (result.isInterface() || Modifier.isAbstract(result.getModifiers())) && !result.isArray()) {
      result = (Class) this.abstractToConcrete.get(result);
    }
    if (result == null) {
      throw new NoConcreteClassException(type);
    } else {
      return result;
    }
  }
}
