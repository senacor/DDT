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

import junit.framework.TestCase;

import com.senacor.ddt.typetransformer.SpecificTransformer;

public class BooleanTransformerTest extends TestCase {
  private BooleanTransformer transformer;
  
  protected void setUp() throws Exception {
    this.transformer = new BooleanTransformer();
  }
  
  public void testTrueValues() throws Exception {
    assertTrue("ja");
    assertTrue("yes");
    assertTrue("1");
    assertTrue("true");
    assertTrue("wahr");
    assertTrue("y");
    assertTrue("j");
    assertTrue("Ja");
    assertTrue("yES");
    assertTrue("1");
    assertTrue("truE");
    assertTrue("wAhr");
    assertTrue("Y");
    assertTrue("J");
  }
  
  public void testFalseValues() throws Exception {
    assertFalse("f");
    assertFalse("bar");
  }
  
  public void testWrongType() throws Exception {
    assertEquals(SpecificTransformer.TRY_NEXT, this.transformer.transform("foo", TestCase.class));
    assertEquals(SpecificTransformer.TRY_NEXT, this.transformer.transform(new Object(), Boolean.class));
  }
  
  public void testAddValue() throws Exception {
    this.transformer.addTrueValue("fooBar");
    assertTrue("foobar");
  }
  
  private void assertFalse(final String string) {
    assertEquals(Boolean.FALSE, this.transformer.transform(string, Boolean.class));
  }
  
  private void assertTrue(final String string) {
    assertEquals(Boolean.TRUE, this.transformer.transform(string, Boolean.class));
  }
}
