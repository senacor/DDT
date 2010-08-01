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

import java.util.HashSet;
import java.util.Set;

import com.senacor.ddt.typetransformer.AbstractGuardedTransformer;
import com.senacor.ddt.util.ParamChecker;

public class BooleanTransformer extends AbstractGuardedTransformer {
  public static final BooleanTransformer INSTANCE = new BooleanTransformer();
  
  private static final Set trueStrings = new HashSet() {
    {
      add("true");
      add("ja");
      add("yes");
      add("wahr");
      add("1");
      add("j");
      add("y");
    }
  };
  
  protected boolean canTransform(final Class sourceType, final Class targetType) {
    final boolean oneIsString = String.class.isAssignableFrom(sourceType) || String.class.isAssignableFrom(targetType);
    final boolean oneIsBoolean =
        Boolean.class.isAssignableFrom(sourceType) || Boolean.class.isAssignableFrom(targetType);
    return oneIsBoolean && oneIsString;
  }
  
  protected Object doTransform(final Object object, final Class targetType) {
    if (String.class.isAssignableFrom(targetType)) {
      return object.toString();
    } else {
      final String value = (String) object;
      if (trueStrings.contains(value.toLowerCase())) {
        return Boolean.TRUE;
      } else {
        return Boolean.FALSE;
      }
    }
  }
  
  public BooleanTransformer addTrueValue(final String newTrueValue) {
    ParamChecker.notBlank("newTrueValue", newTrueValue);
    trueStrings.add(newTrueValue.toLowerCase());
    return this;
  }
}
