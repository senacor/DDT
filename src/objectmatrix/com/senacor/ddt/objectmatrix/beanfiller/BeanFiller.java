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
package com.senacor.ddt.objectmatrix.beanfiller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.objectmatrix.AnnotatedStringMatrix;
import com.senacor.ddt.objectmatrix.EmbeddedAnnotationMatrixDecorator;
import com.senacor.ddt.objectmatrix.KeyNotFoundException;
import com.senacor.ddt.objectmatrix.ObjectMap;
import com.senacor.ddt.objectmatrix.ObjectMatrix;
import com.senacor.ddt.objectmatrix.beanfiller.BeanAccessStrategy.ObjectHolder;
import com.senacor.ddt.objectmatrix.beanfiller.strategy.propertyutils.JavaBeanAccessStrategy;
import com.senacor.ddt.typetransformer.NoSuccessfulTransformerException;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;
import com.senacor.ddt.util.StringFilter;

/**
 * This class provides methods to recursively fill a java bean (or an entire object graph, starting at a root bean),
 * reading data from an {@link ObjectMatrix}.
 * <p>
 * A single BeanFiller instance is bound to {@link ObjectMap}, which is usually bound to one column of an ObjectMatrix.
 * It is stateful but properly synchronized.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class BeanFiller {
  private static final Log log = LogFactory.getLog(BeanFiller.class);
  
  /**
   * @see #addLeafType(Class)
   */
  private final Set knownLeafTypes = new HashSet() {
    {
      addAll(Transformer.BOXED_TYPES.values());
      addAll(Transformer.BOXED_TYPES.keySet());
    }
  };
  
  private StringFilter keyFilter;
  
  private boolean ignoreAllNulls;
  
  /**
   * The data source.
   */
  private final ObjectMap objectMap;
  
  /**
   * The objectMap's keys that are relevant for a filling operation. These are found via
   * {@link ObjectMap#filterKeys(String, String, String)}.
   */
  private List relevantKeys;
  
  /**
   * A cache containing objects already encountered during a filling operation. The key is simply the string path to the
   * object within the current object graph.
   */
  private Map /* <String, Object> */beanCache;
  
  private Map /* <String, Properties> */annotationCache;
  
  private Set /* <String> */leafNodes;
  
  private String lastSeenNullNodeKey;
  
  private final BeanAccessStrategy accessor;
  
  private final Transformer transformer;
  
  private static final Comparator RELEVANT_KEYS_COMPARATOR = new KeyAwareComparator();
  
  /**
   * Create a new BeanFiller with the given access strategy for the beans. Use this if the beans you want to fill are
   * not standard JavaBeans.
   * 
   * @param objectMap
   *          The map this BeanFiller will read data from.
   * @param accessor
   *          The strategy used to read and write properties.
   */
  public BeanFiller(final ObjectMap objectMap, final BeanAccessStrategy accessor) {
    ParamChecker.notNull("objectMap", objectMap);
    ParamChecker.notNull("accessor", accessor);
    this.accessor = accessor;
    this.transformer = objectMap.getTransformer();
    this.accessor.setTransformer(this.transformer);
    this.objectMap = objectMap;
  }
  
  /**
   * Create a new BeanFiller for use with normal JavaBeans ("POJOs"). This is the correct strategy for almost all use
   * cases.
   * 
   * @param objectMap
   *          The map this BeanFiller will read data from.
   */
  public BeanFiller(final ObjectMap objectMap) {
    this(objectMap, new JavaBeanAccessStrategy());
  }
  
  static final class KeyAwareComparator implements Comparator {
    
    private static final Map cache = new HashMap();
    
    public int compare(final Object o1, final Object o2) {
      final KeySplit k1 = cachedKeySplit((String) o1);
      final KeySplit k2 = cachedKeySplit((String) o2);
      
      return k1.compareTo(k2);
    }
    
    private KeySplit cachedKeySplit(final String key) {
      KeySplit res = (KeySplit) cache.get(key);
      if (null == res) {
        res = new KeySplit(key);
        cache.put(key, res);
      }
      return res;
    }
    
    class KeySplit implements Comparable {
      private final String key;
      private final KeySplit next;
      private final Integer intValue;
      
      public KeySplit(final String key) {
        final int indexDot = key.indexOf('.');
        final int indexBracket = key.indexOf('[');
        {
          final String prefix;
          final String suffix;
          if ((indexDot > -1) && ((indexBracket == -1) || (indexDot < indexBracket))) {
            prefix = key.substring(0, indexDot);
            suffix = key.substring(indexDot + 1);
          } else if ((indexBracket > -1) && ((indexDot == -1) || (indexBracket < indexDot))) {
            prefix = key.substring(0, indexBracket);
            final int indexClosingBracket = key.indexOf(']', indexBracket);
            // eliminate the closing bracket from the suffix
            suffix =
                key.substring(indexBracket + 1, indexClosingBracket) + '.' + key.substring(indexClosingBracket + 1);
          } else {
            prefix = key;
            suffix = "";
          }
          this.key = prefix;
          if (!"".equals(suffix)) {
            this.next = new KeySplit(suffix);
          } else {
            this.next = null;
          }
        }
        {
          Integer intValue;
          try {
            intValue = new Integer(Integer.parseInt(this.key));
          } catch (final NumberFormatException e) {
            intValue = null;
          }
          this.intValue = intValue;
        }
      }
      
      public int compareTo(final Object o) {
        if (this == o) {
          return 0;
        }
        
        if (o == null) {
          return -1;
        }
        
        final KeySplit that = (KeySplit) o;
        
        int comparison;
        {
          if ((this.intValue != null) && (that.intValue != null)) {
            comparison = this.intValue.compareTo(that.intValue);
          } else {
            comparison = this.key.compareTo(that.key);
          }
          
          if (comparison == 0) {
            if (this.next != null) {
              comparison = this.next.compareTo(that.next);
            } else if (that.next != null) {
              comparison = -1;
            } else {
              comparison = 0;
            }
          }
        }
        
        return comparison;
      }
      
    }
  }
  
  /**
   * This interface contains the various annotation keys that are understood by BeanFiller.
   */
  public interface AnnotationKeys extends ObjectMatrix.AnnotationKeys {
    /**
     * Indicates the type the elements of a collection or array should have.
     */
    public static final String ELEMENT_TYPE = "element-type";
    
    /**
     * Indicates the type a property should have.
     */
    public static final String TYPE_HINT = "type";
    
    /**
     * Indicates the minimum size an array or collection should have.
     */
    public static final String LENGTH = "length";
    
    /**
     * Indicates that an item should be completely ignored.
     * 
     * @see BeanFiller#shouldSkipWithAnnotations(String, Object)
     */
    public static final String IGNORE = "ignore";
    
    /**
     * Indicates that an item should be completely ignored if its contents are null.
     * 
     * @see BeanFiller#shouldSkipWithAnnotations(String, Object)
     */
    public static final String IGNORE_IF_NULL = "ignore-if-null";
    
    /**
     * Indicates that a property should not be ignored if it contains no value but instantiated with its default value
     * (e.g. type.newInstance() or similar).
     */
    public static final String INSTANTIATE_EMPTY = "empty";
  }
  
  /**
   * Indicates whether a global flag to ignore all null values is set. The effect is identical to a global
   * "ignore-if-null" annotation in the underlying {@link ObjectMap}.
   * 
   * @return The flag.
   * @see AnnotationKeys#IGNORE_IF_NULL
   */
  public boolean isIgnoreAllNulls() {
    return this.ignoreAllNulls;
  }
  
  /**
   * @see #isIgnoreAllNulls()
   */
  public void setIgnoreAllNulls(final boolean ignoreAllNullRows) {
    this.ignoreAllNulls = ignoreAllNullRows;
  }
  
  /**
   * Fill the given bean with data found in the ObjectMap, identified by keys with the common prefix given as
   * <code>beanName</code>. Data that is already present is overwritten if data for the particular property is found in
   * the object map. Other data is left untouched.
   * <p>
   * Beans are described by a roughly PropertyUtils-like expression language, one key per property. Metadata is supplied
   * by annotations as specified by {@link AnnotatedStringMatrix}. The examples in this documentation are based on the
   * default implementation {@link EmbeddedAnnotationMatrixDecorator}. If you use a different implementation, check its
   * documentation to see how to apply annotations. BeanFiller itself defines only the annotation keys and their
   * allowable values.
   * <p>
   * Object graphs can be arbitrarily deep. A bean with the properties foo, bar and baz, with bar having sub-properties
   * quux and quax, can be expressed by the following keys, assuming a bean name of myBean:
   * <ul>
   * <li>myBean.foo</li>
   * <li>myBean.bar</li>
   * <li>myBean.baz</li>
   * <li>myBean.bar.quux</li>
   * <li>myBean.bar.quax</li>
   * </ul>
   * Array, Collections and Maps are fully supported by the common bracket-index notation:
   * <ul>
   * <li>myBean.someList[13].someProperty</li>
   * <li>myBean.someSet[0].someProperty</li>
   * <li>myBean.anArray[1].foo</li>
   * <li>myBean.myMap[someKey].quux</li>
   * </ul>
   * Even Sets are supported. Since sets don't have a way to address an element in the set, the index used is
   * artificial. It is only used to construct the objects that are added to the set and can not be used to address
   * objects that are already in the set.
   * <p>
   * Collections and Maps must be told about the type of their elements. This can be done
   * <ul>
   * <li>for all elements, using the element-type annotation: <code>myBean.myList~element-type=com.foo.Bar</code></li>
   * <li>for a single element: <code>myBean.myList[0]~type=com.foo.Bar</code></li>
   * </ul>
   * <p>
   * Not all nodes of an object graph need to be specified. BeanFiller will walk along any valid graph and instantiate
   * intermediate nodes if needed (and if possible - this depends on your {@link BeanAccessStrategy}). The default
   * implementation requires a no-arg constructor. With this feature, the following is valid:
   * <ul>
   * <li>myBean.someProperty</li>
   * <li>myBean.someProperty.some.inter.mediate.objects.someLeafNode</li>
   * </ul>
   * Even if the intermediate objects are null, this will work: some, inter, mediate and objects will all be
   * instantiated as the BeanFiller walks along the graph.
   * <p>
   * This is very useful, but also introduces a small problem: The graph will always be instantiated, even if the last
   * property - the one we're actually specifying - is empty. That leads to the following:
   * <ul>
   * <li>customer.address.street</li>
   * <li>customer.address.city</li>
   * </ul>
   * Even if street and city are null, the address object will still be instantiated, but will not contain any values.
   * This is often not the desired result. To keep address from being instantiated, BeanFiller must be told to skip the
   * two lines. The annotations are <code>ignore</code> and <code>ignore-if-null</code>. <code>ignore</code> is applied
   * to a single cell in the underlying matrix and tells BeanFiller to just skip this particular cell. Use this if you
   * want to skip a line just once in a large test run. <code>ignore-if-null</code>, on the other hand, is applied for
   * example to a complete row and tells BeanFiller to skip all cells that are null.
   * <p>
   * With the default {@link BeanAccessStrategy} implementation {@link JavaBeanAccessStrategy}, all properties must be
   * available with standard JavaBean getters and setters, or as instance fields. These fields may be private -
   * {@linkplain JavaBeanAccessStrategy} will do everything it can to gain access, i.e. using setAccessible(true), if
   * allowed by the SecurityManager.
   * <p>
   * 
   * 
   * 
   * @param beanName
   *          The name of the bean. Not blank.
   * @param bean
   *          The bean to fill. Not null.
   * @return The filled bean. This is usually the object given as the <code>bean</code> parameter, unless the reference
   *         had to be discarded (e.g. when filling an array that had to be resized).
   */
  public synchronized Object fillBean(final String beanName, final Object bean) {
    ParamChecker.notBlank("beanName", beanName);
    ParamChecker.notNull("bean", bean);
    initializeBeanFilling();
    // Filter keys by "beanName.", so that when looking for "person" we don't match "person2"
    this.relevantKeys = this.objectMap.filterKeys(beanName + ".", null, null);
    this.relevantKeys.addAll(this.objectMap.filterKeys(beanName + "[", null, null));
    filterKeysToIgnore();
    if (this.relevantKeys.isEmpty()) {
      throw new NoPropertyFoundException(beanName, this.objectMap.getIdentifier());
    } else {
      final ObjectHolder root = this.accessor.createHolder(bean, getAnnotation(beanName));
      // seed the cache with the root bean. This way we don't need special treatment for the root
      // and can always
      // rely on finding something in the cache when walking back up the object graph.
      putObjectInCache(beanName, root);
      walkOverKeysAndFillBean();
      return root.getWrapped();
    }
  }
  
  /**
   * Instantiate an object and fill it with the data prefixed by <code>beanName</code>. This is a convenience shortcut
   * for {@link #fillBean(String, Object)} if you don't want to instantiate the object yourself or don't know its type
   * at compile time. This method must read the type from the underlying {@link ObjectMap}, so make sure there is a type
   * annotation.
   * 
   * @param beanName
   *          The name of the bean. Not blank.
   * @return The created and filled bean.
   */
  public synchronized Object createAndFillBean(final String beanName) {
    ParamChecker.notBlank("beanName", beanName);
    initializeFilling();
    final Object bean = instantiateFromKey(beanName);
    if (bean != null) {
      return fillBean(beanName, bean);
    } else {
      return null;
    }
  }
  
  /**
   * Instantiate the object found at the given key in the object map, reading its type from an annotation. This will
   * override any ignore flags that may be set.
   * 
   * @param beanName
   *          The key to look for in the object map.
   * @return The new object, possibly wrapped in an {@link BeanAccessStrategy.ObjectHolder ObjectHolder}. Null if the
   *         ~null annotation is found.
   */
  private Object instantiateFromKey(final String beanName) {
    if (this.objectMap.getKeys().contains(beanName)) {
      final Properties p = getAnnotation(beanName);
      if (p.containsKey(AnnotationKeys.TYPE_HINT)) {
        if (!p.containsKey(ObjectMatrix.AnnotationKeys.NULL)) {
          try {
            final Class type =
                (Class) this.objectMap.getTransformer().transform(p.getProperty(AnnotationKeys.TYPE_HINT), Class.class);
            final Object result = this.objectMap.getObject(beanName, type);
            if (result == null) {
              return this.accessor.instantiate(type);
            } else {
              return result;
            }
          } catch (final NoSuccessfulTransformerException e) {
            throw new TypeDiscoveryFailedException(beanName, e);
          }
        } else {
          return null;
        }
      } else {
        throw new TypeDiscoveryFailedException(beanName);
      }
    } else {
      throw new BeanNotFoundException(beanName);
    }
  }
  
  /**
   * Remove all keys from {@link #relevantKeys} as indicated by {@link #shouldSkip(String)}.
   */
  private void filterKeysToIgnore() {
    final ListIterator iter = this.relevantKeys.listIterator();
    while (iter.hasNext()) {
      final String key = (String) iter.next();
      if (shouldSkip(key)) {
        iter.remove();
      }
    }
  }
  
  /**
   * Initialize all fields needed for filling a bean. This must be called by fillBean() before doing anything.
   */
  private void initializeBeanFilling() {
    initializeFilling();
    setLastSeenNullNodeKey("");
    this.beanCache = new HashMap();
    this.leafNodes = new HashSet();
  }
  
  /**
   * Initialize all filling operations: Null {@link #relevantKeys} and clear {@link #annotationCache}.
   */
  private void initializeFilling() {
    this.relevantKeys = null;
    this.annotationCache = new HashMap();
  }
  
  /**
   * Iterate over the relevant keys and build the object graph based on the root bean.
   * <p>
   * This method only checks whether a key ought to be skipped or not, and if not, delegates to
   * {@link #discoverOrCreateWriteAndStoreObjectForKey(String, boolean)} for each key.
   */
  private void walkOverKeysAndFillBean() {
    Collections.sort(this.relevantKeys, RELEVANT_KEYS_COMPARATOR);
    final Iterator iter = this.relevantKeys.iterator();
    while (iter.hasNext()) {
      final String key = (String) iter.next();
      try {
        discoverOrCreateWriteAndStoreObjectForKey(key, false);
      } catch (final BeanFillerException e) {
        throw e;
      } catch (final Throwable t) {
        throw new BeanFillerException("Error while working on key '" + key + "'", t);
      }
    }
  }
  
  /**
   * Create the object associated with a key in the objectMap, write it as a property into its parent object, and store
   * it in the cache.
   * <p>
   * To do this, the parent object is discovered via the {@link #findParent(String)} method. Note that this causes the
   * entire object graph from the root bean to the node represented by the key to be initialized, if necessary.
   * <p>
   * findParent will return an object from the cache or walk backwards through the graph via recursive calls to this
   * method until it finds one in the graph. This will ultimately reach the root bean. On coming back from the
   * recursion, the graph of parent objects is built and stored in the cache.
   * 
   * @param key
   *          The key to use.
   * @param instantiateOnEmptyNull
   *          Whether to instantiate an object if the underlying value is null but does not have a ~null annotation.
   * @return The newly created object, possibly wrapped by an {@link BeanAccessStrategy.ObjectHolder ObjectHolder}.
   */
  private Object discoverOrCreateWriteAndStoreObjectForKey(final String key, final boolean instantiateOnEmptyNull) {
    if (isBehindNullNode(key)) {
      return null;
    }
    Object result;
    final Object parent = findParent(key);
    final String propertyName = extractPropertyName(key);
    result = discoverOrCreateAndWriteProperty(key, parent, propertyName, instantiateOnEmptyNull);
    putObjectInCache(key, result);
    return result;
  }
  
  /**
   * Put an object in the bean cache. This cache is cleared before filling begins.
   * 
   * @param key
   *          The key to use.
   * @param object
   *          The object to store.
   */
  private void putObjectInCache(final String key, final Object object) {
    this.beanCache.put(key, object);
  }
  
  /**
   * Checks whether the given key indicates an object path that leads past a known null value and is thus unusable.
   * 
   * @param key
   *          The key to check.
   * @return <code>true</code> if the key is unusable, <code>false</code> otherwise.
   */
  private boolean isBehindNullNode(final String key) {
    return key.startsWith(this.lastSeenNullNodeKey);
  }
  
  /**
   * Create or discover the property value for a given property and if necessary write it into the containing object.
   * 
   * @param key
   *          The key to check.
   * @param parent
   *          The parent object.
   * @param propertyName
   *          The property name.
   * @param instantiateOnEmptyNull
   *          Whether to instantiate an object if the underlying value is null but does not have a ~null annotation.
   * @return The discovered or newly created property value, possibly wrapped by an
   *         {@link BeanAccessStrategy.ObjectHolder ObjectHolder}.
   */
  private Object discoverOrCreateAndWriteProperty(final String key, final Object parent, final String propertyName,
      final boolean instantiateOnEmptyNull) {
    // if this is a leaf type, add this node.
    final Object result;
    {
      final Properties annotation = getAnnotation(key);
      final Class explicitPropertyTypeFromAnnotation = getTypeFromAnnotation(annotation);
      final Class existingPropertyType = this.accessor.getPropertyType(parent, propertyName, annotation);
      
      // check whether a value exists already.
      final Object existingProperty = this.accessor.readProperty(parent, propertyName, annotation);
      
      /*
       * if there is no pre-existing object, or it does not the type specified in the object map, or there is a value
       * specified in the map, then create new object
       */
      if ((existingProperty == null)
          || ((explicitPropertyTypeFromAnnotation != null) && !this.accessor.doesObjectImplement(
              explicitPropertyTypeFromAnnotation, existingProperty)) || isSpecifiedInMap(key)) {
        
        if (isSpecifiedInMap(key)) {
          final Class typeForProperty =
              explicitPropertyTypeFromAnnotation != null ? explicitPropertyTypeFromAnnotation : existingPropertyType;
          // there is a value in the map, so get the new object from there:
          final Object fromMap = this.objectMap.getObject(key, typeForProperty);
          if (fromMap == null) {
            if (annotation.containsKey(ObjectMatrix.AnnotationKeys.NULL)) {
              // this is an explicit null...
              if (typeForProperty.isPrimitive()) {
                // ...but we can't set a primitive property to null
                throw new PrimitiveNullException(key);
              } else {
                // ...so we remember it, so we don't walk down this path further...
                setLastSeenNullNodeKey(key);
                // ...and we explicitly null the property in the parent object.
                this.accessor.writeProperty(parent, propertyName, null, annotation);
                result = null;
              }
            } else if (annotation.containsKey(AnnotationKeys.INSTANTIATE_EMPTY)) {
              /*
               * just the value in the map is null, but there is an annotation for us to instantiate an empty object, so
               * we do just that. we use the type from the annotation,not the existing type of the property. if there is
               * no type annotated in the ObjectMap, the accessor will choose a valid type.
               */
              result =
                  this.accessor.instantiateAndSet(parent, propertyName, explicitPropertyTypeFromAnnotation, annotation);
            } else {
              /*
               * the null is not explicit and there is no direct order to instantiate one. what to do? if there is no
               * value but the caller says we need one (e.g. walking down an object graph) or the existing value doesn't
               * have the required type (e.g. the field type is abstract and the existing value doesn't have the
               * concrete type indicated in the object map), then we create a new value. If the property has a primitive
               * type, we don't, of course.
               */
              final boolean existingIsEmptyAndWeNeedToInstantiate =
                  ((existingProperty == null) && instantiateOnEmptyNull);
              final boolean existingIsNotEmptyButDoesntHaveRequiredType =
                  (existingProperty != null)
                      && ((explicitPropertyTypeFromAnnotation != null) && !this.accessor.doesObjectImplement(
                          explicitPropertyTypeFromAnnotation, existingProperty));
              final boolean propertyIsNotPrimitive = (!typeForProperty.isPrimitive());
              if (propertyIsNotPrimitive
                  && (existingIsEmptyAndWeNeedToInstantiate || (existingIsNotEmptyButDoesntHaveRequiredType))) {
                result =
                    this.accessor.instantiateAndSet(parent, propertyName, explicitPropertyTypeFromAnnotation,
                        annotation);
              } else {
                /*
                 * The value from the map is irrelevant, we need no other value value, so the result is the value we
                 * found in the parent bean.
                 */
                result = existingProperty;
              }
            }
          } else {
            result = this.accessor.writeProperty(parent, propertyName, fromMap, annotation);
          }
        } else if ((existingProperty == null) && instantiateOnEmptyNull) {
          result =
              this.accessor.instantiateAndSet(parent, propertyName, explicitPropertyTypeFromAnnotation, annotation);
        } else {
          result = existingProperty;
        }
      } else {
        result = existingProperty;
      }
      if ((result != null) && this.knownLeafTypes.contains(result.getClass())) {
        this.leafNodes.add(key);
      }
    }
    return result;
  }
  
  /**
   * Set the last key whose value was null. Since the keys are always sorted it's enough to store just the last one to
   * block null paths.
   * 
   * @param key
   *          The last null key.
   */
  private void setLastSeenNullNodeKey(final String key) {
    this.lastSeenNullNodeKey = key + "."; /*
                                           * store with appended dot, so we don't block foobar if foo was null, but only
                                           * foo.
                                           */
  }
  
  private Class getTypeFromAnnotation(final Properties annotation) {
    if (annotation.containsKey(AnnotationKeys.TYPE_HINT)) {
      final Class typeInAnnotation =
          (Class) this.transformer.transform(annotation.getProperty(AnnotationKeys.TYPE_HINT), Class.class);
      return typeInAnnotation;
    } else {
      return null;
    }
  }
  
  /**
   * Get an annotation from the underlying map.
   * 
   * @param key
   *          The key.
   * @return The annotation. Not null.
   */
  private Properties getAnnotation(final String key) {
    Properties annotation = (Properties) this.annotationCache.get(key);
    if (annotation == null) {
      try {
        annotation = this.objectMap.getAnnotation(key);
      } catch (final KeyNotFoundException e) {
        // the key could be in the middle of an object graph and not actually appear as a key in the
        // underlying object map.
        // in this case, we eat the exception and just use empty annotations:
        annotation = new Properties();
      }
      this.annotationCache.put(key, annotation);
    }
    return annotation;
  }
  
  /**
   * Checks whether the given key is specified in the underlying map. The key must be contained in the key list, and if
   * applicable, in the relevantKeys list and not be filtered.
   * 
   * @param key
   *          The key to check.
   * @return <code>true</code> if the key is specified, <code>false</code> otherwise.
   */
  private boolean isSpecifiedInMap(final String key) {
    return this.objectMap.getKeys().contains(key) && ((this.relevantKeys == null) || this.relevantKeys.contains(key))
        && !shouldSkip(key);
  }
  
  /**
   * Extract the property name from the given key, taking the part of the string after the last "." or the one enclosed
   * in the last pair of "[]", whichever comes later.
   * 
   * @param key
   *          The key to take apart. Not null.
   * @return The property name. Not null.
   */
  private String extractPropertyName(final String key) {
    final int lastDot = key.lastIndexOf(".");
    final int lastOpenBracket = key.lastIndexOf("[");
    if ((lastDot == -1) && (lastOpenBracket == -1)) {
      throw new AssertionError("this should never happen: neither . nor [ found in key");
    }
    if (lastOpenBracket > lastDot) {
      return key.substring(lastOpenBracket + 1, key.length() - 1);
    } else {
      return key.substring(lastDot + 1);
    }
  }
  
  /**
   * Get the object that is the parent for the one denoted by the given key. This method first looks into the cache. If
   * nothing is found it goes recursively up the object graph to the root object and then instantiates all required
   * intermediate objects, if possible.
   * 
   * @param key
   *          The key whose parent is needed.
   * @return The parent object.
   */
  private Object findParent(final String key) {
    final String parentKey = extractParentKey(key);
    Object parentObject = getObjectFromCache(parentKey);
    if (parentObject == null) {
      parentObject = discoverOrCreateWriteAndStoreObjectForKey(parentKey, true);
    }
    if (this.leafNodes.contains(parentKey)) {
      throw new LeafInPathException(key, parentKey);
    }
    return parentObject;
  }
  
  /**
   * Find an object in the cache.
   * 
   * @param key
   *          The key to look for.
   * @return The found object or null, if the key doesn't point to any object in the cache.
   */
  private Object getObjectFromCache(final String key) {
    return this.beanCache.get(key);
  }
  
  /**
   * Extract the key of the parent object in the graph. This is the reverse of {@link #extractPropertyName(String)}.
   * 
   * @param key
   *          The key to work on. Not blank.
   * @return The parent key. Not null.
   */
  private String extractParentKey(final String key) {
    final int lastDot = key.lastIndexOf(".");
    final int lastOpenBracket = key.lastIndexOf("[");
    if ((lastDot == -1) && (lastOpenBracket == -1)) {
      throw new AssertionError("this should never happen: neither . nor [ found in key");
    }
    if (lastOpenBracket > lastDot) {
      return key.substring(0, lastOpenBracket);
    } else {
      return key.substring(0, lastDot);
    }
  }
  
  /**
   * Checks whether a given key is {@link #setKeyFilter(StringFilter) filtered} or set to ignore by an annotation.
   * 
   * @param key
   *          The key to check. Not null.
   * @return <code>true</code> if the key is filtered or caught by an ignore annotation, <code>false</code> otherwise.
   */
  private boolean shouldSkip(final String key) {
    return isFiltered(key) || shouldSkipWithAnnotations(key);
  }
  
  /**
   * Checks whether a given key is matched by the {@link #setKeyFilter(StringFilter) Filter}.
   * 
   * @param key
   *          The key to check. Not null.
   * @return <code>true</code> if the key is filtered, <code>false</code> otherwise.
   */
  private boolean isFiltered(final String key) {
    if (this.keyFilter == null) {
      return false;
    } else {
      // reverse the filter: it selects keys that are to be excluded
      return !this.keyFilter.accepts(key);
    }
  }
  
  /**
   * Fill the given bean by discovering its name and delegating to {@link #fillBean(String, Object)}. The name is
   * discovered by taking the bean's class name, dropping the package (and, if necessary, parent class names) and
   * lowercasing the first letter of the remaining string. com.foo.Bar would thus be turned to "bar".
   * 
   * @param bean
   *          The bean to fill. Not null.
   * @return <code>bean</code>, now filled with data.
   */
  public Object fillBean(final Object bean) {
    ParamChecker.notNull("bean", bean);
    final String fqClassName = bean.getClass().getName();
    final String className;
    if (fqClassName.indexOf('$') > -1) {
      // drop parent class (i.e. com.foo.Bar$SomeInternalClass should be turned to
      // SomeInternalClass)
      className = fqClassName.substring(fqClassName.lastIndexOf('$') + 1);
    } else {
      className = fqClassName.substring(fqClassName.lastIndexOf('.') + 1);
    }
    final String beanName = Character.toLowerCase(className.charAt(0)) + className.substring(1);
    return fillBean(beanName, bean);
  }
  
  /**
   * Take all objects from the map whose keys begin with <code>prefix</code>, transform them to <code>elementType</code>
   * and return them as a List in the order found in the map.
   * 
   * @param prefix
   *          The required prefix. This is used unchanged and case-sensitively, so "foo" will match both "foobar" and
   *          "foo.bar", but not "Foobar". Not null.
   * @param targetList
   *          The list to fill. New objects will be appended to this list. If this parameter is null, a new List will be
   *          created and returned.
   * @param elementType
   *          The type the objects should be transformed into.
   * @return The list given as <code>targetList</code> or a new one, if <code>targetList</code> was null.
   */
  public List fillList(final String prefix, final List targetList, final Class elementType) {
    initializeFilling();
    final List result = targetList != null ? targetList : new ArrayList();
    final Iterator keys = this.objectMap.filterKeys(prefix, null, null).iterator();
    while (keys.hasNext()) {
      final String key = (String) keys.next();
      if (shouldSkipWithAnnotations(key)) {
        continue; // skip this item
      } else {
        final Object item = this.objectMap.getObject(key, elementType);
        result.add(item);
      }
    }
    
    return result;
  }
  
  /**
   * Check whether there are any annotations for the given key that indicate it should be skipped, i.e. whether it has
   * any applicable ignore or ignore-if-null annotations.
   * 
   * @param key
   *          The key to check. Not null.
   * @return <code>true</code> if annotations indicate that this key should be skipped, <code>false</code> otherwise.
   */
  private boolean shouldSkipWithAnnotations(final String key) {
    final Properties annotation = getAnnotation(key);
    final boolean hasIgnoreAnnotation = annotation.getProperty(AnnotationKeys.IGNORE) != null;
    final boolean hasIgnoreIfNullAnnotation = annotation.getProperty(AnnotationKeys.IGNORE_IF_NULL) != null;
    final boolean hasEmptyAnnotation = annotation.getProperty(AnnotationKeys.INSTANTIATE_EMPTY) != null;
    final boolean hasOverridingAnnotation = hasEmptyAnnotation;
    final boolean itemIsNull = !this.objectMap.isDefinedAt(key);
    return hasIgnoreAnnotation || (hasIgnoreIfNullAnnotation && itemIsNull && !hasOverridingAnnotation)
        || (isIgnoreAllNulls() && itemIsNull && !hasOverridingAnnotation);
  }
  
  /**
   * Take all objects from the map whose keys begin with <code>prefix</code>, transform them to <code>elementType</code>
   * and return them as a Map in the order found in the object map. The respective keys without the prefix are used as
   * keys in the new map. Thus, if given a prefix of "foo." and keys "foo.bar" and "foo.baz", the returned map will
   * contain the keys "bar" and "baz".
   * 
   * @param prefix
   *          The required prefix. This is used unchanged and case-sensitively, so "foo" will match both "foobar" and
   *          "foo.bar", but not "Foobar". Not null.
   * @param targetMap
   *          The map to fill. New objects will be added to this map. If this parameter is null, a new Map will be
   *          created and returned.
   * @param elementType
   *          The type the objects should be transformed into.
   * @return The list given as <code>targetMap</code> or a new one, if <code>targetMap</code> was null.
   */
  public Map fillMap(final String prefix, final Map targetMap, final Class elementType) {
    initializeFilling();
    final Map result = targetMap != null ? targetMap : new HashMap();
    final Iterator keys = this.objectMap.filterKeys(prefix, null, null).iterator();
    while (keys.hasNext()) {
      final String key = (String) keys.next();
      final Object item = this.objectMap.getObject(key, elementType);
      final Properties p = getAnnotation(key);
      if ((p.getProperty(AnnotationKeys.IGNORE) != null)
          || ((p.getProperty(AnnotationKeys.IGNORE_IF_NULL) != null) && (item == null))) {
        continue; // skip this item
      } else {
        final String mapKey = key.substring(prefix.length()); // strip prefix to get just the part
        // we use for the map key
        result.put(mapKey, item);
      }
    }
    
    return result;
  }
  
  /**
   * Add a type to the known set of leaf types. Leaf types are classes that do not have any properties, i.e. they
   * represent leaves in the object graph. This is used mainly for consistency checking of input data. If objects of a
   * leaf type are encountered in the object graph, a {@link LeafInPathException} is thrown.
   * 
   * @param newLeafType
   *          leaf type to add
   * @return <code>this</code>, for method chaining
   */
  public BeanFiller addLeafType(final Class newLeafType) {
    this.knownLeafTypes.add(newLeafType);
    return this;
  }
  
  public StringFilter getKeyFilter() {
    return this.keyFilter;
  }
  
  public void setKeyFilter(final StringFilter keyFilter) {
    this.keyFilter = keyFilter;
  }
}
