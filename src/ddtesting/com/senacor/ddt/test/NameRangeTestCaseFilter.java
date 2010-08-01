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

package com.senacor.ddt.test;

import com.senacor.ddt.util.ParamChecker;

/**
 * A test case filter that implements a range bounded by a first and last test case name. When iterating through the
 * tests given, the first test allowed will be the one named in <code>firstTestCaseName</code>. All following tests will
 * be allowed, up to and including the one named in <code>lastTestCaseName</code>. Either parameter may be null, which
 * will make the range unbounded on that end.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class NameRangeTestCaseFilter implements TestCaseFilter {
  private final String first;
  
  private final String last;
  
  private boolean withinAllowedRange;
  
  /**
   * @param firstTestCaseName
   *          The first test case name to allow. If null, then the range will be unbounded on the left side (all test
   *          cases up to and including the one named in <code>lastTestCaseName</code> will be allowed).
   * @param lastTestCaseName
   *          The last test case name to allow. If null, then the range will be unbounded on the right side (all test
   *          cases from and including the one named in <code>firstTestCaseName</code> will be allowed).
   */
  public NameRangeTestCaseFilter(final String firstTestCaseName, final String lastTestCaseName) {
    ParamChecker.require("At least one of first and last testCaseName must be set", (firstTestCaseName != null)
        || (lastTestCaseName != null));
    if ("".equals(firstTestCaseName)) {
      this.first = null;
    } else {
      this.first = firstTestCaseName;
    }
    if ("".equals(lastTestCaseName)) {
      this.last = null;
    } else {
      this.last = lastTestCaseName;
    }
    
    // if no first name is given (i.e. the left side of the range is open) then we are within the
    // allowed range
    // right from the beginning. otherwise the allowed range begins when the test case with the
    // firstName is reached.
    if ((this.first == null) || (this.first.trim().length() == 0)) {
      this.withinAllowedRange = true;
    } else {
      this.withinAllowedRange = false;
    }
  }
  
  /**
   * @see com.senacor.ddt.test.TestCaseFilter#isTestCaseAllowedToRun(com.senacor.ddt.test.TestCaseData)
   */
  public boolean isTestCaseAllowedToRun(final TestCaseData tcd) {
    boolean allowedToRun;
    if (this.withinAllowedRange) {
      allowedToRun = true;
      
      // if this matches the last test case name, we're at the end of the allowed range. set it but
      // still allow the current test
      // to run
      if (tcd.getTestCaseName().equals(this.last)) {
        this.withinAllowedRange = false;
      }
    } else {
      // if this matches the first test case name, we're at the beginning of the allowed range. set
      // it and allow the current test
      // to run
      if (tcd.getTestCaseName().equals(this.first)) {
        if (!this.first.equals(this.last)) {
          // only activate the range if there actually is one
          // if last equals first, then we want only this single test case to go through and not any
          // larger range.
          this.withinAllowedRange = true;
        }
        allowedToRun = true;
      } else {
        allowedToRun = false;
      }
    }
    
    return allowedToRun;
  }
}
