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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * Compares two objects by looking at all properties of the first object and comparing them to the second object's
 * properties of the same name.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class PropertyEquals {
  private static final Log log = LogFactory.getLog(PropertyEquals.class);
  
  private boolean stopOnMismatch = true;
  
  /**
   * @return flag
   */
  public boolean getStopOnMismatch() {
    return this.stopOnMismatch;
  }
  
  /**
   * If true, comparison will stop and fail on finding the first unequal property. If false, comparison will go on to
   * log all unequal properties and then fail.
   * 
   * @param stopOnMismatch
   *          flag
   */
  public void setStopOnMismatch(final boolean stopOnMismatch) {
    this.stopOnMismatch = stopOnMismatch;
  }
  
  /**
   * Compare all bean properties of Objects a and b. Either the first or all mismatches will be logged at level DEBUG,
   * depending on the {@link #setStopOnMismatch(boolean) stopOnMismatch} flag.
   * 
   * @param a
   *          object
   * @param b
   *          object
   * @return true if all properties are equal, false otherwise.
   * @throws IllegalArgumentException
   *           if the two objects do not have the same set of properties
   */
  public boolean equals(final Object a, final Object b) {
    try {
      return equals(PropertyUtils.describe(a).keySet(), a, b);
    } catch (final Exception e) {
      throw wrapException(e);
    }
  }
  
  protected RuntimeException wrapException(final Exception e) {
    return new RuntimeException("Unexpected error while comparing properties", e);
  }
  
  protected boolean equals(final Set propertyNames, final Object a, final Object b) {
    if (a == b) {
      return true;
    } else if ((a == null) || (b == null)) {
      return false;
    }
    
    final Iterator iter = propertyNames.iterator();
    boolean allMatched = true;
    while (iter.hasNext()) {
      final String propertyName = (String) iter.next();
      Object propertyA;
      Object propertyB;
      boolean matched = true;
      try {
        try {
          propertyA = PropertyUtils.getProperty(a, propertyName);
        } catch (final NoSuchMethodException e) {
          throw new IllegalArgumentException("Object A (" + a.toString() + ") does not have property '" + propertyName
              + "'");
        }
        try {
          propertyB = PropertyUtils.getProperty(b, propertyName);
        } catch (final NoSuchMethodException e) {
          throw new IllegalArgumentException("Object B (" + b.toString() + ") does not have property '" + propertyName
              + "'");
        }
      } catch (final IllegalArgumentException e) {
        throw e;
      } catch (final Exception e) {
        throw wrapException(e);
      }
      if (propertyA == propertyB) {
        continue;
      } else if ((propertyA == null) || (propertyB == null)) {
        matched = false;
      } else if (!propertyA.equals(propertyB)) {
        matched = false;
      }
      if (!matched) {
        reportMismatch(propertyName, propertyA, propertyB);
        allMatched = false;
        if (this.stopOnMismatch) {
          break;
        }
      }
    }
    
    return allMatched;
  }
  
  protected void reportMismatch(final String propertyName, final Object propertyA, final Object propertyB) {
    log.debug("Property '" + propertyName + "' did not match!");
    log.debug("    Value in object A: '" + propertyA + "'");
    log.debug("    Value in object B: '" + propertyB + "'");
  }
}
