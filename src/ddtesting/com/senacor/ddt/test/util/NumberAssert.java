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

/**
 * Utility class to assert number values in tests.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class NumberAssert extends AbstractDdtAssert {
  /**
   * The delta that will be used by default to check two doubles for equality.
   */
  public static final double DEFAULT_DOUBLE_COMPARISON_DELTA = 0.0001;
  
  private static final String DEFAULT_COMPARISON_FAILURE = "Values don't compare as expected";
  
  /**
   * Assert that the two given longs (or other integer values) are <i>not</i> equal.
   * 
   * @param message
   *          Message to throw if the values unexpectedly do match
   * @param notExpected
   *          the not expected value
   * @param actual
   *          the actual value to be tested
   */
  public static void assertNotEquals(final String message, final long notExpected, final long actual) {
    if (notExpected == actual) {
      failEquals(message, new Long(notExpected), new Long(actual));
    }
  }
  
  /**
   * Assert that the two given longs (or other integer values) are <i>not</i> equal. This method provides a default
   * failure message.
   * 
   * @param notExpected
   *          the not expected value
   * @param actual
   *          the actual value to be tested
   */
  public static void assertNotEquals(final long notExpected, final long actual) {
    assertNotEquals(DEFAULT_NOT_EQUALS_FAILURE, notExpected, actual);
  }
  
  /**
   * Assert that the two given doubles are <i>not</i> equal, i.e. they differ by at least the amount of the given delta.
   * 
   * @param message
   *          Message to throw if the values unexpectedly do match
   * @param notExpected
   *          the not expected value
   * @param actual
   *          the actual value to be tested
   * @param delta
   *          the delta by which the two values should at least differ
   */
  public static void assertNotEquals(final String message, final double notExpected, final double actual,
      final double delta) {
    // based on junit.framework.Assert#assertEquals(String, double, double, double)
    if (Double.isInfinite(notExpected)) {
      if (notExpected == actual) {
        failEquals(message, new Double(notExpected), new Double(actual));
      }
    } else if (equalsWithDelta(notExpected, actual, delta)) { // Because comparison with NaN always
      // returns false
      failEquals(message, new Double(notExpected), new Double(actual));
    }
  }
  
  /**
   * Assert that the two given doubles are <i>not</i> equal, i.e. they differ by at least the amount of the given delta.
   * This method provides a default failure message.
   * 
   * @param notExpected
   *          the not expected value
   * @param actual
   *          the actual value to be tested
   * @param delta
   *          the delta by which the two values should at least differ
   */
  public static void assertNotEquals(final double notExpected, final double actual, final double delta) {
    assertNotEquals(DEFAULT_NOT_EQUALS_FAILURE, notExpected, actual, delta);
  }
  
  /**
   * Return true if the difference between expected and actual is at most delta.
   */
  private static boolean equalsWithDelta(final double expected, final double actual, final double delta) {
    return Math.abs(expected - actual) <= delta;
  }
  
  /**
   * Convenience method for comparing two <code>{@link BigDecimal}</code> instances. The equals() method of BigDecimal
   * cannot be used to compare just the value, since it also looks at the scale factor, which most applications to not
   * care about. This implementation only compares the actual values.
   * 
   * @param message
   *          Message to throw if the values don't match
   * @param expected
   *          the expected BigDecimal value
   * @param actual
   *          the actual BigDecimal value to be tested
   */
  public static void assertEquals(final String message, final BigDecimal expected, final BigDecimal actual) {
    if (expected.compareTo(actual) != 0) {
      failNotEquals(message, expected.toString(), actual.toString());
    }
  }
  
  /**
   * Convenience method for comparing two <code>{@link BigDecimal}</code> instances. The equals() method of BigDecimal
   * cannot be used to compare just the value, since it also looks at the scale factor, which most applications to not
   * care about. This implementation only compares the actual values. This method provides a default failure message.
   * 
   * @param expected
   *          the expected BigDecimal value
   * @param actual
   *          the actual BigDecimal value to be tested
   * @see #assertEquals(String, BigDecimal, BigDecimal)
   */
  public static void assertEquals(final BigDecimal expected, final BigDecimal actual) {
    assertEquals(DEFAULT_EQUALS_FAILURE, expected, actual);
  }
  
  /**
   * Assert that the given value is less than or equal to the given threshold, i.e. that
   * <code>value &lt;= threshold</code> is true.
   * 
   * @param message
   *          Message to throw if the assertion fails
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   */
  public static void assertLessOrEqual(final String message, final long threshold, final long value) {
    if (value > threshold) {
      failNotLessOrEqual(message, "" + threshold, "" + value);
    }
  }
  
  /**
   * Assert that the given value is less than or equal to the given threshold, i.e. that
   * <code>value &lt;= threshold</code> is true. This method provides a default failure message.
   * 
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   */
  public static void assertLessOrEqual(final long threshold, final long value) {
    assertLessOrEqual(DEFAULT_COMPARISON_FAILURE, threshold, value);
  }
  
  /**
   * Assert that the given value is less than or equal to the given threshold, i.e. that
   * <code>value &lt;= threshold</code> is true.
   * 
   * @param message
   *          Message to throw if the assertion fails
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   * @param delta
   *          The delta within which the two values should be considered equal
   */
  public static void assertLessOrEqual(final String message, final double threshold, final double value,
      final double delta) {
    if ((value > threshold) && !equalsWithDelta(threshold, value, delta)) {
      failNotLessOrEqual(message, "" + threshold, "" + value);
    }
  }
  
  /**
   * Assert that the given value is less than or equal to the given threshold, i.e. that
   * <code>value &lt;= threshold</code> is true. This method provides a default failure message.
   * 
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   * @param delta
   *          The delta within which the two values should be considered equal
   */
  public static void assertLessOrEqual(final double threshold, final double value, final double delta) {
    assertLessOrEqual(DEFAULT_COMPARISON_FAILURE, threshold, value, delta);
  }
  
  /**
   * Assert that the given value is less than the given threshold, i.e. that <code>value &lt; threshold</code> is true.
   * 
   * @param message
   *          Message to throw if the assertion fails
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   */
  public static void assertLessThan(final String message, final long threshold, final long value) {
    if (value >= threshold) {
      failNotLessThan(message, "" + threshold, "" + value);
    }
  }
  
  /**
   * Assert that the given value is less than the given threshold, i.e. that <code>value &lt; threshold</code> is true.
   * This method provides a default failure message.
   * 
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   */
  public static void assertLessThan(final long threshold, final long value) {
    assertLessThan(DEFAULT_COMPARISON_FAILURE, threshold, value);
  }
  
  /**
   * Assert that the given value is less than the given threshold, i.e. that <code>value &lt; threshold</code> is true.
   * 
   * @param message
   *          Message to throw if the assertion fails
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   * @param delta
   *          The delta by which the two values should at least differ
   * 
   */
  public static void assertLessThan(final String message, final double threshold, final double value, final double delta) {
    if ((value > threshold) || equalsWithDelta(threshold, value, delta)) {
      failNotLessThan(message, "" + threshold, "" + value);
    }
  }
  
  /**
   * Assert that the given value is less than the given threshold, i.e. that <code>value &lt; threshold</code> is true.
   * This method provides a default failure message.
   * 
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   * @param delta
   *          The delta by which the two values should at least differ
   * 
   */
  public static void assertLessThan(final double threshold, final double value, final double delta) {
    assertLessThan(DEFAULT_COMPARISON_FAILURE, threshold, value, delta);
  }
  
  /**
   * Assert that the given value is greater than the given threshold, i.e. that <code>value &gt; threshold</code> is
   * true. This method provides a default failure message.
   * 
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   */
  public static void assertGreaterThan(final long threshold, final long value) {
    assertGreaterThan(DEFAULT_COMPARISON_FAILURE, threshold, value);
  }
  
  /**
   * Assert that the given value is greater than the given threshold, i.e. that <code>value &gt; threshold</code> is
   * true.
   * 
   * @param message
   *          Message to throw if the assertion fails
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   */
  public static void assertGreaterThan(final String message, final long threshold, final long value) {
    if (value <= threshold) {
      failNotGreaterThan(message, "" + threshold, "" + value);
    }
  }
  
  /**
   * Assert that the given value is greater than the given threshold, i.e. that <code>value &gt; threshold</code> is
   * true.
   * 
   * @param message
   *          Message to throw if the assertion fails
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   * @param delta
   *          The delta by which the two values should at least differ
   * 
   */
  public static void assertGreaterThan(final String message, final double threshold, final double value,
      final double delta) {
    if ((value < delta) || equalsWithDelta(threshold, value, delta)) {
      failNotGreaterThan(message, "" + threshold, "" + value);
    }
  }
  
  /**
   * Assert that the given value is greater than the given threshold, i.e. that <code>value &gt; threshold</code> is
   * true. This method provides a default failure message.
   * 
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   * @param delta
   *          The delta by which the two values should at least differ
   * 
   */
  public static void assertGreaterThan(final double threshold, final double value, final double delta) {
    assertGreaterThan(DEFAULT_COMPARISON_FAILURE, threshold, value, delta);
  }
  
  /**
   * Assert that the given value is greater than or equal to the given threshold, i.e. that
   * <code>value &gt;= threshold</code> is true.
   * 
   * @param message
   *          Message to throw if the assertion fails
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   */
  public static void assertGreaterOrEqual(final String message, final long threshold, final long value) {
    if (value < threshold) {
      failNotGreaterOrEqual(message, "" + threshold, "" + value);
    }
  }
  
  /**
   * Assert that the given value is greater than or equal to the given threshold, i.e. that
   * <code>value &gt;= threshold</code> is true. This method provides a default failure message.
   * 
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   */
  public static void assertGreaterOrEqual(final long threshold, final long value) {
    assertGreaterOrEqual(DEFAULT_COMPARISON_FAILURE, threshold, value);
  }
  
  /**
   * Assert that the given value is greater than or equal to the given threshold, i.e. that
   * <code>value &gt;= threshold</code> is true.
   * 
   * @param message
   *          Message to throw if the assertion fails
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   * @param delta
   *          The delta within which the two values should be considered equal
   */
  public static void assertGreaterOrEqual(final String message, final double threshold, final double value,
      final double delta) {
    if ((value < threshold) && !equalsWithDelta(threshold, value, delta)) {
      failNotGreaterOrEqual(message, "" + threshold, "" + value);
    }
  }
  
  /**
   * Assert that the given value is greater than or equal to the given threshold, i.e. that
   * <code>value &gt;= threshold</code> is true. This method provides a default failure message.
   * 
   * @param threshold
   *          The threshold
   * @param value
   *          The value to be tested against the threshold
   * @param delta
   *          The delta within which the two values should be considered equal
   */
  public static void assertGreaterOrEqual(final double threshold, final double value, final double delta) {
    assertGreaterOrEqual(DEFAULT_COMPARISON_FAILURE, threshold, value, delta);
  }
  
  /**
   * Throw appropriate failure for failed LessThan tests.
   */
  private static void failNotLessThan(final String message, final String threshold, final String value) {
    fail(message + " (value is not less than threshold, <threshold> is: " + threshold + ", <value> is: " + value + ")");
  }
  
  /**
   * Throw appropriate failure for failed GreaterThan tests.
   */
  private static void failNotGreaterThan(final String message, final String threshold, final String value) {
    fail(message + " (value is not greater than threshold, <threshold> is: " + threshold + ", <value> is: " + value
        + ")");
  }
  
  /**
   * Throw appropriate failure for failed LessOrEqual tests.
   */
  private static void failNotLessOrEqual(final String message, final String threshold, final String value) {
    fail(message + " (value is not less than or equal to threshold, <threshold> is: " + threshold + ", <value> is: "
        + value + ")");
  }
  
  /**
   * Throw appropriate failure for failed GreaterOrEqual tests.
   */
  private static void failNotGreaterOrEqual(final String message, final String threshold, final String value) {
    fail(message + " (value is not greater than or equal to threshold, <threshold> is: " + threshold + ", <value> is: "
        + value + ")");
  }
}
