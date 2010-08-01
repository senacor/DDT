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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import com.senacor.ddt.objectmatrix.StringMatrix;
import com.senacor.ddt.typetransformer.SpecificTransformer;

public class StringPatternDateTransformerTest extends TestCase implements SpecificTransformer {
  private static final String DEFAULT_DATE_STRING = "2006-11-13T16:36:23.677";
  
  private boolean transformCalled;
  
  protected void setUp() throws Exception {
    super.setUp();
    this.transformCalled = false;
  }
  
  public void testStringMatrixDate_CalendarToString() throws Exception {
    final SpecificTransformer t = createStringMatrixPatternTransformer();
    final Calendar cal = createCalendar();
    assertEquals(DEFAULT_DATE_STRING, t.transform(cal, String.class));
    assertFalse(this.transformCalled);
  }
  
  public void testStringMatrixDate_DateToString() throws Exception {
    final SpecificTransformer t = createStringMatrixPatternTransformer();
    final Calendar cal = createCalendar();
    assertEquals(DEFAULT_DATE_STRING, t.transform(cal.getTime(), String.class));
    assertFalse(this.transformCalled);
  }
  
  public void testStringMatrixDate_SqlDateToString() throws Exception {
    final SpecificTransformer t = createStringMatrixPatternTransformer();
    final Calendar cal = createCalendar();
    assertEquals(DEFAULT_DATE_STRING, t.transform(new java.sql.Date(cal.getTimeInMillis()), String.class));
    assertFalse(this.transformCalled);
  }
  
  public void testStringMatrixDate_StringToDate() throws Exception {
    final SpecificTransformer t = createStringMatrixPatternTransformer();
    final Calendar cal = createCalendar();
    assertEquals(cal.getTime(), t.transform(DEFAULT_DATE_STRING, Date.class));
    assertFalse(this.transformCalled);
  }
  
  public void testStringMatrixDate_StringToSqlDate() throws Exception {
    final SpecificTransformer t = createStringMatrixPatternTransformer();
    final Calendar cal = createCalendar();
    assertEquals(new java.sql.Date(cal.getTimeInMillis()), t.transform(DEFAULT_DATE_STRING, java.sql.Date.class));
    assertFalse(this.transformCalled);
  }
  
  public void testStringMatrixDate_StringToCalendar() throws Exception {
    final SpecificTransformer t = createStringMatrixPatternTransformer();
    final Calendar cal = createCalendar();
    assertEquals(cal, t.transform(DEFAULT_DATE_STRING, Calendar.class));
    assertFalse(this.transformCalled);
  }
  
  public void testStringMatrixDate_StringToGregorianCalendar() throws Exception {
    final SpecificTransformer t = createStringMatrixPatternTransformer();
    final Calendar cal = setupCalendar(new GregorianCalendar());
    assertEquals(cal, t.transform(DEFAULT_DATE_STRING, GregorianCalendar.class));
    assertFalse(this.transformCalled);
  }
  
  public void testCustomPattern_CalendarToString() throws Exception {
    final SpecificTransformer t = new StringPatternDateTransformer("HHmmyyyy-MM-dd-SSS-ss");
    final Calendar cal = createCalendar();
    assertEquals("16362006-11-13-677-23", t.transform(cal, String.class));
    assertFalse(this.transformCalled);
  }
  
  public void testCustomPattern_StringToCalendar() throws Exception {
    final SpecificTransformer t = new StringPatternDateTransformer("HHmmyyyy-MM-dd-SSS-ss");
    final Calendar cal = createCalendar();
    assertEquals(cal, t.transform("16362006-11-13-677-23", Calendar.class));
    assertFalse(this.transformCalled);
  }
  
  public void testBlowUpOnFail() throws Exception {
    final SpecificTransformer t = createStringMatrixPatternTransformer();
    assertEquals(SpecificTransformer.TRY_NEXT, t.transform("foo", Calendar.class));
  }
  
  private SpecificTransformer createStringMatrixPatternTransformer() {
    final SpecificTransformer t = new StringPatternDateTransformer(StringMatrix.FORMAT_DATE);
    return t;
  }
  
  private Calendar createCalendar() {
    final Calendar cal = Calendar.getInstance();
    setupCalendar(cal);
    return cal;
  }
  
  private Calendar setupCalendar(final Calendar cal) {
    cal.clear();
    cal.set(Calendar.YEAR, 2006);
    cal.set(Calendar.MONTH, Calendar.NOVEMBER);
    cal.set(Calendar.DAY_OF_MONTH, 13);
    cal.set(Calendar.HOUR_OF_DAY, 16);
    cal.set(Calendar.MINUTE, 36);
    cal.set(Calendar.SECOND, 23);
    cal.set(Calendar.MILLISECOND, 677);
    return cal;
  }
  
  public void setNextTransformer(final SpecificTransformer next) {
    throw new UnsupportedOperationException("This method is not implemented (yet)");
  }
  
  public Object transform(final Object object, final Class targetType) {
    this.transformCalled = true;
    return null;
  }
  
  public SpecificTransformer getNextTransformer() {
    throw new UnsupportedOperationException("This method is not implemented (yet)");
  }
}
