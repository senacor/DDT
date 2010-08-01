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
package com.senacor.ddt.util;

/**
 * Helper class to check various conditions, throwing an IllegalArgumentException if the conditions are not met.
 * 
 * @author Carl-Eric Menzel
 */
public class ParamChecker {
  /**
   * Require that the given condition is true.
   * 
   * @param msg
   *          Message for the exception
   * @param condition
   *          condition to check
   * @throws IllegalArgumentException
   *           if <code>condition</code> is false
   */
  public static void require(final String msg, boolean condition) throws IllegalArgumentException {
    if (!condition) {
      fail(msg);
    }
  }
  
  /**
   * Require that the given object is not null.
   * 
   * @param paramName
   *          The parameter name that will be referenced in the exception message
   * @param obj
   *          object to check
   * @throws IllegalArgumentException
   *           if <code>obj</code> is null
   */
  public static void notNull(final String paramName, final Object obj) throws IllegalArgumentException {
    if (obj == null) {
      fail(paramName + " must not be null");
    }
  }
  
  /**
   * Require that the given array is neither null nor empty (length &gt; 0).
   * 
   * @param paramName
   *          The parameter name that will be referenced in the exception message
   * @param array
   *          array to check
   * @throws IllegalArgumentException
   *           if <code>array</code> is null or <code>array.length==0</code>
   */
  public static void notEmpty(final String paramName, final Object[] array) throws IllegalArgumentException {
    if ((array == null) || (array.length == 0)) {
      fail(paramName + " must be neither null nor empty");
    }
  }
  
  /**
   * Require that the given array is neither null nor empty (thus satisfying the conditions of
   * {@link #notEmpty(String, Object[])} and additionally that no element is null.
   * 
   * @param paramName
   *          The parameter name that will be referenced in the exception message
   * @param array
   *          array to check
   * @throws IllegalArgumentException
   *           if any element of <code>array</code> is null
   */
  public static void notNullAnywhere(final String paramName, final Object[] array) throws IllegalArgumentException {
    notEmpty(paramName, array);
    for (int i = 0; i < array.length; i++) {
      if (array[i] == null) {
        fail(paramName + "[" + i + "] must not be null");
      }
    }
  }
  
  /**
   * Require that the given string is neither null, nor empty, nor consisting only of whitespace. (trim().length() must
   * be &gt; 0)
   * 
   * @param paramName
   *          The parameter name that will be referenced in the exception message
   * @param string
   *          string to check
   * @throws IllegalArgumentException
   *           if <code>string</code> is blank
   */
  public static void notBlank(final String paramName, final String string) throws IllegalArgumentException {
    notNull(paramName, string);
    if (string.trim().length() == 0) {
      fail(paramName + " must not be blank");
    }
  }
  
  /**
   * Throw an IllegalArgumentException with the given message.
   * 
   * @param msg
   *          message for the exception
   */
  private static void fail(final String msg) throws IllegalArgumentException {
    throw new IllegalArgumentException(msg);
  }
  
  /**
   * Require that the given string array satisfies all conditions of {@link #notNullAnywhere(String, Object[])} and
   * additionally that none of the strings in the array are empty or whitespace only.
   * 
   * @param paramName
   *          The parameter name that will be referenced in the exception message
   * @param strings
   *          string array to check
   * @throws IllegalArgumentException
   *           if any element of <code>strings</code> is blank
   */
  public static void notBlankAnywhere(final String paramName, final String[] strings) throws IllegalArgumentException {
    notEmpty(paramName, strings);
    for (int i = 0; i < strings.length; i++) {
      notBlank(paramName + "[" + i + "]", strings[i]);
    }
  }
  
  /**
   * Require that the given character satisfies the conditions of {@link #notBlank(String, String)}.
   * 
   * @param paramName
   *          The parameter name that will be referenced in the exception message
   * @param character
   *          character to check
   */
  public static void notBlank(final String paramName, final char character) {
    notBlank(paramName, Character.toString(character));
  }
  
  /**
   * Require that the given number is zero or greater.
   * 
   * @param paramName
   *          The parameter name that will be referenced in the exception message
   * @param param
   *          number to check
   */
  public static void notNegative(final String paramName, final long param) {
    if (param < 0) {
      fail("Parameter '" + paramName + "' must not be negative but is " + param);
    }
  }
}
