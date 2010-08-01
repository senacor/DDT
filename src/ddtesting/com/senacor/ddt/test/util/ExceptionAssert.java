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
 * Various convenience asserts to verify thrown exceptions in JUnit tests.
 * 
 * @author Carl-Eric Menzel
 */
public class ExceptionAssert extends AbstractDdtAssert {
  /**
   * Convenience method to check for an expected exception type. This method looks at the class name of the actual
   * Throwable and compares it to the given String <code>expectedExceptionTypeName</code>. In case of nested exceptions
   * it also looks up to ten levels deep.
   * 
   * @param message
   *          Message to throw if the exceptions don't match
   * @param expectedExceptionTypeName
   *          Case-sensitive name (without package!) of the expected throwable class
   * @param actual
   *          Actual thrown exception to be tested
   */
  public static void assertExceptionType(final String message, final String expectedExceptionTypeName,
      final Throwable actual) {
    if (!matchExceptionType(expectedExceptionTypeName, actual, 10)) {
      fail(message + " (expected: " + expectedExceptionTypeName + ", actual: " + actual.getClass() + " -- "
          + actual.getMessage() + ")");
    }
  }
  
  /**
   * Convenience method to check for an expected exception type, with default failure message.
   * 
   * @param expectedExceptionTypeName
   *          Case-sensitive name (without package!) of the expected throwable class
   * @param actual
   *          Actual thrown exception to be tested
   * @see #assertExceptionType(String, String, Throwable)
   */
  public static void assertExceptionType(final String expectedExceptionTypeName, final Throwable actual) {
    assertExceptionType("Wrong exception thrown", expectedExceptionTypeName, actual);
  }
  
  // TODO check superclasses to allow more generic checking
  // TODO allow both simple class names and fully qualified class names
  // TODO replace crude tries mechanism with set-based loop checking
  private static boolean matchExceptionType(final String expectedExceptionTypeName, final Throwable actual,
      final int tries) {
    final String actualName = actual.getClass().getName().substring(actual.getClass().getName().lastIndexOf('.') + 1);
    if (actualName.equals(expectedExceptionTypeName)) {
      return true;
    } else if ((actual.getCause() != null) && (tries > 0)) {
      return matchExceptionType(expectedExceptionTypeName, actual.getCause(), tries - 1);
    } else {
      return false;
    }
  }
}
