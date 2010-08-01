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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.senacor.ddt.objectmatrix.ObjectMap;
import com.senacor.ddt.objectmatrix.ObjectMatrix;
import com.senacor.ddt.objectmatrix.beanfiller.strategy.propertyutils.TypeMismatchException;
import com.senacor.ddt.objectmatrix.excel.ExcelObjectMatrixFactory;
import com.senacor.ddt.test.util.DateAssert;
import com.senacor.ddt.test.util.NumberAssert;
import com.senacor.ddt.test.util.StringAssert;
import com.senacor.ddt.util.StringFilter;

public class BeanFillerTest extends TestCase {
  public static class PrivateFieldsBean {
    private PrivateFieldsBean bean;
    
    private int integer;
  }
  
  private BeanFiller beanFiller;
  
  private ObjectMap objectMap;
  
  protected void setUp() throws Exception {
    final ExcelObjectMatrixFactory omf =
        new ExcelObjectMatrixFactory(getClass().getResourceAsStream("BeanFillerTest.xls"),
            new String[] { "BeanFillerTest" });
    final ObjectMatrix objectMatrix = omf.create()[0];
    this.objectMap = objectMatrix.getObjectMapForColumn(getName());
    this.beanFiller = new BeanFiller(this.objectMap);
  }
  
  public void testNoValidRow() throws Exception {
    try {
      this.beanFiller.fillBean("this bean does not exist", new Object());
    } catch (final NoPropertyFoundException e) {
      ;// expected
    }
  }
  
  public static class SimpleBean {
    private int integer;
    
    public int getInteger() {
      return this.integer;
    }
    
    public void setInteger(final int integer) {
      this.integer = integer;
    }
    
    private BigDecimal bigDecimal;
    
    public BigDecimal getBigDecimal() {
      return this.bigDecimal;
    }
    
    public void setBigDecimal(final BigDecimal bigDecimal) {
      this.bigDecimal = bigDecimal;
    }
    
  }
  
  public void testSimpleBean() throws Exception {
    final SimpleBean sb = (SimpleBean) this.beanFiller.fillBean("simple", new SimpleBean());
    assertEquals(123, sb.getInteger());
    assertNotNull(sb.getBigDecimal());
    assertEquals("123.456", sb.getBigDecimal().toString());
  }
  
  public static class NestedBean extends SimpleBean {
    private SimpleBean bean;
    
    public SimpleBean getBean() {
      return this.bean;
    }
    
    public void setBean(final SimpleBean bean) {
      this.bean = bean;
    }
  }
  
  public void testNestedBean() throws Exception {
    final NestedBean nb = (NestedBean) this.beanFiller.fillBean("nested", new NestedBean());
    assertEquals(123, nb.getInteger());
    assertEquals(456, nb.getBean().getInteger());
  }
  
  public void testNestedTypedBean() throws Exception {
    final NestedBean nb = (NestedBean) this.beanFiller.fillBean("nestedTyped", new NestedBean());
    assertEquals(123, nb.getInteger());
    assertEquals(456, nb.getBean().getInteger());
    assertEquals(NestedBean.class, nb.getBean().getClass());
  }
  
  public void testNullForPrimitives() throws Exception {
    try {
      this.beanFiller.fillBean("simple", new SimpleBean());
      fail("can't set null to primitive integer, should have thrown exception");
    } catch (final PrimitiveNullException e) {
      StringAssert.assertContains("simple.integer", e.getMessage());
    }
  }
  
  public static class IntArrayBean extends SimpleBean {
    private int[] intArray;
    
    public int[] getIntArray() {
      return this.intArray;
    }
    
    public void setIntArray(final int[] intArray) {
      this.intArray = intArray;
    }
  }
  
  public void testPrimitiveArray() throws Exception {
    final IntArrayBean ab = (IntArrayBean) this.beanFiller.fillBean("arrayBean", new IntArrayBean());
    assertEquals(123, ab.getInteger());
    assertEquals(234, ab.getIntArray()[0]);
    assertEquals(345, ab.getIntArray()[1]);
    assertEquals(0, ab.getIntArray()[2]);
    assertEquals(7, ab.getIntArray().length);
    assertEquals(456, ab.getIntArray()[6]);
  }
  
