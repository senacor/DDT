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

import com.senacor.ddt.util.ParamChecker;

/**
 * A legacy report message.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 * @deprecated This works, but is currently not well-supported. The reporting subsystem will be significantly refactored
 *             and improved in a future release.
 */
public class ReportMessage implements Comparable {
  private final ReportLevel level;
  
  private final String message;
  
  /**
   * Constructor.
   * 
   * @param level
   * @param message
   */
  public ReportMessage(final ReportLevel level, final String message) {
    ParamChecker.notNull("level", level);
    ParamChecker.notNull("message", message);
    this.level = level;
    this.message = message;
  }
  
  /**
   * @return the level
   */
  public ReportLevel getLevel() {
    return this.level;
  }
  
  /**
   * @return the message
   */
  public String getMessage() {
    return this.message;
  }
  
  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final Object other) {
    final ReportMessage that = (ReportMessage) other;
    int result = this.level.compareTo(that.level);
    if (result == 0) {
      result = this.message.compareTo(that.message);
    }
    
    return result;
  }
}
