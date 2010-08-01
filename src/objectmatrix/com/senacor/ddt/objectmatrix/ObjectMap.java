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

package com.senacor.ddt.objectmatrix;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.senacor.ddt.typetransformer.Transformer;

/**
 * A map of objects indexed by String keys. This is a one-dimensional variation of {@link ObjectMatrix}, used mostly to
 * provide a view onto a single row or column of a matrix. The methods mirror those in {@linkplain ObjectMatrix}.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public interface ObjectMap {
  /**
   * @return An unmodifiable list of the keys contained in this map.
   */
  List getKeys();
  
  /**
   * Get a typed object at the given position in the map.
   * 
   * @param key
   *          The key. Must not be blank.
   * @param type
   *          The desired type. Must not be null.
   * @return An object of type <code>type</code> or <code>null</code>.
   * @see ObjectMatrix#getObject(String, String, Class)
   */
  Object getObject(String key, Class type);
  
  /**
   * Get a boolean.
   * 
   * @param key
   *          The key. Must not be blank.
   * @return A Boolean or null.
   * @see ObjectMatrix#getBoolean(String, String)
   */
  Boolean getBoolean(String key);
  
  /**
   * Get an integer.
   * 
   * @param key
   *          The key. Must not be blank.
   * @return A Integer or null.
   * @see ObjectMatrix#getInteger(String, String)
   */
  Integer getInteger(String key);
  
  /**
   * Get a Long.
   * 
   * @param key
   *          The key. Must not be blank.
   * @return A Long or null.
   * @see ObjectMatrix#getLong(String, String)
   */
  Long getLong(String key);
  
  /**
   * Get a Double.
   * 
   * @param key
   *          The key. Must not be blank.
   * @return A Double or null.
   * @see ObjectMatrix#getDouble(String, String)
   */
  Double getDouble(String key);
  
  /**
   * Get a Date. Just like its counterpart in {@link ObjectMatrix}, this method will not get you a life, though.
   * 
   * @param key
   *          The key. Must not be blank.
   * @return A Date or null.
   * @see ObjectMatrix#getDate(String, String)
   */
  Date getDate(String key);
  
  /**
   * Get a String.
   * 
   * @param key
   *          The key. Must not be blank.
   * @return A String or null, if and only if the underlying field is annotated with
   *         {@link ObjectMatrix.AnnotationKeys#NULL}.
   * @see ObjectMatrix#getString(String, String)
   */
  String getString(String key);
  
  /**
   * Get a BigDecimal.
   * 
   * @param key
   *          The key. Must not be blank.
   * @return A BigDecimal or null.
   * @see ObjectMatrix#getBigDecimal(String, String)
   */
  BigDecimal getBigDecimal(String key);
  
  /**
   * Filter the row names and return the results. This should be a simple prefix, infix, postfix matching.
   * 
   * @param prefix
   * @param infix
   * @param postfix
   * @return A list of matched keys.
   * @see StringMatrix#filterColumnNames(String, String, String)
   * @see StringMatrix#filterRowNames(String, String, String)
   */
  List filterKeys(String prefix, String infix, String postfix);
  
  /**
   * Get the annotations for the given key.
   * 
   * @param key
   *          The key. Must not be blank.
   * @return A Properties object. May be empty, but not null.
   * @see AnnotatedStringMatrix#getAnnotation(String, String)
   */
  Properties getAnnotation(String key);
  
  /**
   * Get the map's identifier.
   * 
   * @return An opaque identifier
   * @see StringMatrix#getMatrixIdentifier()
   */
  String getIdentifier();
  
  /**
   * Get the transformer used by this ObjectMap.
   * 
   * @return A Transformer instance. If this ObjectMap does not have a transformer, it must return the result of
   *         <code>Transformer#get()</code>, never null.
   * @see ObjectMatrix#getTransformer()
   */
  Transformer getTransformer();
  
  /**
   * Check whether a value is defined for this key. This method checks only untransformed content and the default-value
   * annotation, not the results of any transformations. Usually, this would mean that an empty cell in the underlying
   * table, without a default-value annotation, makes this method return false.
   * 
   * @param key
   *          The key to check.
   * @return <ul>
   *         <li><code>true</code> if there is a value available for this key</li>
   *         <li><code>false</code> otherwise.</li>
   *         </ul>
   */
  boolean isDefinedAt(String key);
}
