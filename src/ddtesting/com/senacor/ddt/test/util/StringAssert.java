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

/**
 * Various convenience asserts to verify strings in JUnit tests.
 * 
 * @author Carl-Eric Menzel
 */
public class StringAssert extends AbstractDdtAssert {
  private static final String DEFAULT_STRING_COMPARISON_FAILURE = "String comparison failed";
  
  /**
   * Assert the given string starts with the given prefix.
   * 
   * @param message
   *          Message to be thrown if the assertion fails.
   * @param expectedPrefix
   *          the prefix the string should start with
   * @param value
   *          the string to be tested
   */
  public static void assertStartsWith(final String message, final String expectedPrefix, final String value) {
    assertNotNull("expectedPrefix is null", expectedPrefix);
    assertNotNull("value is null", value);
    if (!value.startsWith(expectedPrefix)) {
      fail(message + " (<value> doesn't start with <expectedPrefix>, <value> is '" + value + "', <expectedPrefix> is '"
          + expectedPrefix + "')");
    }
  }
  
  /**
   * Assert the given string starts with the given prefix. This method provides a default failure message.
   * 
   * @param expectedPrefix
   *          the prefix the string should start with
   * @param value
   *          the string to be tested
   */
  public static void assertStartsWith(final String expectedPrefix, final String value) {
    assertStartsWith(DEFAULT_STRING_COMPARISON_FAILURE, expectedPrefix, value);
  }
  
  /**
   * Assert the given string starts with the given prefix, ignoring case differences.
   * 
   * @param message
   *          Message to be thrown if the assertion fails.
   * @param expectedPrefix
   *          the prefix the string should start with
   * @param value
   *          the string to be tested
   */
  public static void assertStartsWithIgnoreCase(final String message, final String expectedPrefix, final String value) {
    assertNotNull("expectedPrefix is null", expectedPrefix);
    assertNotNull("value is null", value);
    assertStartsWith(message, expectedPrefix.toUpperCase(), value.toUpperCase());
  }
  
  /**
   * Assert the given string starts with the given prefix, ignoring case differences. This method provides a default
   * failure message.
   * 
   * @param expectedPrefix
   *          the prefix the string should start with
   * @param value
   *          the string to be tested
   */
  public static void assertStartsWithIgnoreCase(final String expectedPrefix, final String value) {
    assertStartsWithIgnoreCase(DEFAULT_STRING_COMPARISON_FAILURE, expectedPrefix, value);
  }
  
  /**
   * Assert the given string ends with the given suffix.
   * 
   * @param message
   *          Message to be thrown if the assertion fails.
   * @param expectedSuffix
   *          the suffix the string should end with
   * @param value
   *          the string to be tested
   */
  public static void assertEndsWith(final String message, final String expectedSuffix, final String value) {
    assertNotNull("expectedSuffix is null", expectedSuffix);
    assertNotNull("value is null", value);
    if (!value.endsWith(expectedSuffix)) {
      fail(message + " (<value> doesn't end with <expectedSuffix>, <value> is '" + value + "', <expectedSuffix> is '"
          + expectedSuffix + "')");
    }
  }
  
  /**
   * Assert the given string ends with the given suffix. This method provides a default failure message.
   * 
   * @param expectedSuffix
   *          the suffix the string should end with
   * @param value
   *          the string to be tested
   */
  public static void assertEndsWith(final String expectedSuffix, final String value) {
    assertEndsWith(DEFAULT_STRING_COMPARISON_FAILURE, expectedSuffix, value);
  }
  
  /**
   * Assert the given string ends with the given suffix, ignoring case differences.
   * 
   * @param message
   *          Message to be thrown if the assertion fails.
   * @param expectedSuffix
   *          the suffix the string should end with
   * @param value
   *          the string to be tested
   */
  public static void assertEndsWithIgnoreCase(final String message, final String expectedSuffix, final String value) {
    assertNotNull("expectedSuffix is null", expectedSuffix);
    assertNotNull("value is null", value);
    assertEndsWith(message, expectedSuffix.toUpperCase(), value.toUpperCase());
  }
  
  /**
   * Assert the given string ends with the given suffix, ignoring case differences. This method provides a default
   * failure message.
   * 
   * @param expectedSuffix
   *          the suffix the string should end with
   * @param value
   *          the string to be tested
   */
  public static void assertEndsWithIgnoreCase(final String expectedSuffix, final String value) {
    assertEndsWithIgnoreCase(DEFAULT_STRING_COMPARISON_FAILURE, expectedSuffix, value);
  }
  