  public void testPrimitiveNullInPath() throws Exception {
    try {
      this.beanFiller.fillBean("bean", new SimpleBean());
      fail("should have thrown exception");
    } catch (final LeafInPathException e) {
      ; // expected
    }
  }
  
  public void testPrimitiveInPath() throws Exception {
    try {
      this.beanFiller.fillBean("bean", new SimpleBean());
      fail("should have thrown exception");
    } catch (final LeafInPathException e) {
      StringAssert.assertContains("bean.integer.foo", e.getMessage());
    }
  }
  
  public void testInitializedNested() throws Exception {
    final NestedBean nb = new NestedBean();
    final SimpleBean sb = new SimpleBean();
    nb.setBean(sb);
    this.beanFiller.fillBean("nested", nb);
    assertSame("BeanFiller must not overwrite existing bean", sb, nb.getBean());
  }
  
  public void testInizializedNestedTyped() throws Exception {
    NestedBean nb = new NestedBean();
    final SimpleBean sb = new SimpleBean();
    nb.setBean(sb);
    nb = (NestedBean) this.beanFiller.fillBean("nestedTyped", nb);
    assertEquals(123, nb.getInteger());
    assertEquals(456, nb.getBean().getInteger());
    assertEquals(NestedBean.class, nb.getBean().getClass());
  }
  
  public static class ListBean {
    private List list;
    
    public List getList() {
      return this.list;
    }
    
    public void setList(final List list) {
      this.list = list;
    }
  }
  
  public void testList() throws Exception {
    ListBean lb;
    // lb = (ListBean) this.beanFiller.fillBean("listBean", new ListBean());
    // _testList_ValidateListBean(lb);
    
    lb = new ListBean();
    final ArrayList presetList = new ArrayList();
    lb.setList(presetList);
    lb = (ListBean) this.beanFiller.fillBean("listBean", lb);
    _testList_ValidateListBean(lb);
    assertSame(presetList, lb.getList());
  }
  
  public void testMixedTypeList() throws Exception {
    final ListBean lb = (ListBean) this.beanFiller.fillBean("mtlBean", new ListBean());
    assertEquals(SimpleBean.class, lb.getList().get(0).getClass());
    assertEquals(NestedBean.class, lb.getList().get(1).getClass());
    final SimpleBean sb = (SimpleBean) lb.getList().get(0);
    assertEquals(345, sb.getInteger());
    final NestedBean nb = (NestedBean) lb.getList().get(1);
    NumberAssert.assertEquals(new BigDecimal("456"), nb.getBigDecimal());
  }
  
  private void _testList_ValidateListBean(final ListBean lb) {
    assertEquals(SimpleBean.class, lb.getList().get(0).getClass());
    SimpleBean sb = (SimpleBean) lb.getList().get(0);
    assertEquals(321, sb.getInteger());
    sb = (SimpleBean) lb.getList().get(1);
    NumberAssert.assertEquals(new BigDecimal("432"), sb.getBigDecimal());
    assertEquals(17, lb.getList().size());
  }
  
  public static class CollectionBean {
    private Collection coll;
    
    public Collection getColl() {
      return this.coll;
    }
    
    public void setColl(final Collection coll) {
      this.coll = coll;
    }
  }
  
  public void testCollection() throws Exception {
    final CollectionBean cb = (CollectionBean) this.beanFiller.fillBean("collBean", new CollectionBean());
    assertNotNull(cb.getColl());
    final Collection coll = cb.getColl();
    assertEquals(17, coll.size());
    boolean foundInteger = false;
    boolean foundBigDecimal = false;
    final Iterator iterator = coll.iterator();
    while (iterator.hasNext()) {
      final SimpleBean sb = (SimpleBean) iterator.next();
      if (sb != null) {
        if (sb.getInteger() == 321) {
          foundInteger = true;
        } else if (sb.getBigDecimal() != null) {
          NumberAssert.assertEquals(new BigDecimal("432"), sb.getBigDecimal());
          foundBigDecimal = true;
        }
      }
    }
    assertTrue(foundInteger);
    assertTrue(foundBigDecimal);
  }
  
