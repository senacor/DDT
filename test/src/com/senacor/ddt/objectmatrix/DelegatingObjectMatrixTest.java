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
package com.senacor.ddt.objectmatrix;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.senacor.ddt.objectmatrix.excel.ExcelObjectMatrixFactory;
import com.senacor.ddt.objectmatrix.excel.ExcelStringMatrixTest;
import com.senacor.ddt.typetransformer.AbstractGuardedTransformer;
import com.senacor.ddt.typetransformer.AbstractTransformer;
import com.senacor.ddt.typetransformer.NoSuccessfulTransformerException;
import com.senacor.ddt.typetransformer.SpecificTransformer;
import com.senacor.ddt.typetransformer.TransformationFailedException;
import com.senacor.ddt.typetransformer.Transformer;

/**
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class DelegatingObjectMatrixTest extends ExcelStringMatrixTest {
  protected ObjectMatrix objectMatrix;
  
  protected void setUp() throws Exception {
    super.setUp();
    this.objectMatrix = new DelegatingObjectMatrix(new EmbeddedAnnotationMatrixDecorator(this.stringMatrix));
    this.stringMatrix = this.objectMatrix;
  }
  
  protected void tearDown() throws Exception {
    super.tearDown();
    this.objectMatrix = null;
    this.stringMatrix = null;
  }
  
  public void testReference() {
    assertEquals("Bar", this.objectMatrix.getString("Col 3", "Row 1"));
    assertEquals("Baz2", this.objectMatrix.getString("Col 3", "Row 2"));
    assertEquals("Bar", this.objectMatrix.getString("Col 4", "Row 1"));
  }
  
  public void testExcelReference() {
    assertEquals(new Date(105, 0, 1), this.objectMatrix.getDate("Col 1", "excelReference"));
  }
  
  public void testNullToken() {
    assertNull(this.objectMatrix.getString("Col 4", "Row 2"));
  }
  
  public void testTransformerFallbackOnNumberParsing() throws Exception {
    Transformer.get().addTransformer(new AbstractGuardedTransformer() {
      
      protected boolean canTransform(final Class sourceType, final Class targetType) {
        return sourceType.equals(String.class) && targetType.equals(Long.class);
      }
      
      protected Object doTransform(final Object object, final Class targetType) {
        if ("true".equals(object)) {
          return new Long(42);
        } else {
          return TRY_NEXT;
        }
      }
    });
    
    Transformer.get().addTransformer(new AbstractGuardedTransformer() {
      
      protected boolean canTransform(final Class sourceType, final Class targetType) {
        return sourceType.equals(String.class) && targetType.equals(Double.class);
      }
      
      protected Object doTransform(final Object object, final Class targetType) {
        if ("true".equals(object)) {
          return new Double(42.5);
        } else {
          return TRY_NEXT;
        }
      }
    });
    
    Transformer.get().addTransformer(new AbstractGuardedTransformer() {
      
      protected boolean canTransform(final Class sourceType, final Class targetType) {
        return sourceType.equals(String.class) && targetType.equals(Integer.class);
      }
      
      protected Object doTransform(final Object object, final Class targetType) {
        if ("true".equals(object)) {
          return new Integer(42);
        } else {
          return TRY_NEXT;
        }
      }
    });
    assertEquals("Registered String-To-Number transformer, should convert correctly", new Long(42), this.objectMatrix
        .getLong("Col 1", "object"));
    assertEquals("Registered String-To-Number transformer, should convert correctly", new Double(42.5),
        this.objectMatrix.getDouble("Col 1", "object"));
    assertEquals("Registered String-To-Number transformer, should convert correctly", new Integer(42),
        this.objectMatrix.getInteger("Col 1", "object"));
  }
  
  public void testNumberHandling() {
    assertEquals(new Long(123456789012345L), this.objectMatrix.getLong("Col 1", "largeNumber"));
    assertEquals("123456789012345", this.objectMatrix.getString("Col 1", "largeNumber"));
    assertEquals(new Double(1234567.89012345), this.objectMatrix.getDouble("Col 1", "largeFloat"));
    assertEquals("1234567.89012345", this.objectMatrix.getString("Col 1", "largeFloat"));
  }
  
  public void testFormulaHandling() {
    assertEquals(new Integer(84), this.objectMatrix.getInteger("Col 1", "sumFormula"));
    assertEquals(new Double(84.5), this.objectMatrix.getDouble("Col 1", "sumFormulaDouble"));
  }
  
  public void testLocalTransformer() throws Exception {
    final Transformer transformer = new Transformer();
    final StringBuffer testString = new StringBuffer("DEADBEEF");
    transformer.addTransformer(new ObjectReturningTransformer(testString));
    assertEquals(testString, transformer.transform("foo", StringBuffer.class)); // just
    // for
    // consistency
    final ObjectMatrix matrix =
        new DelegatingObjectMatrix(new EmbeddedAnnotationMatrixDecorator(this.stringMatrix), transformer);
    assertEquals(testString, matrix.getObject("Col 1", "boolean", StringBuffer.class));
  }
  
  public void testLocalTransformerWithFactory() throws Exception {
    final Transformer transformer = new Transformer();
    final StringBuffer testString = new StringBuffer("DEADBEEF");
    transformer.addTransformer(new ObjectReturningTransformer(testString));
    assertEquals(testString, transformer.transform("foo", StringBuffer.class)); // just
    // for
    // consistency
    final ExcelObjectMatrixFactory factory =
        new ExcelObjectMatrixFactory(getClass().getClassLoader().getResourceAsStream("test.xls"),
            new String[] { "Test" });
    factory.setLocalTransformer(transformer);
    final ObjectMatrix matrix = factory.create()[0];
    assertEquals(testString, matrix.getObject("Col 1", "boolean", StringBuffer.class));
  }
  
  public void testObjects() {
    // booleans
    assertEquals(Boolean.TRUE, this.objectMatrix.getBoolean("Col 1", "boolean"));
    assertEquals(Boolean.TRUE, this.objectMatrix.getBoolean("Col 2", "boolean"));
    assertEquals(Boolean.TRUE, this.objectMatrix.getBoolean("Col 3", "boolean"));
    assertEquals(Boolean.FALSE, this.objectMatrix.getBoolean("Col 4", "boolean"));
    assertNull(this.objectMatrix.getBoolean("Col 5", "boolean"));
    assertNull(this.objectMatrix.getBoolean("Col 6", "boolean"));
    
    // integers
    assertEquals(new Integer(42), this.objectMatrix.getInteger("Col 1", "integer"));
    assertEquals(new Integer(-42), this.objectMatrix.getInteger("Col 2", "integer"));
    assertNull(this.objectMatrix.getInteger("Col 3", "integer"));
    assertNull(this.objectMatrix.getInteger("Col 4", "integer"));
    try {
      this.objectMatrix.getInteger("Col 1", "double");
      fail("should have failed");
    } catch (final NoSuccessfulTransformerException e) {
      assertTrue(e.getMessage().indexOf("Col 1") > -1);
      assertTrue(e.getMessage().indexOf("double") > -1);
    }
    
    // longs
    assertEquals(new Long(42), this.objectMatrix.getLong("Col 1", "long"));
    assertEquals(new Long(-42), this.objectMatrix.getLong("Col 2", "long"));
    assertNull(this.objectMatrix.getLong("Col 3", "long"));
    assertNull(this.objectMatrix.getLong("Col 4", "long"));
    try {
      this.objectMatrix.getLong("Col 1", "double");
      fail("should have failed");
    } catch (final NoSuccessfulTransformerException e) {
      assertTrue(e.getMessage().indexOf("Col 1") > -1);
      assertTrue(e.getMessage().indexOf("double") > -1);
    }
    
    // doubles
    assertEquals(new Double(-42.5), this.objectMatrix.getDouble("Col 2", "double"));
    assertNull(this.objectMatrix.getDouble("Col 3", "double"));
    assertNull(this.objectMatrix.getDouble("Col 4", "double"));
    assertEquals(new Double(100000.5), this.objectMatrix.getDouble("Col 5", "double"));
    
    // bigdecimals
    assertTrue(new BigDecimal("42.5").compareTo(this.objectMatrix.getBigDecimal("Col 1", "bigdecimal")) == 0);
    assertTrue(new BigDecimal("-42.5").compareTo(this.objectMatrix.getBigDecimal("Col 2", "bigdecimal")) == 0);
    assertNull(this.objectMatrix.getBigDecimal("Col 3", "bigdecimal"));
    assertNull(this.objectMatrix.getBigDecimal("Col 4", "bigdecimal"));
    
    // dates
    final Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(2005, 0, 1);
    assertEquals(cal.getTime(), this.objectMatrix.getDate("Col 1", "date"));
    assertNull(this.objectMatrix.getDate("Col 2", "date"));
    assertNull(this.objectMatrix.getDate("Col 3", "date"));
    
    // arbitrary objects
    assertEquals(Boolean.TRUE, this.objectMatrix.getObject("Col 1", "object", Boolean.class));
    assertNull(this.objectMatrix.getObject("Col 2", "object", Boolean.class));
  }
  
  public void testFilterRowNames() throws Exception {
    testRowFilterWithFilteredAnnotations(this.stringMatrix);
  }
  
  public void testDefaultValue() throws Exception {
    assertEquals("default", this.objectMatrix.getString("Col 1", "needSomeDefault"));
    assertEquals("non-default", this.objectMatrix.getString("Col 2", "needSomeDefault"));
    assertNull(this.objectMatrix.getString("Col 3", "needSomeDefault"));
  }
  
  public static void testRowFilterWithFilteredAnnotations(final StringMatrix stringMatrix) {
    List result = stringMatrix.filterRowNames("myBean", null, null);
    assertEquals(39, result.size()); // less than in
    // ExcelStringMatrixTest because
    // DOMatrix uses
    // the filtering
    // AnnotatedStringMatrix
    result = stringMatrix.filterRowNames(null, "Formula", null);
    assertEquals(2, result.size());
    result = stringMatrix.filterRowNames(null, null, "theString");
    dumpList(result);
    // one more than in ESMT, because DOMatrix filters the annotations, so
    // both //
    // ignore-if-null-test lines match
    assertEquals(11, result.size());
    result = stringMatrix.filterRowNames(null, "uninitialized", "theString");
    // same reason
    assertEquals(4, result.size());
    result = stringMatrix.filterRowNames("foo", "uninitialized", "theString");
    assertEquals(0, result.size());
  }
  
  private class ObjectReturningTransformer extends AbstractTransformer {
    private final Object object;
    
    ObjectReturningTransformer(final Object obj) {
      this.object = obj;
    }
    
    public Object transform(final Object o, final Class targetType) {
      if (targetType.isAssignableFrom(this.object.getClass())) {
        return this.object;
      } else {
        return TRY_NEXT;
      }
    }
  }
  
  public static class Money {
    
    private final BigDecimal amount;
    private String currency;
    
    public Money(final BigDecimal b) {
      this.amount = b;
    }
    
    static {
      Transformer.get().addTransformer(new AbstractGuardedTransformer() {
        
        protected boolean canTransform(final Class sourceType, final Class targetType) {
          return Money.class.isAssignableFrom(targetType);
        }
        
        protected Object doTransform(final Object object, final Class targetType) {
          return new Money(new BigDecimal(object.toString()));
        }
        
      });
    }
    
    public String getCurrency() {
      return this.currency;
    }
    
    public void setCurrency(final String currency) {
      this.currency = currency;
    }
    
    public BigDecimal getAmount() {
      return this.amount;
    }
  }
  
  public static class TestBean {
    private List list = new ArrayList();
    
    private List typedList = new ArrayList();
    
    private int[] ints;
    
    private String theString;
    
    private boolean theBoolean;
    
    private int theInteger;
    
    private long theLong;
    
    private double theDouble;
    
    private BigDecimal bigDecimal;
    
    private Date date;
    
    private java.sql.Date sqlDate;
    
    private TestBean initialized = null;
    
    private TestBean uninitialized = null;
    
    private Money money;
    
    private Set Set = new HashSet();
    
    private Collection collection = new HashSet();
    
    /*
     * just to test the not-walking-down-null-graph feature - this is set to null in the excel file, but it must not
     * cause all the other properties starting with "the" to // be ignored.
     */
    private String the;
    
    public Set getSet() {
      return this.Set;
    }
    
    public void setSet(final Set set) {
      this.Set = set;
    }
    
    public List getTypedList() {
      return this.typedList;
    }
    
    public void setTypedList(final List typedList) {
      this.typedList = typedList;
    }
    
    public java.sql.Date getSqlDate() {
      return this.sqlDate;
    }
    
    public void setSqlDate(final java.sql.Date sqlDate) {
      this.sqlDate = sqlDate;
    }
    
    public int[] getInts() {
      return this.ints;
    }
    
    public void setInts(final int[] ints) {
      this.ints = ints;
    }
    
    public List getList() {
      return this.list;
    }
    
    public void setList(final List list) {
      this.list = list;
    }
    
    public TestBean getUninitialized() {
      return this.uninitialized;
    }
    
    public void setUninitialized(final TestBean uninitialized) {
      this.uninitialized = uninitialized;
    }
    
    public TestBean getInitialized() {
      return this.initialized;
    }
    
    public void setInitialized(final TestBean initialized) {
      this.initialized = initialized;
    }
    
    public BigDecimal getBigDecimal() {
      return this.bigDecimal;
    }
    
    public void setBigDecimal(final BigDecimal bigDecimal) {
      this.bigDecimal = bigDecimal;
    }
    
    public Date getDate() {
      return this.date;
    }
    
    public void setDate(final Date date) {
      this.date = date;
    }
    
    public boolean getTheBoolean() {
      return this.theBoolean;
    }
    
    public void setTheBoolean(final boolean theBoolean) {
      this.theBoolean = theBoolean;
    }
    
    public double getTheDouble() {
      return this.theDouble;
    }
    
    public void setTheDouble(final double theDouble) {
      this.theDouble = theDouble;
    }
    
    public int getTheInteger() {
      return this.theInteger;
    }
    
    public void setTheInteger(final int theInteger) {
      this.theInteger = theInteger;
    }
    
    public long getTheLong() {
      return this.theLong;
    }
    
    public void setTheLong(final long theLong) {
      this.theLong = theLong;
    }
    
    public String getTheString() {
      return this.theString;
    }
    
    public void setTheString(final String theString) {
      this.theString = theString;
    }
    
    public String getThe() {
      return this.the;
    }
    
    public void setThe(final String the) {
      this.the = the;
    }
    
    public Collection getCollection() {
      return this.collection;
    }
    
    public void setCollection(final Collection collection) {
      this.collection = collection;
    }
    
    public Money getMoney() {
      return this.money;
    }
    
    public void setMoney(final Money money) {
      this.money = money;
    }
  }
  
  private boolean stringMutatingTransformerActive = false;
  
  public void testGetStringDoesntSkipTransformer() throws Exception {
    assertEquals("Bar", this.objectMatrix.getString("Col 1", "Row 1"));
    assertEquals("Bar2", this.objectMatrix.getString("Col 1", "Row 2"));
    this.objectMatrix.getTransformer().addTransformer(new SpecificTransformer() {
      public Object transform(final Object object, final Class targetType) throws TransformationFailedException {
        if (DelegatingObjectMatrixTest.this.stringMutatingTransformerActive && "Bar".equals(object)) {
          return "Foo";
        } else {
          return TRY_NEXT;
        }
      }
    });
    try {
      this.stringMutatingTransformerActive = true;
      assertEquals("Foo", this.objectMatrix.getString("Col 1", "Row 1"));
      assertEquals("Bar2", this.objectMatrix.getString("Col 1", "Row 2"));
    } finally {
      this.stringMutatingTransformerActive = false;
    }
  }
}
