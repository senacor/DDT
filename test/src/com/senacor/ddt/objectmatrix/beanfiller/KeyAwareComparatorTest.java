/*
 * Copyright (c) 2009 Senacor Technologies AG.
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

import junit.framework.TestCase;

import com.senacor.ddt.objectmatrix.beanfiller.BeanFiller.KeyAwareComparator;

public class KeyAwareComparatorTest extends TestCase {
  private KeyAwareComparator comparator;
  
  protected void setUp() throws Exception {
    this.comparator = new BeanFiller.KeyAwareComparator();
  }
  
  public void testNormalStrings() throws Exception {
    assertEquals(0, this.comparator.compare("foo", "foo"));
    assertTrue(this.comparator.compare("foo", "bar") > 0);
    assertTrue(this.comparator.compare("bar", "foo") < 0);
  }
  
  public void testDottedKeys() throws Exception {
    assertEquals(0, this.comparator.compare("foo.bar", "foo.bar"));
    assertTrue(this.comparator.compare("foo.foo", "foo.bar") > 0);
    assertTrue(this.comparator.compare("foo.bar", "foo.foo") < 0);
    assertTrue(this.comparator.compare("bar.foo", "bar.bar") > 0);
    assertTrue(this.comparator.compare("bar.bar", "bar.foo") < 0);
    assertTrue(this.comparator.compare("foo.foo", "bar.foo") > 0);
    assertTrue(this.comparator.compare("bar.foo", "foo.foo") < 0);
    assertTrue(this.comparator.compare("foo.foo.foo", "foo.foo.bar") > 0);
    assertTrue(this.comparator.compare("foo.foo.bar", "foo.foo.foo") < 0);
  }
  
  public void testBracketedKeys() throws Exception {
    assertEquals(0, this.comparator.compare("foo[bar]", "foo[bar]"));
    assertTrue(this.comparator.compare("foo[foo]", "foo[bar]") > 0);
    assertTrue(this.comparator.compare("foo[bar]", "foo[foo]") < 0);
    assertTrue(this.comparator.compare("foo[foo][foo]", "foo[foo][bar]") > 0);
    assertTrue(this.comparator.compare("foo[foo][bar]", "foo[foo][foo]") < 0);
  }
  
  public void testBracketedAndDottedKeys() throws Exception {
    assertEquals(0, this.comparator.compare("foo[bar].foo", "foo[bar].foo"));
    assertTrue(this.comparator.compare("foo.foo[foo]", "foo.foo[bar]") > 0);
    assertTrue(this.comparator.compare("foo.foo[bar]", "foo.foo[foo]") < 0);
    assertEquals(0, this.comparator.compare("foo.foo", "foo[foo]"));
  }
  
  public void testMixedLengthKeys() throws Exception {
    assertTrue(this.comparator.compare("foo.foo", "foo.foo.foo") < 0);
  }
  
  public void testIntegerKeys() throws Exception {
    assertTrue(this.comparator.compare("foo.foo[10]", "foo.foo[2]") > 0);
    assertTrue(this.comparator.compare("foo.foo[2]", "foo.foo[10]") < 0);
    assertTrue(this.comparator.compare("foo.2", "foo.foo") < 0);
  }
}
