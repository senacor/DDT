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

import java.util.Calendar;
import java.util.Date;

/**
 * Convenient assert methods for date comparison.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class DateAssert extends AbstractDdtAssert {
  private static final String DEFAULT_DATE_EQUALS_FAILURE = "Year/Month/Day equality check failed";
  
  /**
   * Assert that the two given dates match on the year, month and day fields, using the default failure message.
   * Time-of-Day fields are ignored.
   * 
   * @param expected
   *          The expected value
   * @param actual
   *          The actual value
   */
  public static void assertEqualsYearMonthDay(final Date expected, final Date actual) {
    assertEqualsYearMonthDay(DEFAULT_DATE_EQUALS_FAILURE, expected, actual);
  }
  
  /**
   * Assert that the two given dates match on the year, month and day fields. Time-of-Day fields are ignored.
   * 
   * @param message
   *          The message to show when the assertion fails.
   * @param expected
   *          The expected value
   * @param actual
   *          The actual value
   */
  public static void assertEqualsYearMonthDay(final String message, final Date expected, final Date actual) {
    if (expected == actual) {
      return;
    }
    if ((expected == null) || (actual == null)) {
      failNotEquals(message, expected, actual);
    }
    
    final Calendar calExpected = Calendar.getInstance();
    calExpected.setTime(expected);
    
    final Calendar calActual = Calendar.getInstance();
    calActual.setTime(actual);
    if ((calExpected.get(Calendar.YEAR) != calActual.get(Calendar.YEAR))
        || (calExpected.get(Calendar.DAY_OF_YEAR) != calActual.get(Calendar.DAY_OF_YEAR))) {
      failNotEquals(message, calExpected.getTime(), calActual.getTime());
    }
  }
}
