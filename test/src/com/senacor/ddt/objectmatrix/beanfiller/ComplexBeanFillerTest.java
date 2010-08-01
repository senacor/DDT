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

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.senacor.ddt.objectmatrix.DelegatingObjectMatrixTest;

public class ComplexBeanFillerTest extends DelegatingObjectMatrixTest {
  public void testNoValidRows() {
    final BeanFiller bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 1"));
    try {
      bf.fillBean("nonexistentBean", new TestBean());
      fail("No rows exist for this bean, should have thrown NoPropertyFoundException");
    } catch (final NoPropertyFoundException e) {
      ; // expected
    }
  }
  
  public void testFillList() throws Exception {
    final List list1 =
        new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 1")).fillList("fillList", null, String.class);
    assertEquals(4, list1.size());
    assertEquals("foo", list1.get(0));
    assertEquals("bar", list1.get(1));
    assertEquals("baz", list1.get(2));
    assertEquals("quux", list1.get(3));
    
    final List list2 =
        new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 2")).fillList("fillList", null, String.class);
    assertEquals(4, list2.size());
    assertEquals("foo", list2.get(0));
    assertEquals(null, list2.get(1));
    assertEquals("quux", list2.get(2));
    assertEquals("foobar", list2.get(3));
  }
  
  public void testInvalidProperties() {
    final BeanFiller bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Bean"));
    try {
      bf.fillBean("myBean2", new TestBean());
      fail("Should have thrown exception due to invalid property in the excel sheet");
    } catch (final PropertyNotFoundException e) {
      ; // expected
    }
  }
  
  public void testFillMap() throws Exception {
    final Map map1 =
        new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 1")).fillMap("fillList", null, String.class);
    assertEquals(4, map1.size());
    assertEquals("foo", map1.get("[0]"));
    assertEquals("bar", map1.get("[1]"));
    assertEquals("baz", map1.get("[2]"));
    assertEquals("quux", map1.get("[3]"));
    
    final Map map2 =
        new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 2")).fillMap("fillList", null, String.class);
    assertEquals(4, map2.size());
    assertEquals("foo", map2.get("[0]"));
    assertEquals(null, map2.get("[1]"));
    assertEquals("quux", map2.get("[3]"));
    assertEquals("foobar", map2.get("[4]"));
  }
  
  public void testFillBean() {
    TestBean test = new TestBean();
    test.setInitialized(new TestBean());
    
    final BeanFiller bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Bean"));
    test = (TestBean) bf.fillBean("myBean", test);
    assertEquals("foo", test.getTheString());
    assertEquals(true, test.getTheBoolean());
    assertEquals(123, test.getTheInteger());
    assertEquals(234, test.getTheLong());
    assertEquals(34.5, test.getTheDouble(), 1);
    assertEquals(new BigDecimal("45.6"), test.getBigDecimal());
    assertEquals(new Date(113, 7, 2), test.getDate());
    assertEquals("bar", test.getInitialized().getTheString());
    assertEquals(4711, test.getInitialized().getTheInteger());
    assertEquals("baz", test.getUninitialized().getTheString());
    assertEquals("listfoo", test.getList().get(0));
    assertEquals(new Date(109, 7, 5), test.getList().get(3));
    assertEquals(5, test.getList().size());
    assertNull(test.getList().get(2));
    assertEquals(6, test.getInts().length);
    assertEquals(987, test.getInts()[2]);
    assertEquals(new java.sql.Date(108, 7, 4), test.getSqlDate());
    
    final TestBean test2 = (TestBean) test.getList().get(1);
    assertEquals("baz", test2.getTheString());
    
    final TestBean test3 = (TestBean) test.getTypedList().get(0);
    assertEquals("foobar", test3.getTheString());
    assertEquals(6, test.getTypedList().size());
    assertEquals("FooType", test.getTypedList().get(3));
    assertEquals("brian", test.getUninitialized().getUninitialized().getUninitialized().getTheString());
    assertEquals(new Double(3.4), test.getInitialized().getUninitialized().getUninitialized().getList().get(1));
    
    final Iterator iter = test.getSet().iterator();
    boolean foundFirstInSet = false;
    boolean foundSecondInSet = false;
    while (iter.hasNext()) {
      final TestBean bean = (TestBean) iter.next();
      if ("set1".equals(bean.getTheString())) {
        foundFirstInSet = true;
        assertEquals(1.5, bean.getTheDouble(), 1);
      } else if ("set2".equals(bean.getTheString())) {
        foundSecondInSet = true;
        assertEquals(2.5, bean.getTheDouble(), 1);
      }
    }
    assertTrue(foundFirstInSet);
    assertTrue(foundSecondInSet);
    assertNotNull(test.getCollection());
    assertEquals(1, test.getCollection().size());
    assertEquals("collectionstring", test.getCollection().iterator().next());
    
    assertEquals(test.getMoney().getAmount(), new BigDecimal(100));
    assertEquals(test.getMoney().getCurrency(), "EUR");
  }
  
