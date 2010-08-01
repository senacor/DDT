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

import java.lang.reflect.Field;

import com.senacor.ddt.objectmatrix.beanfiller.PrivateFields.MoreFields;

import junit.framework.TestCase;

public class FieldAccessTest extends TestCase {
  public void testFieldInClass() throws Exception {
    PrivateFields bean = new PrivateFields();
    assertEquals(0, bean.getFoo());
    Field field = getAccessibleField(PrivateFields.class, "foo");
    field.set(bean, new Integer(42));
    assertEquals(42, bean.getFoo());
  }
  
  public void testFieldInSuperClass() throws Exception {
    MoreFields bean = new MoreFields();
    assertEquals(0, bean.getFoo());
    Field field = getAccessibleField(MoreFields.class, "foo");
    field.set(bean, new Integer(42));
    assertEquals(42, bean.getFoo());
  }
  
  private Field getAccessibleField(Class type, String fieldName) {
    Field field = findField(fieldName, type.getFields());
    while ((field == null) && !type.equals(Object.class)) {
      field = findField(fieldName, type.getDeclaredFields());
      type = type.getSuperclass();
    }
    return field;
  }
  
  private Field findField(String fieldName, Field[] fields) {
    for (int i = 0; i < fields.length; i++) {
      if (fields[i].getName().equals(fieldName)) {
        if (!fields[i].isAccessible()) {
          fields[i].setAccessible(true);
        }
        return fields[i];
      }
    }
    return null;
  }
}
