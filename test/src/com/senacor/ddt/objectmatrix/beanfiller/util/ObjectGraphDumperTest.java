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

package com.senacor.ddt.objectmatrix.beanfiller.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.senacor.ddt.typetransformer.Transformer;

import junit.framework.TestCase;

public class ObjectGraphDumperTest extends TestCase {
  private static final String TEST_STRING = "0xDEADBEEF";
  
  private static final double TEST_DOUBLE = 3.14159;
  
  private static final Date TEST_DATE = new Date();
  
  private static final String TEST_STRING_2 = "FOOBAR";
  
  private static final LeafNode TEST_LEAF = new LeafNode(TEST_STRING);
  
  private ObjectGraphDumper dumper;
  
  protected void setUp() throws Exception {
    dumper = new ObjectGraphDumper();
    dumper.addLeafType(LeafNode.class);
  }
  
  public void testObjectBean() throws Exception {
    ObjectBean ob = new ObjectBean();
    ob.setObject(createSimpleBean());
    Properties expected = new Properties();
    expected.setProperty("myBean.object.theString", TEST_STRING);
    expected.setProperty("myBean.object.leaf", TEST_STRING);
    expected.setProperty("myBean.object.theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.object.date", (String) Transformer.get().transform(TEST_DATE, String.class));
    Properties props = dumper.dump(ob, "myBean");
    assertMatch(expected, props, false);
  }
  
  public void testSimpleObject() throws Exception {
    SimpleBean bean = createSimpleBean();
    Properties expected = new Properties();
    expected.setProperty("myBean.theString", TEST_STRING);
    expected.setProperty("myBean.leaf", TEST_STRING);
    expected.setProperty("myBean.theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.date", (String) Transformer.get().transform(TEST_DATE, String.class));
    Properties props = dumper.dump(bean, "myBean");
    assertMatch(expected, props, false);
  }
  
  public void testTypeHinting() throws Exception {
    SimpleBean bean = createSimpleBean();
    bean.setDate(new java.sql.Date(bean.getDate().getTime()));
    Properties expected = new Properties();
    expected.setProperty("myBean.theString", TEST_STRING);
    expected.setProperty("myBean.leaf", TEST_STRING);
    expected.setProperty("myBean.theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.date~type=", "java.sql.Date");
    expected.setProperty("myBean~type=", bean.getClass().getName());
    Properties props = dumper.dump(bean, "myBean");
    assertMatch(expected, props, true);
  }
  
  public void testFilters() throws Exception {
    dumper.addFilter("myBean.theString");
    SimpleBean bean = createSimpleBean();
    Properties expected = new Properties();
    // expected.setProperty("myBean.theString", TEST_STRING);
    expected.setProperty("myBean.leaf", TEST_STRING);
    expected.setProperty("myBean.theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean~type=", bean.getClass().getName());
    Properties props = dumper.dump(bean, "myBean");
    assertMatch(expected, props, true);
  }
  
  public void testNestedBean() throws Exception {
    SimpleBean simple = createSimpleBean();
    NestedBean nested = new NestedBean();
    nested.setTheString(TEST_STRING_2);
    Properties expected = new Properties();
    expected.setProperty("myBean.bean.theString", TEST_STRING);
    expected.setProperty("myBean.bean.theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.bean.date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.theString", TEST_STRING_2);
    expected.setProperty("myBean.bean.leaf", TEST_STRING);
    nested.setBean(simple);
    Properties props = dumper.dump(nested, "myBean");
    assertMatch(expected, props, false);
  }
  
