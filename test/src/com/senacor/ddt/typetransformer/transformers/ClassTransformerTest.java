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

import java.util.Date;

import junit.framework.TestCase;

import com.senacor.ddt.test.util.GenericAssert;
import com.senacor.ddt.typetransformer.SpecificTransformer;

public class ClassTransformerTest extends TestCase {
  public void testClassToString() throws Exception {
    assertEquals("java.lang.String", new ClassTransformer().transform(String.class, String.class));
  }
  
  public void testStringToClass() throws Exception {
    assertEquals(String.class, new ClassTransformer().transform("java.lang.String", Class.class));
  }
  
  public void testFailure() throws Exception {
    assertEquals(SpecificTransformer.TRY_NEXT, new ClassTransformer().transform("foo", Class.class));
  }
  
  public void testRejectTypes() throws Exception {
    assertEquals(SpecificTransformer.TRY_NEXT, new ClassTransformer().transform("foo", Date.class));
    assertEquals(SpecificTransformer.TRY_NEXT, new ClassTransformer().transform(new Date(), String.class));
  }
  
  public void testOnedimensionalArrays() throws Exception {
    final Class expected = String[].class;
    final Object actual = new ClassTransformer().transform("java.lang.String[]", Class.class);
    assertEquals(expected, actual);
  }
  
  public void testMultidimensionalArrays() throws Exception {
    final Class expected = String[][].class;
    final Object actual = new ClassTransformer().transform("java.lang.String[][]", Class.class);
    assertEquals(expected, actual);
    final Class expected2 = String[][][].class;
    final Object actual2 = new ClassTransformer().transform("java.lang.String[][][]", Class.class);
    assertEquals(expected2, actual2);
    GenericAssert.assertNotEquals(actual, actual2);
  }
}