  public void testSet() throws Exception {
    final SetBean sb = (SetBean) this.beanFiller.fillBean("setBean", new SetBean());
    assertNotNull(sb.getSet());
    final Collection coll = sb.getSet();
    assertEquals(2, coll.size());
    boolean foundInteger = false;
    boolean foundBigDecimal = false;
    final Iterator iterator = coll.iterator();
    while (iterator.hasNext()) {
      final SimpleBean bean = (SimpleBean) iterator.next();
      if (bean != null) {
        if (bean.getInteger() == 321) {
          foundInteger = true;
        } else if (bean.getBigDecimal() != null) {
          NumberAssert.assertEquals(new BigDecimal("432"), bean.getBigDecimal());
          foundBigDecimal = true;
        }
      }
    }
    assertTrue(foundInteger);
    assertTrue(foundBigDecimal);
  }
  
  public static class SetBean {
    private Set set;
    
    public Set getSet() {
      return this.set;
    }
    
    public void setSet(final Set set) {
      this.set = set;
    }
  }
  
  public void testIgnoreLine() throws Exception {
    final SimpleBean sb = (SimpleBean) this.beanFiller.fillBean("ignoreBean", new SimpleBean());
    assertEquals(0, sb.getInteger());
    try {
      this.beanFiller.fillBean("allIgnored", new SimpleBean());
      fail("all rows are ~ignored, should have thrown exceptions");
    } catch (final NoPropertyFoundException e) {
      ; // expected
    }
  }
  
  public void testNullPathBlocking() throws Exception {
    NestedBean nb = new NestedBean();
    nb = (NestedBean) this.beanFiller.fillBean("nestedBean", nb);
    assertNull(nb.getBean());
  }
  
  public void testBadTypeHint() throws Exception {
    try {
      this.beanFiller.fillBean("badType", new SimpleBean());
      fail("declared non-assignable type in input file, should have thrown exception");
    } catch (final BeanFillerException e) {
      assertTrue(e.getCause() instanceof TypeMismatchException);
    }
  }
  
  public void testInstantiateRoot() throws Exception {
    try {
      this.beanFiller.createAndFillBean(null);
      fail("should have thrown exception");
    } catch (final IllegalArgumentException e) {
      // expected
    }
    try {
      this.beanFiller.createAndFillBean("nonexistant");
      fail("should have thrown exception");
    } catch (final BeanNotFoundException e) {
      // expected
    }
    final SimpleBean sb = (SimpleBean) this.beanFiller.createAndFillBean("freshSimple");
    assertNotNull(sb);
    assertEquals(123, sb.getInteger());
    assertEquals(NestedBean.class, sb.getClass());
    final Date date = (Date) this.beanFiller.createAndFillBean("freshDate");
    assertNotNull(date);
    final Date expected = new Date(90, 0, 1);
    DateAssert.assertEqualsYearMonthDay(expected, date);
  }
  
  public void testDirectFieldAccess() throws Exception {
    PrivateFieldsBean pfb = new PrivateFieldsBean();
    pfb = (PrivateFieldsBean) this.beanFiller.fillBean("nested", pfb);
    assertEquals(789, pfb.integer);
    assertNotNull(pfb.bean);
    assertEquals(987, pfb.bean.integer);
  }
  
  public void testDefaultValues() throws Exception {
    SimpleBean sb = (SimpleBean) this.beanFiller.fillBean("defaultBean", new SimpleBean());
    assertEquals(123, sb.getInteger());
    NumberAssert.assertEquals(new BigDecimal("123.456"), sb.bigDecimal);
    sb = (SimpleBean) this.beanFiller.fillBean("overridden", new SimpleBean());
    assertEquals(234, sb.integer);
    sb = (SimpleBean) this.beanFiller.fillBean("ignoreBean", new SimpleBean());
    // test combination of ignore-if-null and default-value: default-value should win!
    NumberAssert.assertEquals(new BigDecimal("666"), sb.bigDecimal);
  }
  
