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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Variant of {@link PropertyEquals} that only tests a given set of properties.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class SelectivePropertyEquals extends PropertyEquals {
  private final Set propertyNames;
  
  /**
   * @param propertyNames
   *          The names of properties to check.
   */
  public SelectivePropertyEquals(final String[] propertyNames) {
    this(new HashSet(Arrays.asList(propertyNames)));
  }
  
  /**
   * @param propertyNames
   *          The names of properties to check.
   */
  public SelectivePropertyEquals(final Set propertyNames) {
    if ((propertyNames == null) || (propertyNames.size() == 0)) {
      throw new IllegalArgumentException("propertyNames must not be null or empty");
    }
    this.propertyNames = propertyNames;
  }
  
  /**
   * @param propertyName
   *          The single property name to check.
   */
  public SelectivePropertyEquals(final String propertyName) {
    this(new String[] { propertyName });
  }
  
  /**
   * @see com.senacor.ddt.test.util.PropertyEquals#equals(java.lang.Object, java.lang.Object)
   */
  public boolean equals(final Object a, final Object b) {
    return equals(getPropertyNames(), a, b);
  }
  
  protected Set getPropertyNames() {
    return this.propertyNames;
  }
}
