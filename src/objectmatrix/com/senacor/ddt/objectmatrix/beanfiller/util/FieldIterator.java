/*
 * Copyright (c) 2008 Senacor Technologies AG.
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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.senacor.ddt.util.ParamChecker;

public class FieldIterator {
  protected static abstract class Fields {
    public abstract void process(Field field);
  }
  
  private final Class type;
  private boolean withSuper = true;
  
  private FieldIterator(final Class type) {
    ParamChecker.notNull("type", type);
    ParamChecker.require("type must not be primitive", !type.isPrimitive());
    ParamChecker.require("type must not be Object", !type.equals(Object.class));
    this.type = type;
  }
  
  public static FieldIterator with(final Class type) {
    return new FieldIterator(type);
  }
  
  public FieldIterator withoutSuper() {
    final FieldIterator copy = copy();
    copy.withSuper = false;
    return copy;
  }
  
  private FieldIterator copy() {
    final FieldIterator copy = new FieldIterator(this.type);
    copy.withSuper = this.withSuper;
    return copy;
  }
  
  public void forAll(final Fields fields) {
    for (final Iterator iterator = getFields().iterator(); iterator.hasNext();) {
      final Field field = (Field) iterator.next();
      fields.process(field);
    }
  }
  
  private Set getFields() {
    final Set result = new HashSet();
    Class currentType = this.type;
    while (!currentType.equals(Object.class)) {
      result.addAll(getFields(currentType));
      if (this.withSuper) {
        currentType = currentType.getSuperclass();
      } else {
        break;
      }
    }
    return result;
  }
  
  private Collection getFields(final Class type) {
    return Arrays.asList(type.getDeclaredFields());
  }
}
