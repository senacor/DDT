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

import java.lang.reflect.Array;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.typetransformer.AbstractGuardedTransformer;

public class ClassTransformer extends AbstractGuardedTransformer {
  private static final Log log = LogFactory.getLog(ClassTransformer.class);
  public static final ClassTransformer INSTANCE = new ClassTransformer();
  
  protected boolean canTransform(final Class sourceType, final Class targetType) {
    return isStringToClass(sourceType, targetType) || isClasstoString(sourceType, targetType);
  }
  
  private boolean isStringToClass(final Class sourceType, final Class targetType) {
    return String.class.equals(sourceType) && Class.class.equals(targetType);
  }
  
  private boolean isClasstoString(final Class sourceType, final Class targetType) {
    return Class.class.equals(sourceType) && String.class.equals(targetType);
  }
  
  protected Object doTransform(final Object object, final Class targetType) {
    if (isStringToClass(object.getClass(), targetType)) {
      final Class result;
      try {
        final String className = (String) object;
        if (className.endsWith("[]")) {
          final String elementClassName = className.substring(0, className.length() - 2);
          final Class elementClass;
          {
            final Object attempt = doTransform(elementClassName, Class.class);
            if (attempt == TRY_NEXT) {
              return TRY_NEXT;
            } else {
              elementClass = (Class) attempt;
            }
          }
          final Object tempArray = Array.newInstance(elementClass, 0);
          result = tempArray.getClass();
        } else {
          result = Class.forName(className);
        }
      } catch (final ClassNotFoundException e) {
        log.debug("Cannot transform string to class", e);
        return TRY_NEXT;
      }
      return result;
    } else {
      final Class theClass = (Class) object;
      return theClass.getName();
    }
  }
  
}
