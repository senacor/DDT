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

package com.senacor.ddt.typetransformer;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JakartaConvertUtilsTransformer extends AbstractGuardedTransformer implements SpecificTransformer {
  public static final JakartaConvertUtilsTransformer INSTANCE = new JakartaConvertUtilsTransformer();
  static {
    final Converter longConverter = new LongConverter();
    final Converter doubleConverter = new DoubleConverter();
    final Converter intConverter = new IntegerConverter();
    ConvertUtils.register(intConverter, Integer.class);
    ConvertUtils.register(intConverter, Integer.TYPE);
    ConvertUtils.register(doubleConverter, Double.class);
    ConvertUtils.register(doubleConverter, Double.TYPE);
    ConvertUtils.register(longConverter, Long.class);
    ConvertUtils.register(longConverter, Long.TYPE);
  }
  
  private static final Log log = LogFactory.getLog(JakartaConvertUtilsTransformer.class);
  
  protected boolean canTransform(final Class sourceType, final Class targetType) {
    assert sourceType != null : "sourceType must not be null";
    assert targetType != null : "targetType must not be null";
    return String.class.equals(sourceType);
  }
  
  protected Object doTransform(final Object object, final Class targetType) {
    assert object != null : "object should have been checked for not-null in parent class!";
    assert targetType != null : "targetType should have been checked for not-null in parent class!";
    Object converted;
    try {
      converted = ConvertUtils.convert((String) object, targetType);
    } catch (final ConversionException e) {
      // ConvertUtils failed. If you want to see why, turn on
      // debug logging.
      log.debug("ConvertUtils threw an exception while trying to convert", e);
      return TRY_NEXT;
    }
    if (converted == object) {
      // ConvertUtils gave up and returned the same instance, so we give up too - the chain will
      // continue
      return TRY_NEXT;
    } else {
      return converted;
    }
  }
}