  public void testListBean() throws Exception {
    SimpleBean simple0 = createSimpleBean();
    SimpleBean simple1 = createSimpleBean();
    simple1.setLeaf(new LeafNode(TEST_STRING_2));
    ListBean listBean = new ListBean();
    listBean.getList().add(simple0);
    listBean.getList().add(simple1);
    listBean.setTheString(TEST_STRING_2);
    Properties expected = new Properties();
    expected.setProperty("myBean.list[0].theString", TEST_STRING);
    expected.setProperty("myBean.list[0].theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.list[0].date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.list[0].leaf", TEST_STRING);
    expected.setProperty("myBean.list[1].theString", TEST_STRING);
    expected.setProperty("myBean.list[1].theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.list[1].date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.list[1].leaf", TEST_STRING_2);
    expected.setProperty("myBean.theString", TEST_STRING_2);
    Properties props = dumper.dump(listBean, "myBean");
    assertMatch(expected, props, false);
  }
  
  public void testSetBean() throws Exception {
    SimpleBean simple0 = createSimpleBean();
    SimpleBean simple1 = createSimpleBean();
    SetBean setBean = new SetBean();
    setBean.getSet().add(simple0);
    setBean.getSet().add(simple1);
    setBean.setTheString(TEST_STRING_2);
    Properties expected = new Properties();
    expected.setProperty("myBean.set[0].theString", TEST_STRING);
    expected.setProperty("myBean.set[0].theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.set[0].date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.set[0].leaf", TEST_STRING);
    expected.setProperty("myBean.set[1].theString", TEST_STRING);
    expected.setProperty("myBean.set[1].theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.set[1].date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.set[1].leaf", TEST_STRING);
    expected.setProperty("myBean.theString", TEST_STRING_2);
    Properties props = dumper.dump(setBean, "myBean");
    assertMatch(expected, props, false);
  }
  
  public void testCollectionBean() throws Exception {
    SimpleBean simple0 = createSimpleBean();
    SimpleBean simple1 = createSimpleBean();
    CollectionBean colBean = new CollectionBean();
    colBean.getCollection().add(simple0);
    colBean.getCollection().add(simple1);
    colBean.setTheString(TEST_STRING_2);
    Properties expected = new Properties();
    expected.setProperty("myBean.collection[0].theString", TEST_STRING);
    expected.setProperty("myBean.collection[0].theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.collection[0].date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.collection[0].leaf", TEST_STRING);
    expected.setProperty("myBean.collection[1].theString", TEST_STRING);
    expected.setProperty("myBean.collection[1].theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.collection[1].date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.collection[1].leaf", TEST_STRING);
    expected.setProperty("myBean.theString", TEST_STRING_2);
    expected.setProperty("myBean.collection~element-type",
        "com.senacor.ddt.objectmatrix.beanfiller.util.ObjectGraphDumperTest$SimpleBean");
    Properties props = dumper.dump(colBean, "myBean");
    assertMatch(expected, props, false);
  }
  
  public void testMixedCollectionBean() throws Exception {
    SimpleBean simple0 = createSimpleBean();
    LeafNode leaf = new LeafNode(TEST_STRING);
    CollectionBean colBean = new CollectionBean();
    colBean.getCollection().add(simple0);
    colBean.getCollection().add(leaf);
    colBean.setTheString(TEST_STRING_2);
    Properties expected = new Properties();
    expected.setProperty("myBean.collection[0].theString", TEST_STRING);
    expected.setProperty("myBean.collection[0].theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.collection[0].date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.collection[0].leaf", TEST_STRING);
    expected.setProperty("myBean.collection[1]", TEST_STRING);
    expected.setProperty("myBean.theString", TEST_STRING_2);
    expected.setProperty("myBean.collection[0]~type",
        "com.senacor.ddt.objectmatrix.beanfiller.util.ObjectGraphDumperTest$SimpleBean");
    expected.setProperty("myBean.collection[1]~type",
        "com.senacor.ddt.objectmatrix.beanfiller.util.ObjectGraphDumperTest$LeafNode");
    Properties props = dumper.dump(colBean, "myBean");
    assertMatch(expected, props, false);
  }
  
  public void _testMapBean() throws Exception {
    fail("nyi");
  }
  
