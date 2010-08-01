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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import com.senacor.ddt.objectmatrix.csv.CsvStringMatrixReader;

public class AnnotatedStringMatrixTest extends TestCase {
  private AnnotatedStringMatrix matrix;
  
  protected void setUp() throws Exception {
    final Reader input = new InputStreamReader(getClass().getResourceAsStream("annotation-test.csv"));
    this.matrix =
        new EmbeddedAnnotationMatrixDecorator(new DefaultStringMatrix(new CsvStringMatrixReader(input, ';', "foo")));
  }
  
  public void testSimpleEmbeddedRowAnnotation() throws Exception {
    final Properties p = this.matrix.getAnnotation("Col1", "embeddedAnnotation");
    final String value = p.getProperty("embedded");
    assertEquals("value", value);
  }
  
  public void testSimpleEmbeddedColAnnotation() throws Exception {
    final Properties p = this.matrix.getAnnotation("embeddedAnnotation", "Row1");
    final String value = p.getProperty("embedded");
    assertEquals("value", value);
  }
  
  public void testMultipleEmbeddedRowAnnotations() throws Exception {
    final Properties p = this.matrix.getAnnotation("Col1", "multiEmbedded");
    final String value = p.getProperty("embedded1");
    final String value2 = p.getProperty("embedded2");
    assertEquals("value1", value);
    assertEquals("value2", value2);
    assertEquals(2, p.size());
  }
  
  public void testMultipleEmbeddedColAnnotations() throws Exception {
    final Properties p = this.matrix.getAnnotation("multiEmbedded", "Row1");
    final String value = p.getProperty("embedded1");
    final String value2 = p.getProperty("embedded2");
    assertEquals("value1", value);
    assertEquals("value2", value2);
    assertEquals(2, p.size());
  }
  
  public void testPerColumnRowAnnotation() throws Exception {
    final Properties p1 = this.matrix.getAnnotation("Col1", "perColumn");
    final Properties p2 = this.matrix.getAnnotation("Col2", "perColumn");
    assertEquals("I'm in Col 1", p1.getProperty("perColumnAnnotation"));
    assertEquals("I'm in Col 2", p2.getProperty("perColumnAnnotation"));
    assertEquals(1, p1.size());
    assertEquals(1, p2.size());
  }
  
  public void testPerRowColumnAnnotation() throws Exception {
    final Properties p1 = this.matrix.getAnnotation("perRow", "Row1");
    final Properties p2 = this.matrix.getAnnotation("perRow", "Row2");
    assertEquals("I'm in Row 1", p1.getProperty("perRowAnnotation"));
    assertEquals("I'm in Row 2", p2.getProperty("perRowAnnotation"));
    assertEquals(1, p1.size());
    assertEquals(1, p2.size());
  }
  
  public void testMultipleEmbeddedWithNonAnnotatedBaseRow() throws Exception {
    final Properties p = this.matrix.getAnnotation("Col1", "multiEmbeddedWithBase");
    final String value = p.getProperty("embedded1");
    final String value2 = p.getProperty("embedded2");
    assertEquals("valueBase1", value);
    assertEquals("valueBase2", value2);
    assertEquals(2, p.size());
    assertEquals("bar", this.matrix.getString("Col1", "multiEmbeddedWithBase"));
  }
  
  public void testMultipleEmbeddedWithNonAnnotatedBaseCol() throws Exception {
    final Properties p = this.matrix.getAnnotation("multiEmbeddedWithBase", "Row1");
    final String value = p.getProperty("embedded1");
    final String value2 = p.getProperty("embedded2");
    assertEquals("valueBase1", value);
    assertEquals("valueBase2", value2);
    assertEquals(2, p.size());
  }
  
  public void testMultiplePerColumnRowAnnotations() throws Exception {
    final Properties p1 = this.matrix.getAnnotation("Col1", "perColumnMulti");
    final Properties p2 = this.matrix.getAnnotation("Col2", "perColumnMulti");
    assertEquals("I'm in Col 1", p1.getProperty("perColumnAnnotation"));
    assertEquals("I'm in Col 2", p2.getProperty("perColumnAnnotation"));
    assertEquals("2 I'm in Col 1", p1.getProperty("perColumnAnnotation2"));
    assertEquals("2 I'm in Col 2", p2.getProperty("perColumnAnnotation2"));
  }
  
  public void testMultiplePerRowColumnAnnotations() throws Exception {
    final Properties p1 = this.matrix.getAnnotation("perRowMulti", "Row1");
    final Properties p2 = this.matrix.getAnnotation("perRowMulti", "Row2");
    assertEquals("I'm in Row 1", p1.getProperty("perRowAnnotation"));
    assertEquals("I'm in Row 2", p2.getProperty("perRowAnnotation"));
    assertEquals("2 I'm in Row 1", p1.getProperty("perRowAnnotation2"));
    assertEquals("2 I'm in Row 2", p2.getProperty("perRowAnnotation2"));
  }
  
  public void testMultiplePerColumnAndMultipleEmbeddedWithBaseRow() throws Exception {
    final Properties p1 = this.matrix.getAnnotation("Col1", "embeddedAndPerColumn");
    final Properties p2 = this.matrix.getAnnotation("Col2", "embeddedAndPerColumn");
    assertEquals("I'm in Col 1", p1.getProperty("perColumnAnnotation"));
    assertEquals("I'm in Col 2", p2.getProperty("perColumnAnnotation"));
    assertEquals("2 I'm in Col 1", p1.getProperty("perColumnAnnotation2"));
    assertEquals("2 I'm in Col 2", p2.getProperty("perColumnAnnotation2"));
    assertEquals("value1", p1.getProperty("embedded1"));
    assertEquals("value1", p2.getProperty("embedded1"));
    assertEquals("value2", p1.getProperty("embedded2"));
    assertEquals("value2", p2.getProperty("embedded2"));
    assertEquals("", p1.getProperty("withoutValue"));
    assertEquals("", p2.getProperty("withoutValue"));
    assertEquals(5, p1.size());
    assertEquals(5, p2.size());
  }
  
