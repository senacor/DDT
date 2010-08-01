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

package com.senacor.ddt.test.report;

/**
 * The level of a report message.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 * @deprecated This works, but is currently not well-supported. The reporting subsystem will be significantly refactored
 *             and improved in a future release.
 */
public class ReportLevel implements Comparable {
  /**
   * Red
   */
  public static final ReportLevel RED = new ReportLevel((short) 0);
  
  /**
   * Yellow
   */
  public static final ReportLevel YELLOW = new ReportLevel((short) 1);
  
  /**
   * Green
   */
  public static final ReportLevel GREEN = new ReportLevel((short) 2);
  
  private final short level;
  
  private ReportLevel(final short level) {
    this.level = level;
  }
  
  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return getLevel();
  }
  
  /**
   * @return the level
   */
  public short getLevel() {
    return this.level;
  }
  
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other == null) {
      return false;
    } else if (other instanceof ReportLevel) {
      return getLevel() == ((ReportLevel) other).getLevel();
    } else {
      return false;
    }
  }
  
  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final Object other) {
    final ReportLevel that = (ReportLevel) other;
    if (this.level < that.level) {
      return -1;
    } else if (this.level == that.level) {
      return 0;
    } else if (this.level > that.level) {
      return +1;
    } else {
      throw new AssertionError("Logic failed - this is the impossible case!");
    }
  }
}