  public void testArrayBean() throws Exception {
    SimpleBean simple0 = createSimpleBean();
    SimpleBean simple1 = createSimpleBean();
    simple1.setTheString(TEST_STRING_2);
    ArrayBean arrBean = new ArrayBean();
    arrBean.setArray(new SimpleBean[] { simple0, simple1 });
    arrBean.setTheString(TEST_STRING_2);
    Properties expected = new Properties();
    expected.setProperty("myBean.array[0].theString", TEST_STRING);
    expected.setProperty("myBean.array[0].theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.array[0].date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.array[0].leaf", TEST_STRING);
    expected.setProperty("myBean.array[1].theString", TEST_STRING_2);
    expected.setProperty("myBean.array[1].theDouble", "" + TEST_DOUBLE);
    expected.setProperty("myBean.array[1].date", (String) Transformer.get().transform(TEST_DATE, String.class));
    expected.setProperty("myBean.array[1].leaf", TEST_STRING);
    expected.setProperty("myBean.theString", TEST_STRING_2);
    Properties props = dumper.dump(arrBean, "myBean");
    assertMatch(expected, props, false);
  }
  
  private SimpleBean createSimpleBean() {
    SimpleBean bean = new SimpleBean();
    bean.setTheString(TEST_STRING);
    bean.setTheDouble(TEST_DOUBLE);
    bean.setDate(TEST_DATE);
    bean.setLeaf(TEST_LEAF);
    return bean;
  }
  
  private void assertMatch(Properties expected, Properties actual, boolean exclusive) {
    Iterator iter = expected.keySet().iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      assertEquals("Property did not match: " + key, expected.getProperty(key), actual.getProperty(key));
    }
    if (exclusive) {
      assertEquals("expected and actual are not of the same size", expected.size(), actual.size());
    }
  }
  
  public static class SimpleBean {
    SimpleBean() {
      // nothing to be done
    }
    
    private String theString;
    
    private double theDouble;
    
    private Date date;
    
    private LeafNode leaf;
    
    public LeafNode getLeaf() {
      return leaf;
    }
    
    public void setLeaf(LeafNode leaf) {
      this.leaf = leaf;
    }
    
    public Date getDate() {
      return date;
    }
    
    public void setDate(Date date) {
      this.date = date;
    }
    
    public double getTheDouble() {
      return theDouble;
    }
    
    public void setTheDouble(double theDouble) {
      this.theDouble = theDouble;
    }
    
    public String getTheString() {
      return theString;
    }
    
    public void setTheString(String theString) {
      this.theString = theString;
    }
  }
  
  public static class NestedBean extends SimpleBean {
    NestedBean() {
      // nothing to be done
    }
    
    private SimpleBean bean;
    
    public SimpleBean getBean() {
      return bean;
    }
    
    public void setBean(SimpleBean bean) {
      this.bean = bean;
    }
  }
  
  public static class LeafNode {
    private String value;
    
    public LeafNode(String value) {
      this.value = value;
    }
    
    public String toString() {
      return value;
    }
  }
  
  public static class ListBean extends SimpleBean {
    private List list = new ArrayList();
    
    public List getList() {
      return list;
    }
    
    public void setList(List list) {
      this.list = list;
    }
  }
  
  public static class SetBean extends SimpleBean {
    private Set set = new HashSet();
    
    public Set getSet() {
      return set;
    }
    
    public void setSet(Set set) {
      this.set = set;
    }
  }
  
  public static class CollectionBean extends SimpleBean {
    private Collection collection = new ArrayList();
    
    public Collection getCollection() {
      return collection;
    }
    
    public void setCollection(Collection collection) {
      this.collection = collection;
    }
  }
  
  public static class ObjectBean {
    private Object object;
    
    public Object getObject() {
      return object;
    }
    
    public void setObject(Object object) {
      this.object = object;
    }
  }
  
  public static class ArrayBean extends SimpleBean {
    private SimpleBean[] array;
    
    public SimpleBean[] getArray() {
      return array;
    }
    
    public void setArray(SimpleBean[] array) {
      this.array = array;
    }
  }
}