  /**
   * Assert the given string contains the given infix.
   * 
   * @param message
   *          Message to be thrown if the assertion fails.
   * @param expectedInfix
   *          the infix the string should contain
   * @param value
   *          the string to be tested
   */
  public static void assertContains(final String message, final String expectedInfix, final String value) {
    assertNotNull("expectedInfix is null", expectedInfix);
    assertNotNull("value is null", value);
    if (value.indexOf(expectedInfix) == -1) {
      fail(message + " (<value> doesn't contain <expectedInfix>, <value> is '" + value + "', <expectedInfix> is '"
          + expectedInfix + "')");
    }
  }
  
  /**
   * Assert the given string contains the given infix. This method provides a default failure message.
   * 
   * @param expectedInfix
   *          the infix the string should contain
   * @param value
   *          the string to be tested
   */
  public static void assertContains(final String expectedInfix, final String value) {
    assertContains(DEFAULT_STRING_COMPARISON_FAILURE, expectedInfix, value);
  }
  
  /**
   * Assert the given string contains the given infix, ignoring case differences.
   * 
   * @param message
   *          Message to be thrown if the assertion fails.
   * @param expectedInfix
   *          the infix the string should contain
   * @param value
   *          the string to be tested
   */
  public static void assertContainsIgnoreCase(final String message, final String expectedInfix, final String value) {
    assertNotNull("expectedInfix is null", expectedInfix);
    assertNotNull("value is null", value);
    assertContains(message, expectedInfix.toUpperCase(), value.toUpperCase());
  }
  
  /**
   * Assert the given string contains the given infix, ignoring case differences. This method provides a default failure
   * message.
   * 
   * @param expectedInfix
   *          the infix the string should contain
   * @param value
   *          the string to be tested
   */
  public static void assertContainsIgnoreCase(final String expectedInfix, final String value) {
    assertContainsIgnoreCase(DEFAULT_STRING_COMPARISON_FAILURE, expectedInfix, value);
  }
  
  /**
   * Assert the given string doesn't contain the given infix.
   * 
   * @param message
   *          Message to be thrown if the assertion fails.
   * @param notExpected
   *          the infix the string should not contain
   * @param value
   *          the string to be tested
   */
  public static void assertContainsNot(final String message, final String notExpected, final String value) {
    assertNotNull("notExpected is null", notExpected);
    assertNotNull("value is null", value);
    if (value.indexOf(notExpected) > -1) {
      fail(message + " (<value> contains <notExpected>, <value> is '" + value + "', <notExpected> is '" + notExpected
          + "')");
    }
  }
  
  /**
   * Assert the given string doesn't contain the given infix. This method provides a default failure message.
   * 
   * @param notExpected
   *          the infix the string should not contain
   * @param value
   *          the string to be tested
   */
  public static void assertContainsNot(final String notExpected, final String value) {
    assertContainsNot(DEFAULT_STRING_COMPARISON_FAILURE, notExpected, value);
  }
  
  /**
   * Assert the given string doesn't contain the given infix, ignoring case differences.
   * 
   * @param message
   *          Message to be thrown if the assertion fails.
   * @param notExpected
   *          the infix the string should not contain
   * @param value
   *          the string to be tested
   */
  public static void assertContainsNotIgnoreCase(final String message, final String notExpected, final String value) {
    assertNotNull("notExpected is null", notExpected);
    assertNotNull("value is null", value);
    assertContainsNot(message, notExpected.toUpperCase(), value.toUpperCase());
  }
  
  /**
   * Assert the given string doesn't contain the given infix, ignoring case difference. This method provides a default
   * failure message.
   * 
   * @param notExpected
   *          the infix the string should not contain
   * @param value
   *          the string to be tested
   */
  public static void assertContainsNotIgnoreCase(final String notExpected, final String value) {
    assertContainsNotIgnoreCase(DEFAULT_STRING_COMPARISON_FAILURE, notExpected, value);
  }
  
  /**
   * Convenient assertion method to compare the contents of two Strings while ignoring upper/lowercase mismatches.
   * 
   * @param message
   *          Message to throw if the values don't match
   * @param expected
   *          the expected String
   * @param actual
   *          the actual String to be tested
   */
  public static void assertEqualsIgnoreCase(final String message, final String expected, final String actual) {
    if (!expected.equalsIgnoreCase(actual)) {
      failNotEquals(message, expected, actual);
    }
  }
  
  /**
   * Convenient assertion method to compare the contents of two Strings while ignoring upper/lowercase mismatches. This
   * method provides a default failure message.
   * 
   * @param expected
   *          the expected String
   * @param actual
   *          the actual String to be tested
   */
  public static void assertEqualsIgnoreCase(final String expected, final String actual) {
    assertEqualsIgnoreCase(DEFAULT_EQUALS_FAILURE, expected, actual);
  }
}
