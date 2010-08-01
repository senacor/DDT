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

import java.util.Properties;

import com.senacor.ddt.objectmatrix.beanfiller.strategy.propertyutils.JavaBeanAccessStrategy;
import com.senacor.ddt.typetransformer.Transformer;

/**
 * Interface for abstract bean property access.
 * <p>
 * Any values returned by implementations of this interface may be {@link ObjectHolder}s, i.e. wrappers around the
 * actual value. They should be treated as opaque while interacting with the BeanAccessStrategy. Only retrieve the value
 * from the holder once bean access operations are complete, since the value might be changed by these operations.
 * <p>
 * The default implementation {@link JavaBeanAccessStrategy} deals with normal Java beans. Implement this and pass a
 * matching {@link com.senacor.ddt.test.BeanAccessStrategyFactory factory} to {@link BeanFiller} if you need to handle a
 * custom type of object.
 * 
 * @author Carl-Eric Menzel
 */
public interface BeanAccessStrategy {
  
  /**
   * Return the type of the given property.
   * 
   * @param bean
   *          The object containing the property.
   * @param propertyName
   *          The name of the property.
   * @param annotation
   *          Any applicable annotations.
   * @return The discovered type.
   * @throws PropertyNotFoundException
   *           If the requested property does not exist.
   * @throws PropertyAccessException
   *           If the requested property cannot be accessed for any reason.
   */
  Class getPropertyType(Object bean, String propertyName, Properties annotation) throws PropertyNotFoundException,
      PropertyAccessException;
  
  /**
   * Write a value to an object's property.
   * 
   * @param bean
   *          The object to write to.
   * @param propertyName
   *          The property's name.
   * @param propertyValue
   *          The value to write.
   * @param annotations
   *          Any applicable annotations.
   * @throws PropertyNotFoundException
   *           If the requested property does not exist.
   * @throws PropertyAccessException
   *           If the requested property cannot be accessed for any reason.
   * @return The written value, possibly wrapped by an {@link ObjectHolder}. Use this value for further operations.
   */
  Object writeProperty(Object bean, String propertyName, Object propertyValue, Properties annotations)
      throws PropertyNotFoundException, PropertyAccessException;
  
  /**
   * Instantiate a value of the given type and write it to the named property.
   * 
   * @param bean
   *          The object to write to.
   * @param propertyName
   *          The property's name.
   * @param typeToInstantiate
   *          The type to instantiate.
   * @param annotations
   *          Any applicable annotations.
   * @return The instantiated value, possibly wrapped in an {@link ObjectHolder}.
   * @throws PropertyNotFoundException
   *           If the requested property does not exist.
   * @throws PropertyAccessException
   *           If the requested property cannot be accessed for any reason.
   */
  Object instantiateAndSet(Object bean, String propertyName, Class typeToInstantiate, Properties annotations)
      throws PropertyNotFoundException, PropertyAccessException;
  
  /**
   * Read the named property from the given object.
   * 
   * @param bean
   *          The bean to read from.
   * @param propertyName
   *          The name of the property to read.
   * @param annotations
   *          Any applicable annotations.
   * @return The property value, possibly wrapped in an {@link ObjectHolder}.
   * @throws PropertyNotFoundException
   *           If the requested property does not exist.
   * @throws PropertyAccessException
   *           If the requested property cannot be accessed for any reason.
   */
  Object readProperty(Object bean, String propertyName, Properties annotations) throws PropertyNotFoundException,
      PropertyAccessException;
  
  /**
   * Set the transformer instance to use, if necessary. This should be the same transformer the caller is using.
   * 
   * @param transformer
   *          The transformer.
   */
  void setTransformer(Transformer transformer);
  
  /**
   * Check whether the given object implements the given type.
   * 
   * @param typeToImplement
   *          The type to look for.
   * @param objectToCheck
   *          The object to check.
   * @return <code>true</code>, if the object does implement the type, <code>false</code> otherwise.
   */
  boolean doesObjectImplement(Class typeToImplement, Object objectToCheck);
  
  /**
   * Instantiate an object of the given type.
   * 
   * @param type
   *          The type.
   * @return The new instance, possibly wrapped in an {@link ObjectHolder}.
   */
  Object instantiate(Class type);
  
  public Class getConcreteType(final Class type);
  
  /**
   * Create a holder around the given object. Afterwards, only the holder should be passed around, never the object
   * itself.
   * 
   * @param bean
   *          The bean to wrap.
   * @param annotation
   *          Any applicable annotations.
   * @return The holder containing the object.
   */
  ObjectHolder createHolder(Object bean, Properties annotation);
  
  /**
   * A wrapper containing a reference to an object. This is used to be able to change the underlying reference if
   * necessary. An example would be re-sizing an array while filling a bean: The array can't be resized, so a new one is
   * instantiated and substituted for the original one. Callers keeping only a reference to the holder can safely
   * continue using the holder.
   */
  interface ObjectHolder {
    /**
     * Return the object this holder contains. Do not use this unless you are certain that no further operations might
     * change the underlying reference, leaving you with a stale object.
     * 
     * @return The held object.
     */
    public Object getWrapped();
  }
}