  public void testFilters() throws Exception {
    final StringFilter filter = new StringFilter();
    filter.addExcludingFilter("filtered.bean.*");
    this.beanFiller.setKeyFilter(filter);
    final NestedBean nested = (NestedBean) this.beanFiller.fillBean("filtered", new NestedBean());
    assertEquals(123, nested.getInteger());
    assertNull(nested.bean);
  }
  
  public void testMap() throws Exception {
    final MapBean mb = (MapBean) this.beanFiller.fillBean("mapBean", new MapBean());
    assertNotNull(mb.map);
    assertEquals(Integer.valueOf("123"), mb.map.get("anInt"));
    assertEquals(Double.valueOf("234.456"), mb.map.get("aDouble"));
    assertEquals(Integer.valueOf("345"), mb.map.get("aNumber"));
    NumberAssert.assertEquals(new BigDecimal("789"), (BigDecimal) mb.map.get("aBigDec"));
  }
  
  public static class MapBean {
    private Map map;
  }
  
  public void testFillMultiple() throws Exception {
    SimpleBean[] sbs = new SimpleBean[0];
    sbs = (SimpleBean[]) this.beanFiller.fillBean("array", sbs);
    assertEquals(3, sbs.length);
    assertEquals(123, sbs[0].integer);
    assertEquals(234, sbs[1].integer);
    assertEquals(345, sbs[2].integer);
    ArrayList list = new ArrayList();
    list = (ArrayList) this.beanFiller.fillBean("list", list);
    sbs = (SimpleBean[]) list.toArray(new SimpleBean[0]);
    assertEquals(3, list.size());
    assertEquals(123, sbs[0].integer);
    assertEquals(234, sbs[1].integer);
    assertEquals(345, sbs[2].integer);
  }
  
  public static class StringBean {
    private String string;
  }
  
  public void testInstantiateEmpty() throws Exception {
    final StringBean sb = (StringBean) this.beanFiller.fillBean("stringBean", new StringBean());
    assertEquals("", sb.string);
  }
  
  public static class CalendarBean {
    public Calendar cal;
  }
  
  public void testAbstractClassForTransformerTarget() throws Exception {
    final CalendarBean cb = (CalendarBean) this.beanFiller.fillBean("calBean", new CalendarBean());
    assertNotNull(cb.cal);
    DateAssert.assertEqualsYearMonthDay(new Date(107, 0, 1), cb.cal.getTime());
  }
  
  public void testEmptyOverridesIgnoreIfNull() throws Exception {
    final NestedBean nb = new NestedBean();
    assertNull(nb.bean);
    this.beanFiller.fillBean("nestedBeanWithEmpty", nb);
    assertNotNull(nb.bean);
  }
  
  public void testCreatesArray() throws Exception {
    final SimpleBean[] arr = (SimpleBean[]) this.beanFiller.createAndFillBean("beanArray");
    assertEquals(new BigDecimal("1234"), arr[0].bigDecimal);
    assertEquals(new BigDecimal("4321"), arr[1].bigDecimal);
  }
  
  public void testWhitespace() throws Exception {
    final StringBean sb = (StringBean) this.beanFiller.fillBean("whitespace", new StringBean());
    assertEquals("   ", this.objectMap.getString("whitespace.string"));
    assertEquals("   ", sb.string);
  }
  
  public static class AbstractTypeArrayBean {
    public Abstract[] abstracts;
  }
  
  public static abstract class Abstract {
    public String string;
  }
  
  public static class Concrete1 extends Abstract {
    // empty
  }
  
  public static class Concrete2 extends Abstract {
    // empty
  }
  
  public void testAbstractTypeArray() throws Exception {
    final AbstractTypeArrayBean bean =
        (AbstractTypeArrayBean) this.beanFiller.fillBean("abstractArrayBean", new AbstractTypeArrayBean());
    assertEquals(2, bean.abstracts.length);
    assertEquals(Concrete1.class, bean.abstracts[0].getClass());
    assertEquals(Concrete2.class, bean.abstracts[1].getClass());
    assertEquals("foo", bean.abstracts[0].string);
    assertEquals("bar", bean.abstracts[1].string);
  }
}
