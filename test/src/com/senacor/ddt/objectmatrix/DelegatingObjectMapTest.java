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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import com.senacor.ddt.objectmatrix.excel.ExcelObjectMatrixFactory;
import com.senacor.ddt.test.util.DateAssert;
import com.senacor.ddt.test.util.NumberAssert;
import com.senacor.ddt.test.util.StringAssert;

public class DelegatingObjectMapTest extends TestCase {
  private ObjectMatrix matrix;
  
  public void testKeyList_RowMode() throws Exception {
    createMatrix("keyList");
    ObjectMap map = createRowMap("keyList");
    List keys = map.getKeys();
    Iterator it = keys.iterator();
    assertEquals("keyList", it.next());
    assertEquals("Quux", it.next());
    assertEquals("Meta", it.next());
    assertEquals("Hyper", it.next());
    assertFalse(it.hasNext());
  }
  
  public void testKeyList_ColMode() throws Exception {
    createMatrix("keyList");
    ObjectMap map = createColMap("keyList");
    List keys = map.getKeys();
    Iterator it = keys.iterator();
    assertEquals("keyList", it.next());
    assertEquals("Foo", it.next());
    assertEquals("Bar", it.next());
    assertEquals("Baz", it.next());
    assertFalse(it.hasNext());
  }
  
  private ObjectMap createColMap(String colName) {
    return new DelegatingObjectMap(this.matrix, colName, DelegatingObjectMap.Mode.COLUMN);
  }
  
  private void createMatrix(String sheet) {
    ExcelObjectMatrixFactory eomf =
        new ExcelObjectMatrixFactory(getClass().getResourceAsStream("objectmap-test.xls"), new String[] { sheet });
    this.matrix = eomf.create()[0];
  }
  
  private ObjectMap createRowMap(String rowName) {
    return new DelegatingObjectMap(this.matrix, rowName, DelegatingObjectMap.Mode.ROW);
  }
  
  public void testGetObject_RowMode() throws Exception {
    createMatrix("getObject");
    ObjectMap map = createRowMap("Row1");
    assertEquals(Boolean.TRUE, map.getBoolean("Boolean"));
    DateAssert.assertEqualsYearMonthDay(new Date(107, 4, 21), map.getDate("Date"));
    assertEquals(0.345, map.getDouble("Double").doubleValue(), 0.0001);
    assertEquals(42, map.getInteger("Integer").intValue());
    assertEquals(2000000000, map.getLong("Long").longValue());
    assertEquals("myString", map.getString("String"));
    NumberAssert.assertEquals(new BigDecimal("42.35"), map.getBigDecimal("BigDecimal"));
  }
  
  public void testGetObject_ColMode() throws Exception {
    createMatrix("getObject");
    ObjectMap map = createColMap("Col1");
    assertEquals(Boolean.FALSE, map.getBoolean("Boolean"));
    DateAssert.assertEqualsYearMonthDay(new Date(108, 4, 21), map.getDate("Date"));
    assertEquals(0.346, map.getDouble("Double").doubleValue(), 0.0001);
    assertEquals(41, map.getInteger("Integer").intValue());
    assertEquals(2000000009, map.getLong("Long").longValue());
    assertEquals("myString2", map.getString("String"));
    NumberAssert.assertEquals(new BigDecimal("43.35"), map.getBigDecimal("BigDecimal"));
  }
  
  public void testGetAnnotations_RowMode() throws Exception {
    createMatrix("annotations");
    ObjectMap map = createRowMap("Row1");
    Properties p = map.getAnnotation("key1");
    assertEquals("foo", map.getString("key1"));
    assertEquals("cellValue", p.getProperty("cell"));
    assertEquals("embValue", p.getProperty("emb"));
    assertEquals("perValue", p.getProperty("per"));
    assertEquals("keyPerValue", p.getProperty("keyPer"));
    assertEquals("keyEmbValue", p.getProperty("keyEmb"));
    assertEquals(5, p.size());
  }
  
  public void testGetAnnotations_ColMode() throws Exception {
    createMatrix("annotations2");
    ObjectMap map = createColMap("Col1");
    Properties p = map.getAnnotation("key1");
    assertEquals("foo2", map.getString("key1"));
    assertEquals("cellValue2", p.getProperty("cell"));
    assertEquals("embValue2", p.getProperty("emb"));
    assertEquals("perValue2", p.getProperty("per"));
    assertEquals("keyPerValue2", p.getProperty("keyPer"));
    assertEquals("keyEmbValue2", p.getProperty("keyEmb"));
    assertEquals(5, p.size());
  }
  
  public void testValidRowOrCol() throws Exception {
    createMatrix("keyList");
    try {
      createColMap("nonexistant column");
      fail("should have thrown exception");
    } catch (IllegalArgumentException e) {
      StringAssert.assertContains("column", e.getMessage());
    }
    try {
      createRowMap("nonexistant row");
      fail("should have thrown exception");
    } catch (IllegalArgumentException e) {
      StringAssert.assertContains("row", e.getMessage());
    }
  }
}