  public void testMultiplePerRowAndMultipleEmbeddedWithBaseCol() throws Exception {
    final Properties p1 = this.matrix.getAnnotation("embeddedAndPerRow", "Row1");
    final Properties p2 = this.matrix.getAnnotation("embeddedAndPerRow", "Row2");
    assertEquals("I'm in Row 1", p1.getProperty("perRowAnnotation"));
    assertEquals("I'm in Row 2", p2.getProperty("perRowAnnotation"));
    assertEquals("2 I'm in Row 1", p1.getProperty("perRowAnnotation2"));
    assertEquals("2 I'm in Row 2", p2.getProperty("perRowAnnotation2"));
    assertEquals("value1", p1.getProperty("embedded1"));
    assertEquals("value1", p2.getProperty("embedded1"));
    assertEquals("value2", p1.getProperty("embedded2"));
    assertEquals("value2", p2.getProperty("embedded2"));
    assertEquals(4, p1.size());
    assertEquals(4, p2.size());
  }
  
  public void testSimpleCellAnnotation() throws Exception {
    final Properties p = this.matrix.getAnnotation("Col1", "cellAnnotation");
    final String value = p.getProperty("cellAnnotation");
    assertEquals("value", value);
    assertEquals(1, p.size());
  }
  
  public void testCellAndEmbeddedRowAnnotation() throws Exception {
    final Properties p = this.matrix.getAnnotation("Col1", "cellAndEmbedded");
    assertEquals("valueCell", p.getProperty("cellAnnotation"));
    assertEquals("valueEmbedded", p.getProperty("embedded"));
    assertEquals(2, p.size());
  }
  
  public void testFilteredRowNames() throws Exception {
    final List rowNames = this.matrix.getRowNames();
    final Iterator iter = rowNames.iterator();
    assertEquals("Reserved", iter.next());
    assertEquals("embeddedAnnotation", iter.next());
    assertEquals("multiEmbedded", iter.next());
    assertEquals("multiEmbeddedWithBase", iter.next());
    assertEquals("perColumn", iter.next());
    assertEquals("perColumnMulti", iter.next());
    assertEquals("embeddedAndPerColumn", iter.next());
    assertEquals("Row1", iter.next());
    assertEquals("Row2", iter.next());
    assertEquals("cellAnnotation", iter.next());
    assertEquals("cellAndEmbedded", iter.next());
    assertEquals(16, rowNames.size());
  }
  
  public void testFilteredColNames() throws Exception {
    final Iterator iter = this.matrix.getColNames().iterator();
    assertEquals("Reserved", iter.next());
    assertEquals("Col1", iter.next());
    assertEquals("Col2", iter.next());
    assertEquals("embeddedAnnotation", iter.next());
    assertEquals("multiEmbedded", iter.next());
    assertEquals("perRow", iter.next());
    assertEquals("perRowMulti", iter.next());
    assertEquals("embeddedAndPerRow", iter.next());
    assertEquals("multiEmbeddedWithBase", iter.next());
  }
  
  public void testStripCellAnnotations() throws Exception {
    assertEquals("foo", this.matrix.getString("Col1", "cellAnnotation"));
  }
  
  public void testMasking() throws Exception {
    Properties annotation = this.matrix.getAnnotation("maskingTest", "shouldBeMasked");
    assertEquals("bar", annotation.getProperty("foo"));
    assertEquals("", this.matrix.getString("maskingTest", "shouldBeMasked"));
    annotation = this.matrix.getAnnotation("maskingTest", "shouldNotBeMasked");
    assertEquals("", annotation.getProperty("foo"));
    assertEquals("baz", this.matrix.getString("maskingTest", "shouldNotBeMasked"));
  }
  
  public void testGlobalAnnotations() throws Exception {
    final Reader input = new InputStreamReader(getClass().getResourceAsStream("global-annotation-test.csv"));
    this.matrix =
        new EmbeddedAnnotationMatrixDecorator(new DefaultStringMatrix(new CsvStringMatrixReader(input, ';', "foo")));
    Properties annotation = this.matrix.getAnnotation("Col1", "Row1");
    assertEquals(3, annotation.size());
    assertGlobalAnnotation(annotation, "globalcolumn");
    annotation = this.matrix.getAnnotation("Col2", "multiEmbeddedWithBase");
    assertEquals(5, annotation.size());
    assertEquals("valueBase1", annotation.getProperty("embedded1"));
    assertEquals("valueBase2", annotation.getProperty("embedded2"));
    assertGlobalAnnotation(annotation, "globalcolumn2");
  }
  
  private void assertGlobalAnnotation(final Properties annotation, final String columnvalue) {
    assertEquals("globalvalue", annotation.getProperty("global-annotation-embedded"));
    assertEquals(columnvalue, annotation.getProperty("global-annotation-percolumn"));
    assertTrue(annotation.containsKey("global-flag"));
  }
  
  public void testMultiEmbeddedAndValueless() throws Exception {
    Properties p = this.matrix.getAnnotation("Col1", "multi.Embedded[AndValueless]");
    assertEquals("bar", this.matrix.getString("Col1", "multi.Embedded[AndValueless]"));
    assertEquals("foo", p.getProperty("type"));
    assertTrue(p.containsKey("ignore-if-null"));
    p = this.matrix.getAnnotation("Col1", "single.baseless[nullCell]");
    assertTrue(p.containsKey("ignore-if-null"));
  }
}