  public void testIgnoreIfNull() throws Exception {
    BeanFiller bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 1"));
    TestBean tb;
    // explicit null in field
    try {
      tb = (TestBean) bf.fillBean("myBean_ignore-if-null-test", new TestBean());
      fail("all lines are ignored, should have thrown exception");
    } catch (final NoPropertyFoundException e) {
      ; // expected
    }
    
    // empty field
    bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 2"));
    try {
      tb = (TestBean) bf.fillBean("myBean_ignore-if-null-test", new TestBean());
      fail("all lines are ignored, should have thrown exception");
    } catch (final NoPropertyFoundException e) {
      ; // expected
    }
    
    // / filled field
    bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 3"));
    tb = (TestBean) bf.fillBean("myBean_ignore-if-null-test", new TestBean());
    assertNotNull("Row is set to ignore-if-null but field is not null - should have initialized the path!", tb
        .getUninitialized());
    assertEquals("foo", tb.getUninitialized().getTheString());
    
    // null field but no ignore-if-null set
    bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 1"));
    tb = (TestBean) bf.fillBean("myBean_ignore-if-null-test2", new TestBean());
    assertNotNull("Row is not set to ignore-if-null, should have initialized the path!", tb.getUninitialized());
    
    // null field but global ignore-if-null set
    bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Col 1"));
    bf.setIgnoreAllNulls(true);
    try {
      tb = (TestBean) bf.fillBean("myBean_ignore-if-null-test2", new TestBean());
      fail("all lines are ignored, should have thrown exception");
    } catch (final NoPropertyFoundException e) {
      ; // expected
    }
  }
  
  public void testNullBean() {
    final BeanFiller bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Bean"));
    try {
      bf.fillBean("myBean", null);
      fail("Bean is null, should have thrown Exception");
    } catch (final IllegalArgumentException e) {
      ; // expected
    }
  }
  
  public void testNullBeanName() throws Exception {
    final BeanFiller bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Bean"));
    try {
      bf.fillBean(null, new Object());
      fail("bean name is null, should have thrown exception");
    } catch (final IllegalArgumentException e) {
      ; // expected
    }
    try {
      bf.fillBean("  ", new Object());
      fail("bean name is blank, should have thrown exception");
    } catch (final IllegalArgumentException e) {
      ; // expected
    }
  }
  
  public void testImplicitBeanName() {
    final BeanFiller bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Bean"));
    final MyBean implicitlyNamed = (MyBean) bf.fillBean(new MyBean());
    final MyBean explicitlyNamed = (MyBean) bf.fillBean("myBean", new MyBean());
    assertEquals(explicitlyNamed.getTheDouble(), implicitlyNamed.getTheDouble(), 0.001);
    assertEquals(explicitlyNamed.getBigDecimal(), implicitlyNamed.getBigDecimal());
  }
  
  public void testImplicitBeanName_NotFound() throws Exception {
    final BeanFiller bf = new BeanFiller(this.objectMatrix.getObjectMapForColumn("Bean"));
    try {
      bf.fillBean(new TestBean());
      fail("testBean does not exist, should have thrown exception");
    } catch (final NoPropertyFoundException e) {
      ; // expected
    }
  }
  
  public static class MyBean extends TestBean {
    // just for type testing
  }
}
