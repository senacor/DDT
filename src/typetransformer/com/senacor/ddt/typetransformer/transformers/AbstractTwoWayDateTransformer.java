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

import java.util.Calendar;
import java.util.Date;

import com.senacor.ddt.typetransformer.AbstractGuardedTransformer;

public abstract class AbstractTwoWayDateTransformer extends AbstractGuardedTransformer {
  
  protected boolean canTransform(final Class sourceType, final Class targetType) {
    assert sourceType != null : "sourceType must not be null";
    assert targetType != null : "targetType must not be null";
    final boolean oneSideIsString = String.class.equals(sourceType) || String.class.equals(targetType);
    final boolean oneSideIsDate =
        Date.class.equals(targetType) || java.sql.Date.class.equals(targetType) || Date.class.equals(sourceType)
            || java.sql.Date.class.equals(sourceType);
    final boolean oneSideIsCalendar =
        Calendar.class.isAssignableFrom(targetType) || Calendar.class.isAssignableFrom(sourceType);
    return oneSideIsString && (oneSideIsDate || oneSideIsCalendar); // we can convert from string to
    // date or calendar and vice
    // versa;
  }
  
  protected Object doTransform(final Object object, final Class targetType) {
    assert object != null : "object should have been checked for not-null in parent class!";
    assert targetType != null : "targetType should have been checked for not-null in parent class!";
    Object result;
    if (object.getClass().equals(String.class)) {
      result = convertFromString(targetType, (String) object);
    } else if (object instanceof Calendar) {
      result = convertToString(((Calendar) object).getTime());
    } else if (object instanceof Date) {
      result = convertToString((Date) object);
    } else {
      throw new AssertionError(
          "object is neither String nor Date nor Calendar - this should never have made it past the parent method. actual type found: "
              + object.getClass().getName());
    }
    if (result == null) {
      // unable to transform
      return TRY_NEXT;
    } else {
      return result;
    }
  }
  
  protected abstract Object convertToString(Date time);
  
  protected abstract Object convertFromString(Class targetType, String string);
}
