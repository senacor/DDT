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

import junit.framework.Assert;

/**
 * Abstract superclass for the extended Assert utility classes.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class AbstractDdtAssert extends Assert {
  protected static final String DEFAULT_EQUALS_FAILURE = "Values did not match!";
  
  protected static final String DEFAULT_NOT_EQUALS_FAILURE = "Values are unexpectedly equal";
  
  /**
   * Throw appropriate failure for failed NotEquals tests.
   */
  protected static void failEquals(final String message, final Object expected, final Object actual) {
    fail(message + " (objects are equal, <expected> is: " + expected + ", <actual> is: " + actual + ")");
  }
  
  /**
   * Throw appropriate failure for failed equals tests.
   */
  protected static void failNotEquals(final String message, final Object expected, final Object actual) {
    Assert.fail(message + " (objects are not equal, <expected> is " + expected + ", <actual> is " + actual + ")");
  }
}
