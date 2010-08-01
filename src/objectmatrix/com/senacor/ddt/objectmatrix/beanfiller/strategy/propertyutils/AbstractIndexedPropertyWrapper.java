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

import com.senacor.ddt.objectmatrix.beanfiller.BadIndexException;
import com.senacor.ddt.typetransformer.Transformer;

/**
 * Base wrapper for objects whose properties are indexed with integers, e.g. lists and arrays.
 * 
 * @author Carl-Eric Menzel
 */
public abstract class AbstractIndexedPropertyWrapper extends AbstractMultiValuedBeanWrapper {
  
  /**
   * @param transformer
   *          A transformer instance to use if necessary. Not null.
   */
  public AbstractIndexedPropertyWrapper(final Transformer transformer) {
    super(transformer);
  }
  
  public AbstractIndexedPropertyWrapper(final Transformer transformer, final Properties annotation) {
    super(transformer, annotation);
  }
  
  protected int parseIndex(final String propertyName) {
    final String index = chopBracketsIfNecessary(propertyName);
    try {
      return Integer.parseInt(index);
    } catch (final NumberFormatException e) {
      throw new BadIndexException("can't parse index '" + propertyName + "'", e);
    }
  }
}
