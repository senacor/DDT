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
 * Generic assert methods complementing the default JUnit asserts.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class GenericAssert extends AbstractDdtAssert {
  /**
   * Assert that the two given objects are <i>not</i> equal, i.e. exactly one of them is null or
   * <code>expected.equals(actual)</code> returns false.
   * 
   * @param message
   *          Message to throw if the values unexpectedly do match
   * @param notExpected
   *          the not expected object
   * @param actual
   *          the actual object to be tested
   */
  public static void assertNotEquals(final String message, final Object notExpected, final Object actual) {
    if ((notExpected == actual) || ((notExpected != null) && notExpected.equals(actual))) {
      failEquals(message, actual, actual);
    }
  }
  
  /**
   * Assert that the two given objects are <i>not</i> equal, i.e. exactly one of them is null or
   * <code>expected.equals(actual)</code> returns false. This method provides a default failure message.
   * 
   * @param notExpected
   *          the not expected object
   * @param actual
   *          the actual object to be tested
   */
  public static void assertNotEquals(final Object notExpected, final Object actual) {
    assertNotEquals(DEFAULT_NOT_EQUALS_FAILURE, notExpected, actual);
  }
}
