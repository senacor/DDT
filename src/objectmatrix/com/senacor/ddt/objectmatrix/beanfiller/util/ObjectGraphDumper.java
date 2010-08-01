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

package com.senacor.ddt.objectmatrix.beanfiller.util;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.objectmatrix.EmbeddedAnnotationMatrixDecorator;
import com.senacor.ddt.objectmatrix.beanfiller.BeanFiller;
import com.senacor.ddt.objectmatrix.properties.PropertyFileStringMatrixReader;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;

/**
 * Utility class to generate Properties out of a given object graph without too much handwriting. The properties are
 * built such that they can be written to a properties file that can be read by {@link PropertyFileStringMatrixReader}
 * or used as the basis for creating other kinds of ObjectMatrix inputs, like CSV files or Excel sheets.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class ObjectGraphDumper {
  private static final Log log = LogFactory.getLog(ObjectGraphDumper.class);
  
  private Transformer localTransformer = Transformer.get();
  
  private final Set filters = new HashSet();
  
  private final Set leafClasses = new HashSet() {
    {
      add(Number.class);
      add(Character.class);
      add(String.class);
      add(StringBuffer.class);
      add(Date.class);
      add(Calendar.class);
      Iterator iterator = Transformer.BOXED_TYPES.values().iterator();
      while (iterator.hasNext()) {
        Class boxed = (Class) iterator.next();
        add(boxed);
      }
    }
  };
  
  /**
   * Dump the given bean into a new Properties object, using the given bean name as a key prefix.
   * 
   * @param bean
   *          The bean to be dumped. May be null.
   * @param beanName
   *          The prefix to use. Must not be null but may be blank.
   * @return A properties object containing a useful dump of the bean.
   */
  public Properties dump(final Object bean, final String beanName) {
    ParamChecker.notNull("beanName", beanName);
    final Properties result = new Properties();
    dump(bean, beanName, result);
    if (log.isDebugEnabled()) {
      result.list(System.out);
    }
    return result;
  }
  
  /**
   * Dump the given bean into the given Properties object, using the given bean name as a key prefix.
   * 
   * @param bean
   *          The bean to be dumped. May be null.
   * @param beanName
   *          The prefix to use. Must not be null but may be blank.
   * @param result
   *          The properties object into which the bean will be dumped. Must not be null.
   * @return The properties object given as parameter <code>result</code>, now containing a useful dump of the bean.
   */
  public Properties dump(final Object bean, final String beanName, final Properties result) {
    ParamChecker.notNull("beanName", beanName);
    ParamChecker.notNull("result", result);
    // output type of root bean (i.e. the one without . or [ in its name. we can't compare the root
    // bean's type to any field type
    // so to be safe we always print its type
    if ((bean != null) && (beanName.indexOf('.') == -1) && (beanName.indexOf('[') == -1)) {
      result.setProperty(beanName + "~type=", bean.getClass().getName());
    }
    // only dump this bean if it is not filtered
    if (!matchesFilter(beanName)) {
      if (bean == null) {
        result.setProperty(beanName, "~null");
      } else if (bean.getClass().isArray()) {
        dumpCollection(Arrays.asList((Object[]) bean), beanName, result);
      } else if (bean instanceof Collection) {
        dumpCollection((Collection) bean, beanName, result);
      } else if (bean instanceof Map) {
        dumpMap((Map) bean, beanName, result);
      } else if (isLeaf(bean.getClass())) {
        dumpLeaf(bean, beanName, result);
      } else {
        dumpBean(bean, beanName, result);
      }
    }
    return result;
  }
  
  private boolean matchesFilter(final String beanName) {
    return this.filters.contains(beanName);
  }
  
  protected void dumpMap(final Map map, final String beanName, final Properties result) {
    // TODO cmenzel Auto-generated method stub
    //
    throw new UnsupportedOperationException("Maps are not supported yet");
  }
  
  protected void dumpCollection(final Collection collection, final String beanName, final Properties result) {
    if (!collection.isEmpty()) {
      final Iterator iter = collection.iterator();
      final List elementTypes = new ArrayList(collection.size());
      boolean allEqualTypes = true;
      for (int i = 0; iter.hasNext(); i++) {
        final String indexedBeanName = beanName + "[" + i + "]";
        final Object indexedBean = iter.next();
        final Class indexedBeanType = indexedBean.getClass();
        if (!elementTypes.contains(indexedBeanType) && (elementTypes.size() > 0)) {
          allEqualTypes = false;
        }
        elementTypes.add(indexedBeanType);
        dump(indexedBean, indexedBeanName, result);
      }
      if (allEqualTypes) {
        final String elementTypeHint =
            beanName + EmbeddedAnnotationMatrixDecorator.ANNOTATION_MARK + BeanFiller.AnnotationKeys.ELEMENT_TYPE;
        final String elementTypeHintValue = ((Class) elementTypes.get(0)).getName();
        result.setProperty(elementTypeHint, elementTypeHintValue);
      } else {
        for (int i = 0; i < elementTypes.size(); i++) {
          final Class elementType = (Class) elementTypes.get(i);
          final String elementTypeHint =
              beanName + "[" + i + "]" + EmbeddedAnnotationMatrixDecorator.ANNOTATION_MARK
                  + BeanFiller.AnnotationKeys.TYPE_HINT;
          final String elementTypeHintValue = elementType.getName();
          result.setProperty(elementTypeHint, elementTypeHintValue);
        }
      }
    }
  }
  
  protected void dumpBean(final Object bean, final String beanName, final Properties result) {
    log.debug("dumpBean: " + beanName);
    final PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean.getClass());
    for (int i = 0; i < descriptors.length; i++) {
      final PropertyDescriptor descriptor = descriptors[i];
      // we're only interested in read/write-properties. we don't need unreadable properties
      // since they can't be read by us (duh!) and we don't need unwritable properties, since
      // they can't ever be written back from the dump.
      if ((descriptor.getReadMethod() != null) && (descriptor.getWriteMethod() != null)) {
        final String propertyName = descriptor.getName();
        final String qualifiedPropertyName = beanName + "." + propertyName;
        try {
          final Object propertyValue = PropertyUtils.getProperty(bean, propertyName);
          if ((propertyValue != null) && !propertyValue.getClass().equals(descriptor.getPropertyType())
              && !descriptor.getPropertyType().isPrimitive()) {
            result.setProperty(qualifiedPropertyName + "~type=", propertyValue.getClass().getName());
          }
          dump(propertyValue, qualifiedPropertyName, result);
        } catch (final Exception e) {
          throw new RuntimeException("error attempting to read property " + qualifiedPropertyName + "!", e);
        }
      }
    }
  }
  
  protected void dumpLeaf(final Object leaf, final String leafName, final Properties result) {
    String propertyAsString = toString(leaf);
    if (propertyAsString == null) {
      propertyAsString = "";
    }
    result.setProperty(leafName, propertyAsString);
  }
  
  protected final boolean isLeaf(final Class propertyType) {
    if (propertyType.isPrimitive()) {
      return true; // primitives are always leaves
    } else if (this.leafClasses.contains(propertyType)) {
      return true; // if it's marked as a leaf class then it probably is a leaf
    } else {
      // check whether this type descends from one of the known leaf types
      final Iterator iter = this.leafClasses.iterator();
      while (iter.hasNext()) {
        final Class type = (Class) iter.next();
        if (type.isAssignableFrom(propertyType)) {
          // we have a descendant of a leaf type
          // 1. include it in the leafClasses set so we don't have to iterate over all types next
          // time
          this.leafClasses.add(propertyType);
          // 2. return yes, this is a leaf type
          return true;
        }
      }
      // nope, not a leaf type
      return false;
    }
  }
  
  protected final String toString(final Object property) {
    return (String) this.localTransformer.transform(property, String.class);
  }
  
  /**
   * Add a new type as "known leaf type". If an object is encountered in the object graph that is of a leaf type or
   * extends a leaf type, then that object will be transformed into a string and added to the dump. Other types are
   * taken to be beans and the dump will continue by branching out below them.
   * <p>
   * Example:
   * 
   * <pre>
   * class MyBean {
   *   String foo;
   *   
   *   double bar;
   *   
   *   MyBean bean;
   * }
   * </pre>
   * 
   * In this example, MyBean is not a leaf type, therefore, lines such as "myBean.bean.foo" may appear in the output.
   * String and double are leaf types, so they will be treated as leaves: The graph will end at "myBean.foo" or
   * "myBean.bean.foo" and not branch further at these points.
   * 
   * @param newLeafType
   *          The type to add as leaf.
   */
  public void addLeafType(final Class newLeafType) {
    ParamChecker.notNull("newLeafType", newLeafType);
    this.leafClasses.add(newLeafType);
  }
  
  /**
   * Add the path and name of a property that should not appear in the dump.
   * <p>
   * Example:
   * 
   * <pre>
   * class MyBean {
   *   String foo;
   *   
   *   double bar;
   *   
   *   MyBean bean;
   * }
   * </pre>
   * 
   * If you use "myBean" as prefix name and do not want "foo" to appear in your dump, add "myBean.foo" as a filter
   * string.
   * 
   * @param filter
   *          The filter string to add.
   */
  public void addFilter(final String filter) {
    this.filters.add(filter);
  }
  
  /**
   * Set an alternate Transformer to use. By default, this class uses the global transformer returned by
   * Transformer.get().
   * 
   * @param localTransformer
   *          The new transformer. Must not be null.
   */
  public void setLocalTransformer(final Transformer localTransformer) {
    ParamChecker.notNull("localTransformer", localTransformer);
    this.localTransformer = localTransformer;
  }
}
