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
package com.senacor.ddt.test.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class AssertTest extends TestCase {
  public void testEqualsBigDecimal() throws Exception {
    BigDecimal expected = new BigDecimal(12345);
    BigDecimal actualCorrect = new BigDecimal(12345);
    BigDecimal actualFalse = new BigDecimal(54321);
    try {
      NumberAssert.assertEquals(expected, actualCorrect);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are equal");
    }
    try {
      NumberAssert.assertEquals(expected, actualFalse);
      fail("should have failed here, values are not equal");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testEqualsIgnoreCaseString() throws Exception {
    String expected = "foo";
    String actualCorrect = "Foo";
    String actualFalse = "Bar";
    try {
      StringAssert.assertEqualsIgnoreCase(expected, actualCorrect);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are equal");
    }
    try {
      StringAssert.assertEqualsIgnoreCase(expected, actualFalse);
      fail("should have failed here, values are not equal");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testStartsWith() throws Exception {
    String prefix = "foo";
    String valuePass = "fooBar";
    String valueFail1 = "barFoo";
    String valueFail2 = "FooBar";
    try {
      StringAssert.assertStartsWith(prefix, valuePass);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertStartsWith(prefix, valueFail1);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
    try {
      StringAssert.assertStartsWith(prefix, valueFail2);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testStartsWithIgnoreCase() throws Exception {
    String prefix = "foo";
    String valuePass = "fooBar";
    String valuePass2 = "FooBar";
    String valueFail1 = "barFoo";
    try {
      StringAssert.assertStartsWithIgnoreCase(prefix, valuePass);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertStartsWithIgnoreCase(prefix, valuePass2);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertStartsWithIgnoreCase(prefix, valueFail1);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testEndsWith() throws Exception {
    String suffix = "foo";
    String valuePass = "barfoo";
    String valueFail1 = "barFoo";
    String valueFail2 = "FooBar";
    try {
      StringAssert.assertEndsWith(suffix, valuePass);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertEndsWith(suffix, valueFail1);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
    try {
      StringAssert.assertEndsWith(suffix, valueFail2);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testEndsWithIgnoreCase() throws Exception {
    String suffix = "foo";
    String valuePass = "barfoo";
    String valuePass2 = "barFoo";
    String valueFail1 = "FooBar";
    try {
      StringAssert.assertEndsWithIgnoreCase(suffix, valuePass);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertEndsWithIgnoreCase(suffix, valuePass2);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertEndsWithIgnoreCase(suffix, valueFail1);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testContains() throws Exception {
    String infix = "foo";
    String valuePass = "barfoo";
    String valueFail1 = "barFoo";
    String valueFail2 = "FooBar";
    try {
      StringAssert.assertContains(infix, valuePass);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertContains(infix, valueFail1);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
    try {
      StringAssert.assertContains(infix, valueFail2);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testContainsIgnoreCase() throws Exception {
    String infix = "foo";
    String valuePass = "barfoo";
    String valuePass2 = "barFoo";
    String valueFail = "Bar";
    try {
      StringAssert.assertContainsIgnoreCase(infix, valuePass);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertContainsIgnoreCase(infix, valuePass2);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertContainsIgnoreCase(infix, valueFail);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testContainsNot() throws Exception {
    String infix = "foo";
    String valuePass = "bar";
    String valuePass2 = "Foo";
    String valueFail1 = "barfoo";
    try {
      StringAssert.assertContainsNot(infix, valuePass);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertContainsNot(infix, valuePass2);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertContainsNot(infix, valueFail1);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testContainsNotIgnoreCase() throws Exception {
    String infix = "foo";
    String valuePass = "bar";
    String valueFail2 = "Foo";
    String valueFail1 = "barfoo";
    try {
      StringAssert.assertContainsNotIgnoreCase(infix, valuePass);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      StringAssert.assertContainsNotIgnoreCase(infix, valueFail1);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
    try {
      StringAssert.assertContainsNotIgnoreCase(infix, valueFail2);
      fail("should have failed here, values are not correct");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testNotEqualsObject() throws Exception {
    Object expected = new Long(12345);
    Object actualFalse = new Long(12345);
    Object actualCorrect = new Long(54321);
    try {
      GenericAssert.assertNotEquals(expected, actualCorrect);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are not equal");
    }
    try {
      GenericAssert.assertNotEquals(expected, actualFalse);
      fail("should have failed here, values are equal");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testNotEqualsLong() throws Exception {
    long expected = 12345;
    long actualFalse = 12345;
    long actualCorrect = 54321;
    try {
      NumberAssert.assertNotEquals(expected, actualCorrect);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are not equal");
    }
    try {
      NumberAssert.assertNotEquals(expected, actualFalse);
      fail("should have failed here, values are equal");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testNotEqualsDouble() throws Exception {
    double expected = 12.345;
    double actualFalse = 12.3451;
    double actualCorrect = 12.346;
    try {
      NumberAssert.assertNotEquals(expected, actualCorrect, 0.0001);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are not equal");
    }
    try {
      NumberAssert.assertNotEquals(expected, actualFalse, 0.0001);
      fail("should have failed here, values are equal");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testLessOrEqualLong() throws Exception {
    long threshold = 100;
    long valuePass1 = 50;
    long valuePass2 = 100;
    long valueFail = 101;
    try {
      NumberAssert.assertLessOrEqual(threshold, valuePass1);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertLessOrEqual(threshold, valuePass2);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertLessOrEqual(threshold, valueFail);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testLessOrEqualDouble() throws Exception {
    double threshold = 100;
    double valuePass1 = 99.9;
    double valuePass2 = 100.00001;
    double valueFail = 100.1;
    try {
      NumberAssert.assertLessOrEqual(threshold, valuePass1, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertLessOrEqual(threshold, valuePass2, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertLessOrEqual(threshold, valueFail, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testLessThanLong() throws Exception {
    long threshold = 100;
    long valuePass1 = 50;
    long valueFail2 = 100;
    long valueFail = 101;
    try {
      NumberAssert.assertLessThan(threshold, valuePass1);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertLessThan(threshold, valueFail);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
    try {
      NumberAssert.assertLessThan(threshold, valueFail2);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testLessThanDouble() throws Exception {
    double threshold = 100;
    double valuePass1 = 50;
    double valueFail2 = 99.99999;
    double valueFail = 101;
    try {
      NumberAssert.assertLessThan(threshold, valuePass1, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertLessThan(threshold, valueFail, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
    try {
      NumberAssert.assertLessThan(threshold, valueFail2, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testGreaterThanLong() throws Exception {
    long threshold = 100;
    long valuePass1 = 150;
    long valueFail2 = 100;
    long valueFail = 99;
    try {
      NumberAssert.assertGreaterThan(threshold, valuePass1);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertGreaterThan(threshold, valueFail);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
    try {
      NumberAssert.assertGreaterThan(threshold, valueFail2);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testGreaterThanDouble() throws Exception {
    double threshold = 100;
    double valuePass1 = 150;
    double valueFail2 = 100.00001;
    double valueFail = 99;
    try {
      NumberAssert.assertGreaterThan(threshold, valuePass1, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertGreaterThan(threshold, valueFail, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
    try {
      NumberAssert.assertGreaterThan(threshold, valueFail2, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testGreaterOrEqualLong() throws Exception {
    long threshold = 100;
    long valuePass1 = 150;
    long valuePass2 = 100;
    long valueFail = 99;
    try {
      NumberAssert.assertGreaterOrEqual(threshold, valuePass1);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertGreaterOrEqual(threshold, valuePass2);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertGreaterOrEqual(threshold, valueFail);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testGreaterOrEqualDouble() throws Exception {
    double threshold = 100;
    double valuePass1 = 100.1;
    double valuePass2 = 100.00001;
    double valueFail = 99.9;
    try {
      NumberAssert.assertGreaterOrEqual(threshold, valuePass1, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertGreaterOrEqual(threshold, valuePass2, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are correct");
    }
    try {
      NumberAssert.assertGreaterOrEqual(threshold, valueFail, NumberAssert.DEFAULT_DOUBLE_COMPARISON_DELTA);
      fail("should have failed");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testEqualsExceptionType() throws Exception {
    String expected = "RuntimeException";
    Throwable actualCorrect = new RuntimeException();
    Throwable actualFalse = new Exception();
    try {
      ExceptionAssert.assertExceptionType(expected, actualCorrect);
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are equal");
    }
    try {
      ExceptionAssert.assertExceptionType(expected, actualFalse);
      fail("should have failed here, values are not equal");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
  
  public void testDateEquals() throws Exception {
    Calendar expected = new GregorianCalendar(2005, 6, 7, 20, 21, 22);
    Calendar actualCorrect = new GregorianCalendar(2005, 6, 7, 20, 21, 22);
    Calendar actualCorrect_DifferentTime = new GregorianCalendar(2005, 6, 7, 21, 22, 23);
    Calendar actualFalse = new GregorianCalendar(2006, 7, 8);
    try {
      DateAssert.assertEqualsYearMonthDay(expected.getTime(), actualCorrect.getTime());
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are ok");
    }
    try {
      DateAssert.assertEqualsYearMonthDay(expected.getTime(), actualCorrect_DifferentTime.getTime());
    } catch (AssertionFailedError e) {
      fail("should not have failed here, values are ok");
    }
    try {
      DateAssert.assertEqualsYearMonthDay(expected.getTime(), actualFalse.getTime());
      fail("should have failed here, values are not ok");
    } catch (AssertionFailedError e) {
      ; // expected
    }
  }
}
