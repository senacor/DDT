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

package com.senacor.ddt.typetransformer.transformers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.typetransformer.AbstractGuardedTransformer;
import com.senacor.ddt.typetransformer.SpecificTransformer;

/**
 * {@link SpecificTransformer} that handles string/number conversions. All Java Number types are supported, always using
 * the <code><i>numberType</i>.valueOf(String)</code> method. {@link BigInteger} and {@link BigDecimal} instances are
 * created via their String constructors. If the abstract {@link Number} type is the target type, an {@link Integer}
 * will be created.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class NumberTransformer extends AbstractGuardedTransformer {
  /**
   * Default instance included in all Transformers.
   */
  public static final NumberTransformer INSTANCE = new NumberTransformer();
  
  private static final Set KNOWN_NUMBER_TYPES = new HashSet() {
    {
      add(Integer.class);
      add(Short.class);
      add(Byte.class);
      add(Long.class);
      add(Float.class);
      add(Double.class);
      add(BigInteger.class);
      add(BigDecimal.class);
      add(Number.class);
    }
  };
  
  protected boolean canTransform(final Class sourceType, final Class targetType) {
    return String.class.equals(sourceType) && KNOWN_NUMBER_TYPES.contains(targetType);
  }
  
  private static final Log log = LogFactory.getLog(NumberTransformer.class);
  
  protected Object doTransform(final Object object, final Class targetType) {
    final String string = (String) object;
    if (log.isDebugEnabled()) {
      log.debug("Attempting to transform '" + string + "' to type " + targetType.getName());
    }
    try {
      if (Integer.class.equals(targetType)) {
        return Integer.valueOf(string);
      } else if (Double.class.equals(targetType)) {
        return Double.valueOf(string);
      } else if (BigDecimal.class.equals(targetType)) {
        return new BigDecimal(string);
      } else if (Long.class.equals(targetType)) {
        return Long.valueOf(string);
      } else if (BigInteger.class.equals(targetType)) {
        return new BigInteger(string);
      } else if (Float.class.equals(targetType)) {
        return Float.valueOf(string);
      } else if (Short.class.equals(targetType)) {
        return Short.valueOf(string);
      } else if (Byte.class.equals(targetType)) {
        return Byte.valueOf(string);
      } else if (Number.class.equals(targetType)) {
        return Integer.valueOf(string);
      } else {
        throw new AssertionError("Error while trying to transform '" + string
            + "': Found a Number type I don't know of: " + targetType.getName());
      }
    } catch (final NumberFormatException e) {
      return TRY_NEXT;
    }
  }
  
}
