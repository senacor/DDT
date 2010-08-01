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

import com.senacor.ddt.typetransformer.NoSuccessfulTransformerException;
import com.senacor.ddt.typetransformer.SpecificTransformer;
import com.senacor.ddt.typetransformer.Transformer;

public class ConstructorTransformerTest extends TestCase {
  private Transformer master;
  
  protected void setUp() throws Exception {
    this.master = Transformer.createPreFilledTransformer();
  }
  
  public void testSingleArgumentConstructor() throws Exception {
    final SpecificTransformer trans = new ConstructorTransformer(":", TestBean.class);
    this.master.addTransformer(trans);
    TestBean bean = null;
    try {
      bean = (TestBean) this.master.transform("0.5", TestBean.class);
    } catch (final Exception e) {
      e.printStackTrace();
      fail("should not have thrown exception!");
    }
    assertEquals(0.5, bean.testDouble, 0.01);
    assertNotNull(bean.testDate); // testDate is filled by default, should not be overwritten in this case
  }
  
  public void testSingleArgumentConstructorWithEmptyDelimiter() throws Exception {
    final SpecificTransformer trans = new ConstructorTransformer("", TestBean.class);
    this.master.addTransformer(trans);
    TestBean bean = null;
    bean = (TestBean) this.master.transform("0.5", TestBean.class);
    assertEquals(0.5, bean.testDouble, 0.01);
    assertNotNull(bean.testDate); // testDate is filled by default, should not be overwritten in this case
  }
  
  public void testSingleArgumentConstructorWithNullDelimiter() throws Exception {
    final SpecificTransformer trans = new ConstructorTransformer(null, TestBean.class);
    this.master.addTransformer(trans);
    TestBean bean = null;
    bean = (TestBean) this.master.transform("0.5", TestBean.class);
    assertEquals(0.5, bean.testDouble, 0.01);
    assertNotNull(bean.testDate); // testDate is filled by default, should not be overwritten in this case
  }
  
  public void testNullArgument() throws Exception {
    final SpecificTransformer trans = new ConstructorTransformer(":", TestBean.class);
    this.master.addTransformer(trans);
    TestBean bean = null;
    try {
      bean = (TestBean) this.master.transform("0.5:", TestBean.class);
    } catch (final Exception e) {
      e.printStackTrace();
      fail("should not have thrown exception!");
    }
    assertEquals(0.5, bean.testDouble, 0.01);
    assertNull(bean.testDate); // testDate must be overwritten by the null parameter
  }
  
  public void testAlternateSingleArgumentConstructor() throws Exception {
    final SpecificTransformer trans = new ConstructorTransformer(":", TestBean.class);
    this.master.addTransformer(trans);
    TestBean bean = null;
    try {
      bean = (TestBean) this.master.transform("1999-12-31", TestBean.class);
    } catch (final Exception e) {
      e.printStackTrace();
      fail("should not have thrown exception!");
    }
    assertEquals(0, bean.testDouble, 0.01);
    assertNotNull(bean.testDate);
    assertEquals(99, bean.testDate.getYear());
    assertEquals(11, bean.testDate.getMonth());
    assertEquals(31, bean.testDate.getDate());
  }
  
  public void testTwoArgumentConstructor() throws Exception {
    final SpecificTransformer trans = new ConstructorTransformer(":", TestBean.class);
    this.master.addTransformer(trans);
    TestBean bean = null;
    try {
      bean = (TestBean) this.master.transform("0.5:1999-12-31", TestBean.class);
    } catch (final Exception e) {
      e.printStackTrace();
      fail("should not have thrown exception!");
    }
    assertEquals(0.5, bean.testDouble, 0.01);
    assertNotNull(bean.testDate);
    assertEquals(99, bean.testDate.getYear());
    assertEquals(11, bean.testDate.getMonth());
    assertEquals(31, bean.testDate.getDate());
  }
  
  public void testThreeBadArguments() throws Exception {
    final SpecificTransformer trans = new ConstructorTransformer(":", TestBean.class);
    this.master.addTransformer(trans);
    try {
      this.master.transform("0.5:1999-12-31:foo", TestBean.class);
      fail("should have thrown exception!");
    } catch (final NoSuccessfulTransformerException e) {
      ; // expected
    }
  }
  
  public void testSubclass() throws Exception {
    final SpecificTransformer trans = new ConstructorTransformer(":", TestBean.class, true);
    this.master.addTransformer(trans);
    try {
      final TestSubBean result = (TestSubBean) this.master.transform("0.5:a", TestSubBean.class);
      assertEquals('a', result.foo);
    } catch (final NoSuccessfulTransformerException e) {
      fail("should not have thrown exception!");
      ; // expected
    }
  }
  
  public static class TestBean {
    public double testDouble;
    
    public Date testDate;
    
    public TestBean(final double testDouble) {
      this(testDouble, new Date());
    }
    
    public TestBean(final double testDouble, final Date testDate) {
      this.testDouble = testDouble;
      this.testDate = testDate;
    }
    
    public TestBean(final Date testDate) {
      this(0, testDate);
    }
  }
  
  public static class TestSubBean extends TestBean {
    
    public char foo;
    
    public TestSubBean(final double testDouble) {
      super(testDouble);
    }
    
    public TestSubBean(final double testDouble, final char foo) {
      super(testDouble);
      this.foo = foo;
    }
    
  }
}
